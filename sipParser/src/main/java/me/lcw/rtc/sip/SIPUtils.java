package me.lcw.rtc.sip;

public class SIPUtils {
  private SIPUtils() {}
  public static String leftTrim(String value) {
    int count = 0;
    while(Character.isWhitespace(value.charAt(count))) {
      count++;
    }
    return value.substring(count);
  }
}
