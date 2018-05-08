package me.lcw.rtc.sdp;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lcw.rtc.sdp.SDPProtocol.SDPAttribute;
import me.lcw.rtc.sdp.SDPProtocol.SDPConnection;
import me.lcw.rtc.sdp.SDPProtocol.SDPMedia;
import me.lcw.rtc.sdp.SDPProtocol.SDPOrigin;
import me.lcw.rtc.sdp.SDPProtocol.SDPSession;
import me.lcw.rtc.sdp.SDPProtocol.SDPTime;
import me.lcw.rtc.sdp.SDPProtocol.SDPVersion;

public class SDP {


  private final SDPVersion version;
  private final SDPOrigin origin;
  private final SDPSession session;
  private final SDPTime time;
  private final SDPConnection connection;
  private final List<SDPAttribute> sessionAttributes;
  private final Set<SDPMedia> media;
  private final Map<SDPMedia, List<SDPAttribute>> mediaAttributes;
  private final Map<SDPMedia, List<SDPConnection>> mediaConnection;
  private final String sdpString;

  SDP(SDPVersion version, SDPOrigin origin, SDPSession session, SDPTime time, SDPConnection connection, List<SDPAttribute> sessionAttributes,
      Set<SDPMedia> media, Map<SDPMedia, List<SDPAttribute>> mediaAttributes, Map<SDPMedia, List<SDPConnection>> mediaConnection) {
    this.version = version;
    this.origin = origin;
    this.session = session;
    this.time = time;
    this.connection = connection;
    this.sessionAttributes = Collections.unmodifiableList(sessionAttributes);
    this.media = Collections.unmodifiableSet(media);
    this.mediaAttributes = Collections.unmodifiableMap(mediaAttributes);
    this.mediaConnection = Collections.unmodifiableMap(mediaConnection);
    sdpString = SDPProtocol.SDPToString(version, origin, session, time, connection, sessionAttributes, media, mediaAttributes, mediaConnection);
  }


  public SDPVersion getVersion() {
    return version;
  }

  public SDPOrigin getOrigin() {
    return origin;
  }

  public SDPSession getSession() {
    return session;
  }

  public SDPTime getTime() {
    return time;
  }

  public SDPConnection getConnection() {
    return connection;
  }

  public List<SDPAttribute> getSessionAttributes() {
    return sessionAttributes;
  }

  public Set<SDPMedia> getMedia() {
    return media;
  }

  public Map<SDPMedia, List<SDPAttribute>> getMediaAttributes() {
    return mediaAttributes;
  }

  public Map<SDPMedia, List<SDPConnection>> getMediaConnection() {
    return mediaConnection;
  }

  public String getSdpString() {
    return sdpString;
  }

  public ByteBuffer getBytes() {
    return ByteBuffer.wrap(sdpString.getBytes());
  }

  @Override
  public String toString() {
    return sdpString;
  }

  @Override
  public int hashCode() {
    return sdpString.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof SDP) {
      if(o.hashCode() == hashCode() && o.toString().equals(toString())) {
        return true;
      }
    }
    return false;
  }

}
