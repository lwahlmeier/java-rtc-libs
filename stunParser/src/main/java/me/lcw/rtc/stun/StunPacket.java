package me.lcw.rtc.stun;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class StunPacket {
  private final ByteBuffer buf;

  public StunPacket(final ByteBuffer buf) throws StunProtocolException{
    this.buf = buf.slice().asReadOnlyBuffer();
    if(buf.getInt(4) != 0x2112a442) {
      throw new StunProtocolException("Not a valid stun packet!");
    }
    int size = buf.getShort(2)&0xffff;
    if(buf.remaining() != size+20) {
      throw new StunProtocolException("Not a valid stun packet or bad size!");
    }
    checkAttributes(); 
  }

  private void checkAttributes() throws StunProtocolException {
    try {
      ByteBuffer tmpbb = buf.duplicate();
      tmpbb.position(20);
      while(tmpbb.hasRemaining()) {
        int type = tmpbb.getShort() & 0xffff;
        int size = tmpbb.getShort() & 0xffff;
        tmpbb.position((tmpbb.position() + size + 3) & ~3);
        StunAttribute.fromValue(type);
      }
    } catch(Exception e) {
      throw new StunProtocolException(e);
    }
  }

  private ByteBuffer getAttribute(StunAttribute attr) {
    ByteBuffer tmpbb = buf.duplicate();
    tmpbb.position(20);
    while(tmpbb.hasRemaining()) {
      int type = tmpbb.getShort() & 0xffff;
      int size = tmpbb.getShort() & 0xffff;
      StunAttribute fa = StunAttribute.fromValue(type);
      if(fa == attr) {
        ByteBuffer bb = tmpbb.slice();
        bb.limit(size);
        return bb;
      }
      tmpbb.position((tmpbb.position() + size + 3) & ~3);
    }
    return StunUtils.EMPTY_BB;
  }

  public ByteBuffer getBytes() {
    return this.buf.asReadOnlyBuffer();
  }
  
  public List<StunAttribute> getAttributes() {
    List<StunAttribute> sal = new ArrayList<>();
    ByteBuffer tmpbb = buf.duplicate();
    tmpbb.position(20);
    while(tmpbb.hasRemaining()) {
      int type = tmpbb.getShort() & 0xffff;
      int size = tmpbb.getShort() & 0xffff;
      StunAttribute fa = StunAttribute.fromValue(type);
      sal.add(fa);
      tmpbb.position((tmpbb.position() + size + 3) & ~3);
    }
    return sal;
  }

  public StunPacketBuilder createBuilder() {
    StunPacketBuilder builder = new StunPacketBuilder();
    builder.setTxID(getTxID());
    ByteBuffer tmpbb = buf.duplicate();
    tmpbb.position(20);
    while(tmpbb.hasRemaining()) {
      int type = tmpbb.getShort() & 0xffff;
      int size = tmpbb.getShort() & 0xffff;
      StunAttribute fa = StunAttribute.fromValue(type);
      ByteBuffer bb = tmpbb.slice();
      bb.limit(size);
      builder.setAttribute(fa, bb);
      tmpbb.position((tmpbb.position() + size + 3) & ~3);
    }
    return builder;
  }

  public StunMessageType getMessageType() {
    switch(buf.getShort(0)) {
    case 0x0001:
      return StunMessageType.REQUEST;
    case 0x0101:
      return StunMessageType.SUCCESS;
    case 0x0111:
      return StunMessageType.FAILURE;
    case 0x0011:
      return StunMessageType.INDICATION;
    default:
      throw new IllegalStateException("stun parse error");
    }
  }

  public TransactionID getTxID() {
    byte[] ba = new byte[12];
    ByteBuffer bb = buf.duplicate();
    bb.position(8);
    bb.get(ba);
    return new TransactionID(ba);
  }

  public boolean hasAddress() {
    return getAttribute(StunAttribute.MAPPED_ADDRESS).hasRemaining() || getAttribute(StunAttribute.XOR_MAPPED_ADDRESS).hasRemaining();
  }

  public InetSocketAddress getAddress() {
    boolean xor = false;
    ByteBuffer bb = getAttribute(StunAttribute.MAPPED_ADDRESS);
    byte family;
    int port;
    if(!bb.hasRemaining()) {
      bb = getAttribute(StunAttribute.XOR_MAPPED_ADDRESS);
      if(!bb.hasRemaining()) {
        throw new IllegalStateException("No Mapped Address found!");
      }
      xor = true;
      bb.get();
      family = bb.get();
      port = (bb.getShort() ^ StunUtils.STUN_SHORT_MAGIC) & 0xffff;
    } else {
      bb.get();
      family = bb.get();
      port = bb.getShort()&0xffff;
    }

    byte[] addr;
    if(family == 1) {
      addr = new byte[4];
    } else if(family == 2) {
      addr = new byte[16];
    } else {
      throw new IllegalArgumentException("Bad ip family!:"+family);
    }
    bb.get(addr);
    try {
      if(xor) {
        return new InetSocketAddress(InetAddress.getByAddress(getTxID().unmaskAddress(addr)), port);
      } else {
        return new InetSocketAddress( InetAddress.getByAddress(addr), port);
      }
    } catch(UnknownHostException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public ByteBuffer getUsername() {
    ByteBuffer bb = getAttribute(StunAttribute.USERNAME);
    if(!bb.hasRemaining()) {
      throw new IllegalStateException("No Username found!");
    }
    return bb;
  }

  public ByteBuffer getIceControlled() {
    ByteBuffer bb = getAttribute(StunAttribute.ICE_CONTROLLED);
    if(!bb.hasRemaining()) {
      throw new IllegalStateException("No ICE_CONTROLLED found!");
    }
    return bb;
  }

  public ByteBuffer getSoftware() {
    ByteBuffer bb = getAttribute(StunAttribute.SOFTWARE);
    if(!bb.hasRemaining()) {
      throw new IllegalStateException("No Software found!");
    }
    return bb;
  }

  public boolean hasFingerPrint() {
    ByteBuffer bb = getAttribute(StunAttribute.FINGERPRINT);
    if(!bb.hasRemaining()) {
      return false;
    }
    return true;
  }

  public int getFingerPrint() {
    ByteBuffer bb = getAttribute(StunAttribute.FINGERPRINT);
    if(!bb.hasRemaining()) {
      throw new IllegalStateException("No Fingerprint found!");
    }
    return bb.getInt();
  }

  public boolean hasMessageIntegerity() {
    ByteBuffer bb = getAttribute(StunAttribute.MESSAGE_INTEGRITY);
    if(!bb.hasRemaining()) {
      return false;
    }
    return true;
  }

  public ByteBuffer getMessageIntegerity() {
    ByteBuffer bb = getAttribute(StunAttribute.MESSAGE_INTEGRITY);
    if(!bb.hasRemaining()) {
      throw new IllegalStateException("No MessageIntegerity found!");
    }
    return bb;
  }

  public ByteBuffer getPriority() {
    ByteBuffer bb = getAttribute(StunAttribute.PRIORITY);
    if(!bb.hasRemaining()) {
      throw new IllegalStateException("No Priority found!");
    }
    return bb;
  }

}
