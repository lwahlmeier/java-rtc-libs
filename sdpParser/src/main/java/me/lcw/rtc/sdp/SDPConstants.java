package me.lcw.rtc.sdp;

import me.lcw.rtc.sdp.SDPProtocol.SDPSession;
import me.lcw.rtc.sdp.SDPProtocol.SDPTime;
import me.lcw.rtc.sdp.SDPProtocol.SDPVersion;

public class SDPConstants {
  
  public static final String SDP_OWNER   = "o=- 112233112233 10 IN IP4 127.0.0.1";
  public static final String SDP_AUDIO_MEDIA = "m=audio {PORT} {TRANSPORTS} {CODEC}";
  public static final String SDP_CONN_DATA = "c=IN IP4 {IP}";
  public static final String SDP_RTCP_MUX  = "a=rtcp-mux";
  public static final String SDP_DIRECTION = "a=sendrecv";
  public static final String SDP_ACODEC    = "a=rtpmap:{ID} {NAME}/{RATE}";
  
  public static final SDPVersion SDP_VERSION = new SDPVersion(0);
  public static final SDPSession SDP_SESSION = new SDPSession("-");
  public static final SDPTime SDP_TIME = new SDPTime(0L,0L);

  public static enum SDPParameter {
    Version("v"),
    Origin("o"),
    SessionName("s"),
    SessionInformation("i"),
    URIDescription("u"),
    Email("e"),
    PhoneNumber("p"),
    Connection("c"),
    Bandwidth("b"),
    TimeZone("z"),
    Key("k"),
    Attribute("a");
    
    private final String sh;
    SDPParameter(String sh) {
      this.sh = sh;
    }
    
    public String getShortHand() {
      return sh;
    }
  }
  
  public static class SDPLine {
    public SDPLine(SDPParameter sdpP, String info) {
      
    }
  }
  
  public static enum SDPProtocolType {
    UDP, TCP, TLS, RTP, SAVPF
  }
  
  public static enum AudioCodec {
    PCMU(0, 8000), 
    PCMA(8, 8000), 
    G722(9, 8000);
    
    
    private final int id;
    private final int rate;
    AudioCodec(int id, int rate) {
      this.id = id;
      this.rate = rate;
    }
    
    public int getId() {
      return id;
    }
    
    public int getRate() {
      return rate;
    }
  }
}
