package me.lcw.rtc.sdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import me.lcw.rtc.sdp.SDP;
import me.lcw.rtc.sdp.SDPProtocol;
import me.lcw.rtc.sdp.SDPProtocol.SDPOrigin;
import me.lcw.rtc.sdp.SDPProtocol.SDPParsingException;
import me.lcw.rtc.sdp.SDPProtocol.SDPVersion;

public class SDPTest {
  public static final String exp1 = "v=0\r\no=John 0844526 2890844526 IN IP4 172.22.1.102\r\ns=-\r\nc=IN IP4 172.22.1.102\r\nt=0 0\r\nm=audio 6000 RTP/AVP 97 98\r\na=rtpmap:97 AMR/16000/1\r\na=rtpmap:98 AMR-WB/8000/1\r\nm=video 49172 RTP/AVP 32\r\na=rtpmap:32 MPV/90000\r\n";
  public static final String exp2 = "v=0\r\nm=audio 5004 RTP/AVP 0\r\na=rtpmap:0 PCMU/8000/1\r\n";
  public static final String exp3 = "v=0\r\nm=audio 0 RTP/AVP 97\r\na=rtpmap:97 speex/16000\r\na=fmtp:97 mode=\"10,any\"\r\n";
  public static final String exp4 = "v=0\r\no=jdoe 2890844526 2890842807 IN IP4 10.47.16.5\r\ns=SDP Seminar\r\nc=IN IP4 224.2.17.12/127\r\nt=2873397496 2873404696\r\na=recvonly\r\nm=audio 49170 RTP/AVP 0\r\nm=video 51372 RTP/AVP 99\r\na=rtpmap:99 h263-1998/90000\r\n";
  public static final List<String> ALLSDP = Arrays.asList(new String[]{exp1,exp2,exp3,exp4});
  public static final String SDPFULL = "v=0\r\no=jdoe 2890844526 2890842807 IN IP4 10.47.16.5\r\ns=SDP Seminar\r\ni=A Seminar on the session description protocol\r\nu=http://www.example.com/seminars/sdp.pdf\r\ne=j.doe@example.com (Jane Doe)\r\nc=IN IP4 224.2.17.12/127\r\nt=2873397496 2873404696\r\na=recvonly\r\nm=audio 49170 RTP/AVP 0\r\nm=video 51372 RTP/AVP 99\r\na=rtpmap:99 h263-1998/90000\r\n";
  public static String COMPLEX_SDP1;
  
  @BeforeClass
  public static void load() {
    InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("complex1.sdp");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] ba = new byte[4096];
    int size = 0;
    try {
      while((size = is.read(ba)) > 0) {
        baos.write(ba, 0, size);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);  
    } finally {
      try {
        is.close();
      } catch (IOException e) {
      }
    }
    COMPLEX_SDP1 = baos.toString();
  }
  
  @Test
  public void sdpParseComplex1() throws Exception {
    SDP sdp = SDPProtocol.SDPFromString(COMPLEX_SDP1);
//    System.out.println(COMPLEX_SDP1.replaceAll("\n", "\r\n").replaceAll("\r\n", "\\\\r\\\\n"));
//    System.out.println(sdp.toString().replaceAll("\r\n", "\\\\r\\\\n"));
    assertEquals(COMPLEX_SDP1.replaceAll("\n", "\r\n"), sdp.toString());
  }
  
  @Test
  public void sdpParseTest1() throws SDPParsingException {
    for(String exp: ALLSDP) {
      SDP sdp = SDPProtocol.SDPFromString(exp);
      System.out.println(exp);
      System.out.println(sdp.toString());
      assertEquals(exp, sdp.toString());
    }
  }
  
  @Test
  public void versionCompaire() throws SDPParsingException {
    SDPVersion v1 = SDPVersion.parseLine("v=100");
    SDPVersion v2 = SDPVersion.parseLine("v=100");
    SDPVersion v3 = SDPVersion.parseLine("v=10");
    assertEquals(v1, v2);
    assertFalse(v1.equals(v3));
    assertFalse(v3.equals(v1));
    assertFalse(v3.equals(new Object()));
    assertTrue(v1.hashCode() == v2.hashCode());
    assertTrue(v1.hashCode() != v3.hashCode());
  }
  
  @Test(expected=SDPParsingException.class)
  public void badVersion1() throws SDPParsingException {
    SDPVersion.parseLine("v=100b");
  }
  
  @Test(expected=SDPParsingException.class)
  public void badVersion2() throws SDPParsingException {
    SDPVersion.parseLine("a=100b");
  }
  
  
  
  
  @Test
  public void originCompaire() throws SDPParsingException {
    SDPOrigin v1 = SDPOrigin.parseLine("o=jdoe 2890844526 2890842807 IN IP4 10.47.16.5");
    SDPOrigin v2 = SDPOrigin.parseLine("o=jdoe 2890844526 2890842807 IN IP4 10.47.16.5");
    SDPOrigin v3 = SDPOrigin.parseLine("o=jdoe 2890844526 2890842807 IN IP4 10.47.16.2");
    assertEquals(v1, v2);
    assertFalse(v1.equals(v3));
    assertFalse(v3.equals(v1));
    assertFalse(v3.equals(new Object()));
    assertTrue(v1.hashCode() == v2.hashCode());
    assertTrue(v1.hashCode() != v3.hashCode());
  }
  
  @Test(expected=SDPParsingException.class)
  public void badOrigin1() throws SDPParsingException {
    SDPOrigin.parseLine("o=jdoe 2890844526 2890842807 IN IP410.47.16.2");
  }
  
  @Test(expected=SDPParsingException.class)
  public void badOrigin2() throws SDPParsingException {
    SDPOrigin.parseLine("a=100b");
  }
}
