package me.lcw.rtc.stun;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class StunPacketBuilder {
  private MessageType type;
  private TransactionID tid;
  private List<StunAttribute> attribs = new ArrayList<>();
  private List<ByteBuffer> attribBuffers = new ArrayList<>();

  public StunPacketBuilder() {
    this.type = MessageType.REQUEST;
    this.tid = StunUtils.generateTxID();
  }

  public StunPacketBuilder setType(final MessageType t) {
    this.type = t;
    return this;
  }

  public StunPacketBuilder setTxID(TransactionID txID) {
    this.tid = txID;
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

  public StunPacketBuilder setAttribute(StunAttribute attr, ByteBuffer bb) {
    attribs.add(attr);
    attribBuffers.add(bb.slice());
    return this;
  }

  public StunPacketBuilder setMappedAddress(InetSocketAddress isa) {
    if(isa.getAddress() == null) {
      throw new IllegalArgumentException();
    }
    return setMappedAddress(isa.getAddress().getAddress(), isa.getPort());
  }

  public StunPacketBuilder setMappedAddress(byte[] ip, int port) {
    return setMappedAddress(ByteBuffer.wrap(ip).getInt(), port);
  }

  public StunPacketBuilder setMappedAddress(int ip, int port) {
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
    bb.put((byte)1); // ipv4
    bb.putShort((short)(port));
    bb.putInt(ip);
    bb.position(0);
    setAttribute(StunAttribute.MAPPED_ADDRESS, bb);
    return this;

  }
  public StunPacketBuilder setXorMappedAddress(InetSocketAddress isa) {
    if(isa.getAddress() == null) {
      throw new IllegalArgumentException();
    }
    return setXorMappedAddress(isa.getAddress().getAddress(), isa.getPort());
  }
  public StunPacketBuilder setXorMappedAddress(byte[] ip, int port) {
    return setXorMappedAddress(ByteBuffer.wrap(ip).getInt(), port);
  }
  public StunPacketBuilder setXorMappedAddress(int ip, int port) {
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

    ByteBuffer bb = ByteBuffer.allocate(8);
    bb.put((byte)0); // reserved
    bb.put((byte)1); // ipv4
    bb.putShort((short)(port ^ StunUtils.STUN_SHORT_MAGIC));
    bb.putInt(ip ^ StunUtils.STUN_MAGIC);
    bb.position(0);
    setAttribute(StunAttribute.XOR_MAPPED_ADDRESS, bb);
    return this;
  }

  public StunPacketBuilder setUsername(ByteBuffer username) {
    setAttribute(StunAttribute.USERNAME, username);
    return this;
  }

  public StunPacket buildSigned(ByteBuffer key) {
    try {
      SecretKeySpec signingKey = new SecretKeySpec(key.array(), key.arrayOffset() + key.position(), key.limit() - key.position(), "HmacSHA1");
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(signingKey);
      ByteBuffer obb = build().getBytes();
      ByteBuffer nbb = ByteBuffer.allocate(obb.remaining() + 24 + 8);
      nbb.put(obb);

      mac.update(StunUtils.BBToBA(obb));
      nbb.putShort((short)StunAttribute.MESSAGE_INTEGRITY.bits);
      nbb.putShort((short)mac.getMacLength());
      nbb.put(mac.doFinal());

      CRC32 crc = new CRC32();
      crc.update(nbb.array(), nbb.arrayOffset(), nbb.position());

      nbb.putShort((short)StunAttribute.FINGERPRINT.bits);
      nbb.putShort((short)4);
      nbb.putInt(0x5354554e ^ (int)crc.getValue());
      nbb.putShort(2, (short)(nbb.position() - 20));
      nbb.flip();
      return new StunPacket(nbb);
    } catch(NoSuchAlgorithmException|InvalidKeyException e) {
      throw new IllegalStateException(e);
    }

  }

  public StunPacket build() {
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
        bb.put((byte)0);
      }
    }
    bb.flip();
    return new StunPacket(bb);
  }
}