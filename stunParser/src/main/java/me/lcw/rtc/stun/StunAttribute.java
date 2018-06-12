package me.lcw.rtc.stun;

public enum StunAttribute {
  MAPPED_ADDRESS(0x0001),
  USERNAME(0x0006),
  MESSAGE_INTEGRITY(0x0008),
  ERROR_CODE(0x0009),
  UNKNOWN_ATTRIBUTES(0x000a),
  REALM(0x0014),
  NONCE(0x0015),
  PRIORITY(0x0024),
  XOR_MAPPED_ADDRESS(0x0020),
  SOFTWARE(0x8022),
  ALTERNATE_SERVER(0x8023),
  FINGERPRINT(0x8028),
  USE_CANDIDATE(0x0025),
  ICE_CONTROLLED(0x8029),
  ICE_CONTROLLING(0x802A);

  public final int bits;
  private StunAttribute(int bits) {
    this.bits = bits;
  }

  public boolean isComprehensionOptional() {
    return (bits & 0x8000) != 0;
  }

  public static StunAttribute fromValue(int val) {
    switch(val) {
    case 0x0001:
      return StunAttribute.MAPPED_ADDRESS;
    case 0x0006:
      return StunAttribute.USERNAME;
    case 0x0008:
      return StunAttribute.MESSAGE_INTEGRITY;
    case 0x0009:
      return StunAttribute.ERROR_CODE;
    case 0x000a:
      return StunAttribute.UNKNOWN_ATTRIBUTES;
    case 0x0014:
      return StunAttribute.REALM;
    case 0x0015:
      return StunAttribute.NONCE;
    case 0x0024:
      return StunAttribute.PRIORITY;
    case 0x0020:
      return StunAttribute.XOR_MAPPED_ADDRESS;
    case 0x8022:
      return StunAttribute.SOFTWARE;
    case 0x8023:
      return StunAttribute.ALTERNATE_SERVER;
    case 0x8028:
      return StunAttribute.FINGERPRINT;
    case 0x0025:
      return StunAttribute.USE_CANDIDATE;
    case 0x8029:
      return StunAttribute.ICE_CONTROLLED;
    case 0x802A:
      return StunAttribute.ICE_CONTROLLING;
    default:
      if((val & 0x8000) == 0) {
        throw new IllegalStateException("Bad Attribute in data!");
      }
    }
    return UNKNOWN_ATTRIBUTES;
  }
}