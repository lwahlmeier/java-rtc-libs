package me.lcw.rtc.stun;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class StunPacketBuilder {
  private StunMessageType type;
  private TransactionID tid;
  private List<StunAttribute> attribs = new ArrayList<>();
  private List<ByteBuffer> attribBuffers = new ArrayList<>();
  private byte padding = 0x0;
  private boolean fingerprint = false;
  private byte[] key = null;

  public StunPacketBuilder() {
    this.type = StunMessageType.REQUEST;
    this.tid = StunUtils.generateTxID();
  }

  public StunPacketBuilder setType(final StunMessageType t) {
    this.type = t;
    return this;
  }

  public StunPacketBuilder setTxID(TransactionID txID) {
    this.tid = txID;
    return this;
  }

  public StunPacketBuilder removeAttribute(StunAttribute sa) {
    int x = attribs.indexOf(sa);
    attribs.remove(x);
    attribBuffers.remove(x);
    return this;
  }

  public StunPacketBuilder setPaddingByte(byte b) {
    padding = b;
    return this;
  }

  private ByteBuffer makeHeader(int len) {
    ByteBuffer header = ByteBuffer.allocate(20);
    header.putShort((short)type.bits);
    header.putShort((short)len); // length, to be filled in later
    header.putInt(StunUtils.STUN_MAGIC);
    header.put(tid.getByteBuffer());
    header.position(0);
    return header;
  }

  public StunPacketBuilder clearAllAttributes() {
    attribs.clear();
    attribBuffers.clear();
    return this;
  }

  public StunPacketBuilder enableFingerPrint() {
    fingerprint = true;
    return this;
  }

  public StunPacketBuilder disableFingerPrint() {
    fingerprint = false;
    return this;
  }

  public StunPacketBuilder setAttribute(StunAttribute attr, ByteBuffer bb) {
    attribs.add(attr);
    attribBuffers.add(bb.slice());
    return this;
  }

  public StunPacketBuilder setMappedAddress(InetSocketAddress isa) {
    if(isa == null || isa.getAddress() == null) {
      throw new IllegalArgumentException("Address can not be null!");
    }
    return setMappedAddress(isa.getAddress().getAddress(), isa.getPort());
  }

  public StunPacketBuilder setMappedAddress(byte[] ip, int port) {
    if(port > Short.MAX_VALUE*2) {
      throw new IllegalArgumentException("BadPort number!");
    }
    int pos = -1;
    for(int i=0; i<attribs.size(); i++) {
      if(attribs.get(i).equals(StunAttribute.MAPPED_ADDRESS)) {
        pos = i;
        break;
      }
    }
    if(pos >= 0) {
      attribs.remove(pos);
      attribBuffers.remove(pos);
    }

    ByteBuffer bb = ByteBuffer.allocate(8);
    bb.put((byte)0); // reserved
    if(ip.length == 16) {
      bb.put((byte)2); // ipv6
    } else {
      bb.put((byte)1); // ipv4
    }
    bb.putShort((short)(port));
    bb.put(ip);
    bb.position(0);
    setAttribute(StunAttribute.MAPPED_ADDRESS, bb);
    return this;
  }

  public StunPacketBuilder setXorMappedAddress(InetSocketAddress isa) {
    if(isa == null || isa.getAddress() == null) {
      throw new IllegalArgumentException("Address can not be null!");
    }
    return setXorMappedAddress(isa.getAddress().getAddress(), isa.getPort());
  }

  public StunPacketBuilder setXorMappedAddress(byte[] ip, int port) {
    if(port > Short.MAX_VALUE*2) {
      throw new IllegalArgumentException("BadPort number!");
    }
    int pos = -1;
    for(int i=0; i<attribs.size(); i++) {
      if(attribs.get(i).equals(StunAttribute.XOR_MAPPED_ADDRESS)) {
        pos = i;
        break;
      }
    }
    if(pos >= 0) {
      attribs.remove(pos);
      attribBuffers.remove(pos);
    }

    ByteBuffer bb = ByteBuffer.allocate(2+ip.length+2);
    bb.put((byte)0); // reserved
    if(ip.length == 16) {
      bb.put((byte)2); // ipv6
    } else {
      bb.put((byte)1); // ipv4
    }
    bb.putShort((short)(port ^ StunUtils.STUN_SHORT_MAGIC));
    bb.put(StunUtils.unmaskAddress(this.tid, ip));
    bb.position(0);
    setAttribute(StunAttribute.XOR_MAPPED_ADDRESS, bb);
    return this;
  }

  public StunPacketBuilder setUsername(ByteBuffer username) {
    setAttribute(StunAttribute.USERNAME, username);
    return this;
  }
  
  public StunPacketBuilder setKey(byte[] ba) {
    key = ba;
    return this;
  }

  public StunPacket build() throws StunProtocolException {
    int size = 20;
    for(ByteBuffer bb: attribBuffers) {
      size += bb.remaining() + 4;
      size = (size + 3) & ~3;
    }
    ByteBuffer bb = ByteBuffer.allocate(size);
    bb.put(makeHeader(size - 20));
    for(int i=0; i<attribs.size(); i++) {
      bb.putShort((short)attribs.get(i).bits);
      bb.putShort((short)attribBuffers.get(i).remaining());
      bb.put(attribBuffers.get(i).duplicate());
      while((bb.position() & 3) != 0) {
        bb.put(padding);
      }
    }
    bb.flip();
    if(key != null) {
      bb = StunUtils.addMessageIntegerity(new StunPacket(bb), key).getBytes();
    }
    if(this.fingerprint) {
      return StunUtils.addFingerPrint(new StunPacket(bb));
    } else {
      return new StunPacket(bb);
    }
  }
}