package me.lcw.rtc.stun;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TransactionID {
    private final byte[] tid_ba;
    private final int tid;

    protected TransactionID(byte[] ba) {
        if(ba.length != 12) {
            throw new IllegalArgumentException("Invalid TransactionID size: "+ba.length);
        }
        this.tid_ba = ba;
        tid = StunUtils.getInt(0, ba);
    }

    public TransactionID(int tid, int tid1, int tid2) {
        tid_ba = new byte[12];
        ByteBuffer.wrap(tid_ba).putInt(tid).putInt(tid1).putInt(tid2);
        this.tid = tid;
    }
    
    public byte[] unmaskAddress(byte[] address) {
      return StunUtils.unmaskAddress(this, address);
    }

    public ByteBuffer getByteBuffer() {
        return ByteBuffer.wrap(tid_ba).asReadOnlyBuffer();
    }
    
    public byte[] getArray() {
      byte[] nba = new byte[12];
      System.arraycopy(tid_ba, 0, nba, 0, 12);
      return nba;
    }

    @Override
    public int hashCode() {
        return tid;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TransactionID) {
            return Arrays.equals(tid_ba, ((TransactionID)o).tid_ba);
        }
        return false;
    }
    
}
