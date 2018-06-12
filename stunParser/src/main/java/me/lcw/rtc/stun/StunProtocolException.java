package me.lcw.rtc.stun;

public class StunProtocolException extends Exception {

  private static final long serialVersionUID = 9152822370299419342L;

  StunProtocolException(Throwable t) {
    super(t);
  }
  StunProtocolException(String msg) {
    super(msg);
  }
  StunProtocolException(String msg, Throwable t) {
    super(msg, t);
  }
}
