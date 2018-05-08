package me.lcw.rtc.sdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

public class SDPBuilder {
  
  private SDPVersion version = SDPConstants.SDP_VERSION;
  private SDPOrigin origin = null;
  private SDPSession session = null;
  private SDPTime time = null;
  private SDPConnection connection = null;
  private List<SDPAttribute> sessionAttributes = new ArrayList<>();
  private Set<SDPMedia> media = new LinkedHashSet<>();
  private Map<SDPMedia, List<SDPAttribute>> mediaAttributes = new HashMap<>();
  private Map<SDPMedia, List<SDPConnection>> mediaConnection = new HashMap<>();
  
  public SDPBuilder setVersion(SDPVersion ver) {
    this.version = ver;
    return this;
  }
  
  public SDPBuilder setOrigin(SDPOrigin origin) {
    this.origin = origin;
    return this;
  }
  
  public SDPBuilder setSession(SDPSession session) {
    this.session = session;
    return this;
  }
  
  public SDPBuilder setTime(SDPTime time) {
    this.time = time;
    return this;
  }
  
  public SDPBuilder setSessionConnection(SDPConnection connection) {
    this.connection = connection;
    return this;
  }
  
  public SDPBuilder addSessionAttribute(SDPAttribute attribute) {
    if(!sessionAttributes.contains(attribute)) {
      sessionAttributes.add(attribute);
    }
    return this;
  }
  public SDPBuilder removeSessionAttribute(SDPAttribute attribute) {
    while(sessionAttributes.remove(attribute)){}
    return this;
  }
  
  public SDPBuilder addMedia(SDPMedia media) {
    this.media.add(media);
    if(!this.mediaAttributes.containsKey(media)) {
      this.mediaAttributes.put(media, new ArrayList<>());
    }
    if(!this.mediaConnection.containsKey(media)) {
      this.mediaConnection.put(media, new ArrayList<>());
    }
    return this;
  }
  
  public SDPBuilder removeMedia(SDPMedia media) {
    this.media.remove(media);
    this.mediaAttributes.remove(media);
    this.mediaConnection.remove(media);
    return this;
  }
  
  public SDPBuilder addMediaAttribute(SDPMedia media, SDPAttribute attribute) {
    if(this.media.contains(media)) {
      List<SDPAttribute> ma = this.mediaAttributes.get(media);
      if(!ma.contains(attribute)) {
        ma.add(attribute);
      }
      return this;
    }
    throw new IllegalArgumentException("Media:"+media+", does not exist yet!");
  }
  
  public SDPBuilder removeMediaAttribute(SDPMedia media, SDPAttribute attribute) {
    if(this.media.contains(media)) {
      this.mediaAttributes.get(media).remove(attribute);
      return this;
    }
    throw new IllegalArgumentException("Media:"+media+", does not exist yet!");
  }
  
  public SDPBuilder addMediaConnection(SDPMedia media, SDPConnection connection) {
    if(this.media.contains(media)) {
      List<SDPConnection> mc = this.mediaConnection.get(media);
      if(!mc.contains(connection)) {
        mc.add(connection);
      }
      return this;
    }
    throw new IllegalArgumentException("Media:"+media+", does not exist yet!");
  }
  
  public SDPBuilder removeMediaConnection(SDPMedia media, SDPConnection connection) {
    if(this.media.contains(media)) {
      this.mediaConnection.get(media).remove(connection);
      return this;
    }
    throw new IllegalArgumentException("Media:"+media+", does not exist yet!");
  }
  
  public SDPBuilder clone() {
    SDPBuilder clone = new SDPBuilder();
    clone.setVersion(version);
    clone.setOrigin(origin);
    clone.setSession(session);
    clone.setTime(time);
    clone.setSessionConnection(connection);
    for(SDPMedia m: media) {
      clone.addMedia(m);
      List<SDPConnection> mc = mediaConnection.get(m);
      List<SDPAttribute> ma = mediaAttributes.get(m);
      for(SDPConnection c: mc) {
        clone.addMediaConnection(m, c);
      }
      for(SDPAttribute a: ma) {
        clone.addMediaAttribute(m, a);
      }
    }
    return clone;
  }
  
  public SDP build() {
    return new SDP(version, origin, session, time, connection, new ArrayList<>(sessionAttributes), new LinkedHashSet<>(media)
        , new HashMap<>(mediaAttributes), new HashMap<>(mediaConnection));
  }
  
  public static void main(String[] args) {
    System.out.println(new SDPBuilder().clone().build());
  }
  
}
