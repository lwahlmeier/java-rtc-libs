package me.lcw.rtc.sip;

public class SIPProtocolException extends Exception {

  private static final long serialVersionUID = 1500219278529961053L;
  
  public SIPProtocolException(Throwable t) {
    super(t);
  }
  public SIPProtocolException(String msg) {
    super(msg);
  }
  public SIPProtocolException(String msg, Throwable t) {
    super(msg, t);
  }
}
