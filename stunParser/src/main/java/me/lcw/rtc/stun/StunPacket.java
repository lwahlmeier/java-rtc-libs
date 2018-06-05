package me.lcw.rtc.stun;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class StunPacket {
    private final ByteBuffer buf;

    public StunPacket(final ByteBuffer buf) {
        if(buf.getInt(4) != 0x2112a442) {
            throw new IllegalStateException("stun parse error");
        }
        this.buf = buf.slice().asReadOnlyBuffer();
        checkAttributes(); 
    }

    private void checkAttributes() {
        ByteBuffer tmpbb = buf.duplicate();
        tmpbb.position(20);
        while(tmpbb.hasRemaining()) {
            int type = tmpbb.getShort() & 0xffff;
            int size = tmpbb.getShort() & 0xffff;
            tmpbb.position((tmpbb.position() + size + 3) & ~3);
            StunAttribute.fromValue(type);
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

    public StunPacketBuilder createBuilder() {
        StunPacketBuilder builder = new StunPacketBuilder();
        builder.setTxID(getTxID());
        return builder;
    }

    public MessageType getType() {
        switch(buf.getShort(0)) {
        case 0x0001:
            return MessageType.REQUEST;
        case 0x0101:
            return MessageType.SUCCESS;
        case 0x0111:
            return MessageType.FAILURE;
        case 0x0011:
            return MessageType.INDICATION;
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

}
