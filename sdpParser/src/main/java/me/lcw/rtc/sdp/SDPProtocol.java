package me.lcw.rtc.sdp;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SDPProtocol {
  
  public interface SDPLine {
    String toSDPLine();
  }

  public static class SDPVersion implements SDPLine {
    public static final String StartsWith = "v";
    public final int version;

    public SDPVersion(int v) {
      this.version = v;
    }
    
    public static SDPVersion parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          return new SDPVersion(Integer.parseInt(l2.substring(2)));
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPVersion", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPVersion");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+version;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPVersion) {
        if(((SDPVersion) o).version == version) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPOrigin implements SDPLine {
    public static final String StartsWith = "o";
    public final String userName;
    public final String sessionID;
    public final String version;
    public final String networkType;
    public final String addrType;
    public final String addressType;

    public SDPOrigin(String userName, String sessionID, String version, String networkType, String addrType, String addressType) {
      this.userName = userName;
      this.sessionID = sessionID;
      this.version = version;
      this.networkType = networkType;
      this.addrType = addrType;
      this.addressType = addressType;
    }
    
    public static SDPOrigin parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          String[] orign = l2.substring(2).split(" ");
          return new SDPOrigin(orign[0], orign[1], orign[2], orign[3], orign[4], orign[5]);
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+userName+" "+sessionID+" "+version+" "+networkType+" "+addrType+" "+addressType;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPOrigin) {
        if(((SDPOrigin) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPSession implements SDPLine {
    public static final String StartsWith = "s";

    public final String session;

    public SDPSession(String session) {
      this.session = session;
    }
    
    public static SDPSession parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          return new SDPSession(l2.substring(2));
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPSession", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPSession");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+session;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPSession) {
        if(((SDPSession) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPInformation implements SDPLine {
    public static final String StartsWith = "i";

    public final String info;

    public SDPInformation(String info) {
      this.info = info;
    }
    
    public static SDPInformation parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          return new SDPInformation(l2.substring(2));
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPInformation", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPInformation");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+info;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPInformation) {
        if(((SDPInformation) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPUri implements SDPLine {
    public static final String StartsWith = "u";

    public final URI uri;

    public SDPUri(URI uri) {
      this.uri = uri;
    }
    
    public static SDPUri parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          return new SDPUri(new URI(l2.substring(2)));
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPInformation", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPInformation");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+uri;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPUri) {
        if(((SDPUri) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPConnection implements SDPLine {
    public static final String StartsWith = "c";
    public final String networkType;
    public final String addressType;
    public final String address;

    public SDPConnection(String networkType, String addressType, String address) {
      this.networkType = networkType;
      this.addressType = addressType;
      this.address = address;
    }
    
    public static SDPConnection parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          String[] str = l2.substring(2).split(" ");
          return new SDPConnection(str[0], str[1], str[2]);
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+networkType+" "+addressType+" "+address;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPConnection) {
        if(((SDPConnection) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPTime implements SDPLine {
    public static final String StartsWith = "t";
    public final long start;
    public final long stop;

    public SDPTime(long start, long stop) {
      this.start = start;
      this.stop = stop;
    }
    
    public static SDPTime parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          String[] str = l2.substring(2).split(" ");
          return new SDPTime(Long.parseLong(str[0]), Long.parseLong(str[1]));
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+start+" "+stop;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPTime) {
        if(((SDPTime) o).start == start && ((SDPTime) o).stop == stop) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static class SDPMedia implements SDPLine {
    public static final String StartsWith = "m";
    
    public final String media;
    public final int port;
    public final String transport;
    public final String formats;

    public SDPMedia(String media, int port, String transport, String formats) {
      this.media = media;
      this.port = port;
      this.transport = transport;
      this.formats = formats;
    }
    
    public static SDPMedia parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          String[] str = l2.substring(2).split(" ", 4);
          return new SDPMedia(str[0], Integer.parseInt(str[1]), str[2], str[3]);
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+media+" "+port+" "+transport+" "+formats;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPMedia) {
        if(((SDPMedia) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  
  public static class SDPAttribute implements SDPLine {
    public static final String StartsWith = "a";
    
    public final String attribute;

    public SDPAttribute(String attribute) {
      this.attribute = attribute;
    }
    
    public static SDPAttribute parseLine(String line) throws SDPParsingException {
      String l2 = line.trim();
      if(l2.startsWith(StartsWith+"=")) {
        try {
          return new SDPAttribute(l2.substring(2));
        } catch(Exception e) {
          throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin", e);
        }
      }
      throw new SDPParsingException("Could not parse:"+line+" as SDPOrigin");
    }

    @Override
    public String toSDPLine() {
      return StartsWith+"="+attribute;
    }
    
    @Override
    public int hashCode() {
      return toSDPLine().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
      if(o instanceof SDPAttribute) {
        if(((SDPAttribute) o).toSDPLine().equals(toSDPLine())) {
          return true;
        }
      }
      return false;
    }
  }
  
  public static SDP SDPFromString(String sdpString) throws SDPParsingException {
    String[] lines = sdpString.split("\n");
    
    SDPBuilder sdpb = new SDPBuilder();
    SDPMedia lastMedia = null;
    for(String s: lines) {
      if(s.startsWith("v")) {
        sdpb.setVersion(SDPVersion.parseLine(s));
      } else if(s.startsWith("o")) { 
        sdpb.setOrigin(SDPOrigin.parseLine(s));
      } else if(s.startsWith("s")) {
        sdpb.setSession(SDPSession.parseLine(s));
      } else if(s.startsWith("t")) {
        sdpb.setTime(SDPTime.parseLine(s));
      } else if(s.startsWith("m")) {
        lastMedia = SDPMedia.parseLine(s);
        sdpb.addMedia(lastMedia);
      } else if(s.startsWith("a")) {
        if(lastMedia == null) {
          sdpb.addSessionAttribute(SDPAttribute.parseLine(s));  
        } else {
          sdpb.addMediaAttribute(lastMedia, SDPAttribute.parseLine(s));
        }
      } else if(s.startsWith("c")) {
        if(lastMedia == null) {
          sdpb.setSessionConnection(SDPConnection.parseLine(s));  
        } else {
          sdpb.addMediaConnection(lastMedia, SDPConnection.parseLine(s));
        }
      } else {
        System.out.println("Unknown Media:"+s);
      }
    }
    return sdpb.build();
  }
  
  public static String SDPToString(SDPVersion version, SDPOrigin origin, SDPSession session, SDPTime time, SDPConnection connection, List<SDPAttribute> sessionAttributes,
      Set<SDPMedia> media, Map<SDPMedia, List<SDPAttribute>> mediaAttributes, Map<SDPMedia, List<SDPConnection>> mediaConnection) {
    StringBuilder sb = new StringBuilder();
    sb.append(version.toSDPLine()).append("\r\n");
    if(origin != null) {
      sb.append(origin.toSDPLine()).append("\r\n");
    }
    if(session != null) {
      sb.append(session.toSDPLine()).append("\r\n");
    }
    if(connection != null) {
      sb.append(connection.toSDPLine()).append("\r\n");
    }
    if(time != null) {
      sb.append(time.toSDPLine()).append("\r\n");
    }
    if(sessionAttributes != null) {
      for(SDPAttribute a: sessionAttributes) {
        sb.append(a.toSDPLine()).append("\r\n");
      }
    }
    for(SDPMedia sdpm: media) {
      sb.append(sdpm.toSDPLine()).append("\r\n");
      List<SDPConnection> mc = mediaConnection.get(sdpm);
      List<SDPAttribute> ma = mediaAttributes.get(sdpm);
      for(SDPConnection c: mc) {
        sb.append(c.toSDPLine()).append("\r\n");  
      }
      for(SDPAttribute a: ma) {
        sb.append(a.toSDPLine()).append("\r\n");  
      }
    }
    return sb.toString();
  }
  
  public static class SDPParsingException extends Exception {
    private static final long serialVersionUID = -7401482087135317326L;
    
    public SDPParsingException() {
      super();
    }
    
    public SDPParsingException(String s) {
      super(s);
    }
    
    public SDPParsingException(String s, Throwable t) {
      super(s, t);
    }
    
    public SDPParsingException(Throwable t) {
      super(t);
    }
    
  }
}
