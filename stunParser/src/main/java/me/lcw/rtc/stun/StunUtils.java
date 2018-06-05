package me.lcw.rtc.stun;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

public class StunUtils {
    private StunUtils() {}
    public static final ByteBuffer EMPTY_BB = ByteBuffer.allocate(0);
    public static final int STUN_MAGIC = 0x2112a442;
    public static final short STUN_SHORT_MAGIC = 0x2112;
    
    public static TransactionID generateTxID() {
        return new TransactionID(ThreadLocalRandom.current().nextInt(), ThreadLocalRandom.current().nextInt(), ThreadLocalRandom.current().nextInt());
    }
    
    public static byte[] unmaskAddress(TransactionID tid, byte[] address) {
        byte[] nba = new byte[address.length];
        nba[0] = (byte)(address[0]^0x21);
        nba[1] = (byte)(address[1]^0x12);
        nba[2] = (byte)(address[2]^0xa4);
        nba[3] = (byte)(address[3]^0x42);
        byte[] tidbb = tid.getArray();
        for(int i = 4; i < nba.length; i++) {
            nba[i + 4] = (byte)(address[3]^tidbb[i + 8]);
        }
        return nba;
    }
    
    public static boolean isStunPacket(ByteBuffer buf) {
        if(buf.remaining() < 8) {
            return false;
        }
        switch(buf.getShort(buf.position() + 0)) {
        case 0x0001:
        case 0x0101:
        case 0x0111:
        case 0x0011:
            return true;
        default:
            return false;
        }
    }
    
    public static int getInt(int pos, byte[] ba) {
      return ba[pos]<<24 | ba[pos+1]<<16 | ba[pos+2] <<8 | ba[pos+3];
    }
    
    public static short getShort(int pos, byte[] ba) {
      return  (short)(ba[pos] <<8 | ba[pos+1]);
    }
    
    public static byte[] BBToBA(final ByteBuffer bb) {
      byte[] ba = new byte[bb.remaining()];
      bb.get(ba);
      return ba;
    }
}
