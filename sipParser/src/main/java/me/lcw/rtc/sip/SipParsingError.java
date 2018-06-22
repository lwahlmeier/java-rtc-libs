package me.lcw.rtc.sip;

public class SipParsingError extends Exception{
  
  private static final long serialVersionUID = -5981787163072976056L;
  
  public SipParsingError() {};
  public SipParsingError(String s) {
    super(s);
  };
  
  public SipParsingError(Throwable t) {
    super(t);
  };
  public SipParsingError(String s, Throwable t) {
    super(s, t);
  };

}
