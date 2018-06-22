package me.lcw.rtc.sip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SIPHeader {

  private final String rawHeaders;
  private final List<String> keys;
  private final List<String> values;

  private SIPHeader(final String rawHeaders, List<String> keys, List<String> values) {
    this.rawHeaders = rawHeaders;
    this.keys = Collections.unmodifiableList(new ArrayList<>(keys));
    this.values = Collections.unmodifiableList(new ArrayList<>(values));
  }

  public String getKey(String key) {
    int i=0;
    for(String s: keys) {
      if(s.equalsIgnoreCase(key)) {
        return values.get(i);
      }
      i++;
    }
    return null;
  }

  public List<String> getKeyList(String key) {
    List<String> rvalues = new ArrayList<>();
    int i=0;
    for(String s: keys) {
      if(s.equalsIgnoreCase(key)) {
        rvalues.add(values.get(i));
      }
      i++;
    }
    return rvalues;
  }

  public String getCallId() {
    return getKey(SIPConstants.SIP_HEADER_KEY_CALL_ID);
  }

  public String getTo() {
    return getKey(SIPConstants.SIP_HEADER_KEY_TO);
  }

  public String getFrom() {
    return getKey(SIPConstants.SIP_HEADER_KEY_FROM);
  }

  public String getCSeq() {
    return getKey(SIPConstants.SIP_HEADER_KEY_CALL_SEQUENCE);
  }

  public List<String> getVia() {
    return getKeyList(SIPConstants.SIP_HEADER_KEY_VIA);
  }

  @Override
  public int hashCode() {
    return rawHeaders.hashCode()+4;
  }

  @Override
  public String toString() {
    return this.rawHeaders;
  }

  @Override
  public boolean equals(Object o) {
    if(o == this) {
      return true;
    }
    if(o instanceof SIPHeader) {
      if(rawHeaders.equals(o.toString())) {
        return true;
      }
    }
    return false;
  }

  public static SIPHeader fromString(String headerString) throws SIPProtocolException {
    String rawHeaders;
    List<String> keys = new ArrayList<>();
    List<String> values = new ArrayList<>();
    if(headerString.substring(0, headerString.indexOf(SIPConstants.SIP_NEWLINE)).contains(SIPConstants.SIP_20)) {
      headerString = SIPUtils.leftTrim(headerString.substring(headerString.indexOf(SIPConstants.SIP_NEWLINE)));
    }
    if(headerString.endsWith(SIPConstants.SIP_DOUBLE_NEWLINE)) {
      rawHeaders = headerString.substring(0, headerString.length()-2);
    } else if(!headerString.endsWith(SIPConstants.SIP_NEWLINE)) { 
      rawHeaders = (headerString+SIPConstants.SIP_NEWLINE);
    } else {
      rawHeaders = SIPUtils.leftTrim(headerString);
    }

    String[] rows = headerString.split(SIPConstants.SIP_NEWLINE);
    String cid = null;
    String to = null;
    String from = null;
    String cseq = null;

    for(String h: rows) {
      if (h.isEmpty()) {
        continue;
      }
      int delim = h.indexOf(SIPConstants.SIP_KEY_VALUE_DELIMINATOR);
      if (delim < 0) {
        throw new SIPProtocolException("Header is missing key value delim: " + h);
      }
      String key = h.substring(0, delim).trim();
      String value = h.substring(delim+1).trim();
      switch(key) {
      case SIPConstants.SIP_HEADER_KEY_TO:
        if(to == null) {
          to = value;
        } else {
          throw new SIPProtocolException("Header has multipule To values!: "+to+":"+value );
        }
      case SIPConstants.SIP_HEADER_KEY_FROM:
        if(from == null) {
          from = value;
        } else {
          throw new SIPProtocolException("Header has multipule From values!: "+from+":"+value );
        }
      case SIPConstants.SIP_HEADER_KEY_CALL_ID:
        if(cid == null) {
          cid = value;
        } else {
          throw new SIPProtocolException("Header has multipule Call-ID values!: "+cid+":"+value );
        }
      case SIPConstants.SIP_HEADER_KEY_CALL_SEQUENCE:
        if(cseq == null) {
          cseq = value;
        } else {
          throw new SIPProtocolException("Header has multipule CSeq values!: "+cseq+":"+value );
        }
      default:

      }
      keys.add(key);
      values.add(value);
    }

    return new SIPHeader(rawHeaders, keys, values);
  }

}
