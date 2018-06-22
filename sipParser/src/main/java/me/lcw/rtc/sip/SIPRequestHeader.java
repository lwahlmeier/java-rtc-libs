package me.lcw.rtc.sip;

public class SIPRequestHeader {

  private final SIPRequest sr;
  private final String uri;
  private final String sip_version;
  private final String fullRequest;
  
  
  public SIPRequestHeader(SIPRequest sr, String uri, String sip_version) {
    this.sr = sr;
    this.uri = uri;
    this.sip_version = sip_version;
    this.fullRequest = sr+" "+uri+" "+sip_version;
  }
  
  @Override
  public int hashCode() {
    return fullRequest.hashCode()+4;
  }

  @Override
  public String toString() {
    return this.fullRequest;
  }

  @Override
  public boolean equals(Object o) {
    if(o == this) {
      return true;
    }
    if(o instanceof SIPRequestHeader) {
      if(fullRequest.equals(o.toString())) {
        return true;
      }
    }
    return false;
  }
  
  public static SIPRequestHeader fromString(final String header) {
    String s = header.trim();
    int np = s.indexOf(SIPConstants.SIP_NEWLINE);
    if(np != -1) {
      s = s.substring(0, np);
    }
    String[] ss = s.split(" ");
    return new SIPRequestHeader(SIPRequest.fromString(ss[0].trim()), ss[1].trim(), ss[2].trim());
  }
}
