package me.lcw.rtc.stun;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class StunUtils {
  private StunUtils() {}
  private static final String HEX = "0123456789ABCDEF";
  public static final ByteBuffer EMPTY_BB = ByteBuffer.allocate(0);
  public static final byte[] EMPTY_BA = new byte[0];
  public static final int STUN_MAGIC = 0x2112a442;
  public static final short STUN_SHORT_MAGIC = 0x2112;
  public static final int STUN_FINGERPRINT_MAGIC = 0x5354554e; 

  public static TransactionID generateTxID() {
    return new TransactionID(
        ThreadLocalRandom.current().nextInt(), 
        ThreadLocalRandom.current().nextInt(), 
        ThreadLocalRandom.current().nextInt()
        );
  }

  public static StunPacket addFingerPrint(final StunPacket sp) throws StunProtocolException {
    ByteBuffer obb = sp.getBytes();
    ByteBuffer nbb = ByteBuffer.allocate(obb.remaining() + 8);
    nbb.put(obb);
    nbb.putShort(2, (short)(obb.getShort(2)+8));
    CRC32 crc = new CRC32();
    crc.update(nbb.array(), nbb.arrayOffset(), nbb.position());
    nbb.putShort((short)StunAttribute.FINGERPRINT.bits);
    nbb.putShort((short)4);
    nbb.putInt(STUN_FINGERPRINT_MAGIC ^ (int)crc.getValue());
    nbb.flip();
    return new StunPacket(nbb);
  }

  public static boolean verifyFingerPrint(final StunPacket sp) {
    if(!sp.hasFingerPrint()) {
      return false;
    }
    ByteBuffer obb = sp.getBytes();
    int old_crc = sp.getFingerPrint();
    byte[] ba = new byte[obb.remaining()-8];
    obb.get(ba);
    CRC32 crc = new CRC32();
    crc.update(ba);
    int new_crc = STUN_FINGERPRINT_MAGIC ^ (int)crc.getValue();
    return new_crc == old_crc;
  }

  public static StunPacket addMessageIntegerity(final StunPacket sp, byte[] key) throws StunProtocolException {
    try {
      SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(signingKey);
      ByteBuffer obb = sp.getBytes();
      //Create new Buffer with extra space
      ByteBuffer nbb = ByteBuffer.allocate( obb.remaining()+24);
      //Add in old Buffer
      nbb.put(obb);
      //Update the length
      nbb.putShort(2, (short)(obb.getShort(2)+24));

      //Duplicate the buffer
      ByteBuffer bb2 = nbb.duplicate();
      //flip duplicated bugger to be read
      bb2.flip();

      //add new message-int attrib to the buffer
      nbb.putShort((short)StunAttribute.MESSAGE_INTEGRITY.bits);
      //add length
      nbb.putShort((short)mac.getMacLength());      
      //Do sign and add to buffer
      nbb.put(mac.doFinal(StunUtils.BBToBA(bb2)));
      //flip the buffer for use
      nbb.flip();
      return new StunPacket(nbb);
    } catch(StunProtocolException e) {
      throw e;
    } catch(Exception e) {
      throw new StunProtocolException(e);
    }
  }

  public static boolean verifyMessageIntegerity(final StunPacket sp, byte[] key) {
    if(!sp.hasMessageIntegerity()) {
      return false;
    }
    ByteBuffer sba = ByteBuffer.allocate(sp.getBytes().remaining());
    sba.put(sp.getBytes());
    sba.flip();

    int pos = 20;
    int offset = pos;
    while(pos < sba.remaining()) {
      StunAttribute sa = StunAttribute.fromValue(sba.getShort(pos)&0xffff);
      pos+=2;
      int sa_size = sba.getShort(pos)&0xffff;
      pos+=2;
      offset = pos;
      if(sa == StunAttribute.MESSAGE_INTEGRITY) {
        try {
          SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
          Mac mac = Mac.getInstance("HmacSHA1");
          mac.init(signingKey);
          sba.putShort(2, (short)offset);
          sba.limit(offset-4);
          byte[] sig = mac.doFinal(BBToBA(sba.duplicate()));
          sba.limit(sba.capacity());
          byte[] ba = new byte[sa_size];
          sba.position(offset);
          sba.get(ba);
          return Arrays.equals(ba, sig);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      pos+=sa_size;
      pos=pos +3 & ~3;
    }
    return false;
  }

  public static byte[] unmaskAddress(TransactionID tid, byte[] address) {
    byte[] nba = new byte[address.length];
    nba[0] = (byte)(address[0]^0x21);
    nba[1] = (byte)(address[1]^0x12);
    nba[2] = (byte)(address[2]^0xa4);
    nba[3] = (byte)(address[3]^0x42);
    byte[] tidbb = tid.getArray();
    for(int i = 4; i < nba.length; i++) {
      nba[i] = (byte)(address[i]^tidbb[i-4]);
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

  public static byte[] MD5(String md5) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      return md.digest(md5.getBytes());
    } catch (java.security.NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getHex(ByteBuffer bb) {
    final StringBuilder hex = new StringBuilder(2 * bb.remaining());
    for (int i=0; i<bb.remaining(); i++) {
      byte b = bb.get(i);
      hex.append(HEX.charAt((b & 0xF0) >> 4)).append(HEX.charAt((b & 0x0F)));
    }
    return hex.toString();
  }

  public static String getHex(byte[] raw) {
    final StringBuilder hex = new StringBuilder(2 * raw.length);
    for (final byte b : raw) {
      hex.append(HEX.charAt((b & 0xF0) >> 4)).append(HEX.charAt((b & 0x0F)));
    }
    return hex.toString();
  }
}
