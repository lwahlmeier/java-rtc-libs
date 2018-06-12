package me.lcw.rtc.stun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public class StunTests {
  
  public static final ByteBuffer SAMPLE_STUN_REQ1 = ByteBuffer.wrap(new byte[] {
      (byte)0x00,(byte)0x01,(byte)0x00,(byte)0x58
      ,(byte)0x21,(byte)0x12,(byte)0xa4,(byte)0x42
      ,(byte)0xb7,(byte)0xe7,(byte)0xa7,(byte)0x01
      ,(byte)0xbc,(byte)0x34,(byte)0xd6,(byte)0x86
      ,(byte)0xfa,(byte)0x87,(byte)0xdf,(byte)0xae 
      ,(byte)0x80,(byte)0x22,(byte)0x00,(byte)0x10
      ,(byte)0x53,(byte)0x54,(byte)0x55,(byte)0x4e
      ,(byte)0x20,(byte)0x74,(byte)0x65,(byte)0x73
      ,(byte)0x74,(byte)0x20,(byte)0x63,(byte)0x6c
      ,(byte)0x69,(byte)0x65,(byte)0x6e,(byte)0x74 
      ,(byte)0x00,(byte)0x24,(byte)0x00,(byte)0x04
      ,(byte)0x6e,(byte)0x00,(byte)0x01,(byte)0xff
      ,(byte)0x80,(byte)0x29,(byte)0x00,(byte)0x08
      ,(byte)0x93,(byte)0x2f,(byte)0xf9,(byte)0xb1
      ,(byte)0x51,(byte)0x26,(byte)0x3b,(byte)0x36
      ,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x09
      ,(byte)0x65,(byte)0x76,(byte)0x74,(byte)0x6a
      ,(byte)0x3a,(byte)0x68,(byte)0x36,(byte)0x76
      ,(byte)0x59,(byte)0x20,(byte)0x20,(byte)0x20
      ,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x14
      ,(byte)0x9a,(byte)0xea,(byte)0xa7,(byte)0x0c
      ,(byte)0xbf,(byte)0xd8,(byte)0xcb,(byte)0x56
      ,(byte)0x78,(byte)0x1e,(byte)0xf2,(byte)0xb5 
      ,(byte)0xb2,(byte)0xd3,(byte)0xf2,(byte)0x49
      ,(byte)0xc1,(byte)0xb5,(byte)0x71,(byte)0xa2 
      ,(byte)0x80,(byte)0x28,(byte)0x00,(byte)0x04
      ,(byte)0xe5,(byte)0x7a,(byte)0x3b,(byte)0xcf
  }).asReadOnlyBuffer();
  
  public static final ByteBuffer SAMPLE_STUN_RESP1 = ByteBuffer.wrap(new byte[] {
      (byte)0x01,(byte)0x01,(byte)0x00,(byte)0x3c
      ,(byte)0x21,(byte)0x12,(byte)0xa4,(byte)0x42
      ,(byte)0xb7,(byte)0xe7,(byte)0xa7,(byte)0x01
      ,(byte)0xbc,(byte)0x34,(byte)0xd6,(byte)0x86
      ,(byte)0xfa,(byte)0x87,(byte)0xdf,(byte)0xae
      ,(byte)0x80,(byte)0x22,(byte)0x00,(byte)0x0b
      ,(byte)0x74,(byte)0x65,(byte)0x73,(byte)0x74
      ,(byte)0x20,(byte)0x76,(byte)0x65,(byte)0x63
      ,(byte)0x74,(byte)0x6f,(byte)0x72,(byte)0x20
      ,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x08
      ,(byte)0x00,(byte)0x01,(byte)0xa1,(byte)0x47
      ,(byte)0xe1,(byte)0x12,(byte)0xa6,(byte)0x43
      ,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x14
      ,(byte)0x2b,(byte)0x91,(byte)0xf5,(byte)0x99
      ,(byte)0xfd,(byte)0x9e,(byte)0x90,(byte)0xc3
      ,(byte)0x8c,(byte)0x74,(byte)0x89,(byte)0xf9
      ,(byte)0x2a,(byte)0xf9,(byte)0xba,(byte)0x53
      ,(byte)0xf0,(byte)0x6b,(byte)0xe7,(byte)0xd7
      ,(byte)0x80,(byte)0x28,(byte)0x00,(byte)0x04
      ,(byte)0xc0,(byte)0x7d,(byte)0x4c,(byte)0x96
      });
  
  
  public static final ByteBuffer SAMPLE_STUN_RESP2 = ByteBuffer.wrap(new byte[] {
      (byte)0x01,(byte)0x01,(byte)0x00,(byte)0x48
      ,(byte)0x21,(byte)0x12,(byte)0xa4,(byte)0x42
      ,(byte)0xb7,(byte)0xe7,(byte)0xa7,(byte)0x01
      ,(byte)0xbc,(byte)0x34,(byte)0xd6,(byte)0x86
      ,(byte)0xfa,(byte)0x87,(byte)0xdf,(byte)0xae
      ,(byte)0x80,(byte)0x22,(byte)0x00,(byte)0x0b
      ,(byte)0x74,(byte)0x65,(byte)0x73,(byte)0x74
      ,(byte)0x20,(byte)0x76,(byte)0x65,(byte)0x63
      ,(byte)0x74,(byte)0x6f,(byte)0x72,(byte)0x20
      ,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x14
      ,(byte)0x00,(byte)0x02,(byte)0xa1,(byte)0x47
      ,(byte)0x01,(byte)0x13,(byte)0xa9,(byte)0xfa
      ,(byte)0xa5,(byte)0xd3,(byte)0xf1,(byte)0x79
      ,(byte)0xbc,(byte)0x25,(byte)0xf4,(byte)0xb5
      ,(byte)0xbe,(byte)0xd2,(byte)0xb9,(byte)0xd9
      ,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x14
      ,(byte)0xa3,(byte)0x82,(byte)0x95,(byte)0x4e
      ,(byte)0x4b,(byte)0xe6,(byte)0x7b,(byte)0xf1
      ,(byte)0x17,(byte)0x84,(byte)0xc9,(byte)0x7c
      ,(byte)0x82,(byte)0x92,(byte)0xc2,(byte)0x75
      ,(byte)0xbf,(byte)0xe3,(byte)0xed,(byte)0x41
      ,(byte)0x80,(byte)0x28,(byte)0x00,(byte)0x04
      ,(byte)0xc8,(byte)0xfb,(byte)0x0b,(byte)0x4c
  });
  
  public static final ByteBuffer SAMPLE_STUN_REQ2 = ByteBuffer.wrap(new byte[] {
      (byte)0x00,(byte)0x01,(byte)0x00,(byte)0x60
      ,(byte)0x21,(byte)0x12,(byte)0xa4,(byte)0x42
      ,(byte)0x78,(byte)0xad,(byte)0x34,(byte)0x33
      ,(byte)0xc6,(byte)0xad,(byte)0x72,(byte)0xc0
      ,(byte)0x29,(byte)0xda,(byte)0x41,(byte)0x2e
      ,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x12
      ,(byte)0xe3,(byte)0x83,(byte)0x9e,(byte)0xe3
      ,(byte)0x83,(byte)0x88,(byte)0xe3,(byte)0x83
      ,(byte)0xaa,(byte)0xe3,(byte)0x83,(byte)0x83
      ,(byte)0xe3,(byte)0x82,(byte)0xaf,(byte)0xe3
      ,(byte)0x82,(byte)0xb9,(byte)0x00,(byte)0x00
      ,(byte)0x00,(byte)0x15,(byte)0x00,(byte)0x1c
      ,(byte)0x66,(byte)0x2f,(byte)0x2f,(byte)0x34
      ,(byte)0x39,(byte)0x39,(byte)0x6b,(byte)0x39
      ,(byte)0x35,(byte)0x34,(byte)0x64,(byte)0x36
      ,(byte)0x4f,(byte)0x4c,(byte)0x33,(byte)0x34
      ,(byte)0x6f,(byte)0x4c,(byte)0x39,(byte)0x46
      ,(byte)0x53,(byte)0x54,(byte)0x76,(byte)0x79
      ,(byte)0x36,(byte)0x34,(byte)0x73,(byte)0x41
      ,(byte)0x00,(byte)0x14,(byte)0x00,(byte)0x0b
      ,(byte)0x65,(byte)0x78,(byte)0x61,(byte)0x6d
      ,(byte)0x70,(byte)0x6c,(byte)0x65,(byte)0x2e
      ,(byte)0x6f,(byte)0x72,(byte)0x67,(byte)0x00
      ,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x14
      ,(byte)0xf6,(byte)0x70,(byte)0x24,(byte)0x65
      ,(byte)0x6d,(byte)0xd6,(byte)0x4a,(byte)0x3e
      ,(byte)0x02,(byte)0xb8,(byte)0xe0,(byte)0x71
      ,(byte)0x2e,(byte)0x85,(byte)0xc9,(byte)0xa2
      ,(byte)0x8c,(byte)0xa8,(byte)0x96,(byte)0x66
      });

  
  @Test
  public void testSampleREQ1() throws Exception {
    StunPacket sp = new StunPacket(SAMPLE_STUN_REQ1);
    assertEquals(StunMessageType.REQUEST, sp.getMessageType());
    List<StunAttribute> sal = sp.getAttributes();
    assertEquals(StunAttribute.SOFTWARE, sal.get(0));
    assertEquals(StunAttribute.PRIORITY, sal.get(1));
    assertEquals(StunAttribute.ICE_CONTROLLED, sal.get(2));
    assertEquals(StunAttribute.USERNAME, sal.get(3));
    assertEquals(StunAttribute.MESSAGE_INTEGRITY, sal.get(4));
    assertEquals(StunAttribute.FINGERPRINT, sal.get(5));
    ByteBuffer bb = sp.getUsername();
    String name = new String(StunUtils.BBToBA(bb));
    assertEquals("evtj:h6vY", name);
    String swn = new String(StunUtils.BBToBA(sp.getSoftware()));
    assertEquals("STUN test client", swn);
    assertTrue(StunUtils.verifyMessageIntegerity(sp, "VOkJxbRl1RmTxUk/WvJxBt".getBytes()));
    assertTrue(StunUtils.verifyFingerPrint(sp));


    StunPacketBuilder spb = new StunPacketBuilder();
    spb.setPaddingByte((byte)0x20);
    spb.setType(StunMessageType.REQUEST);
    spb.setTxID(sp.getTxID());
    spb.setAttribute(StunAttribute.SOFTWARE, ByteBuffer.wrap(swn.getBytes()));
    spb.setAttribute(StunAttribute.PRIORITY, sp.getPriority());
    spb.setAttribute(StunAttribute.ICE_CONTROLLED, sp.getIceControlled());
    spb.setUsername(sp.getUsername());

    spb.setKey("VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    spb.enableFingerPrint();
    StunPacket sp5 = spb.build();
    sal = sp5.getAttributes();
    assertEquals(StunAttribute.SOFTWARE, sal.get(0));
    assertEquals(StunAttribute.PRIORITY, sal.get(1));
    assertEquals(StunAttribute.ICE_CONTROLLED, sal.get(2));
    assertEquals(StunAttribute.USERNAME, sal.get(3));
    assertEquals(StunAttribute.MESSAGE_INTEGRITY, sal.get(4));
    assertEquals(StunAttribute.FINGERPRINT, sal.get(5));
    
    StunUtils.verifyMessageIntegerity(sp, "VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    StunUtils.verifyMessageIntegerity(sp5, "VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    assertEquals(sp.getMessageIntegerity(), sp5.getMessageIntegerity());

    assertEquals(sp.getFingerPrint(), sp5.getFingerPrint());
  }
  
  @Test
  public void testSampleRESP1() throws Exception {
    StunPacket sp = new StunPacket(SAMPLE_STUN_RESP1);
    assertEquals(StunMessageType.SUCCESS, sp.getMessageType());
    List<StunAttribute> sal = sp.getAttributes();
    assertEquals(StunAttribute.SOFTWARE, sal.get(0));
    assertEquals(StunAttribute.XOR_MAPPED_ADDRESS, sal.get(1));
    assertEquals(StunAttribute.MESSAGE_INTEGRITY, sal.get(2));
    assertEquals(StunAttribute.FINGERPRINT, sal.get(3));
    String swn = new String(StunUtils.BBToBA(sp.getSoftware()));
    assertEquals("test vector", swn);
    assertEquals(new InetSocketAddress("192.0.2.1", 32853), sp.getAddress());
    assertTrue(StunUtils.verifyMessageIntegerity(sp, "VOkJxbRl1RmTxUk/WvJxBt".getBytes()));
    assertTrue(StunUtils.verifyFingerPrint(sp));


    StunPacketBuilder spb = new StunPacketBuilder();
    spb.setType(StunMessageType.SUCCESS);
    spb.setPaddingByte((byte)0x20);
    spb.setTxID(sp.getTxID());
    spb.setAttribute(StunAttribute.SOFTWARE, ByteBuffer.wrap(swn.getBytes()));
    spb.setXorMappedAddress(new InetSocketAddress("192.0.2.1", 32853));


    spb.setKey("VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    spb.enableFingerPrint();
    StunPacket sp5 = spb.build();
    
    StunUtils.verifyMessageIntegerity(sp, "VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    StunUtils.verifyMessageIntegerity(sp5, "VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    assertEquals(sp.getMessageIntegerity(), sp5.getMessageIntegerity());

    assertEquals(sp.getFingerPrint(), sp5.getFingerPrint());
  }
  
  @Test
  public void testSampleRESP2() throws Exception {
    StunPacket sp = new StunPacket(SAMPLE_STUN_RESP2);
    assertEquals(StunMessageType.SUCCESS, sp.getMessageType());
    List<StunAttribute> sal = sp.getAttributes();
    assertEquals(StunAttribute.SOFTWARE, sal.get(0));
    assertEquals(StunAttribute.XOR_MAPPED_ADDRESS, sal.get(1));
    assertEquals(StunAttribute.MESSAGE_INTEGRITY, sal.get(2));
    assertEquals(StunAttribute.FINGERPRINT, sal.get(3));

    String swn = new String(StunUtils.BBToBA(sp.getSoftware()));
    assertEquals("test vector", swn);
    assertEquals(new InetSocketAddress("2001:db8:1234:5678:11:2233:4455:6677", 32853), sp.getAddress());
    assertTrue(StunUtils.verifyMessageIntegerity(sp, "VOkJxbRl1RmTxUk/WvJxBt".getBytes()));
    assertTrue(StunUtils.verifyFingerPrint(sp));


    StunPacketBuilder spb = new StunPacketBuilder();
    spb.setType(StunMessageType.SUCCESS);
    spb.setPaddingByte((byte)0x20);
    spb.setTxID(sp.getTxID());
    spb.setAttribute(StunAttribute.SOFTWARE, ByteBuffer.wrap(swn.getBytes()));
    spb.setXorMappedAddress(new InetSocketAddress("2001:db8:1234:5678:11:2233:4455:6677", 32853));


    spb.setKey("VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    spb.enableFingerPrint();
    StunPacket sp5 = spb.build();
    
    StunUtils.verifyMessageIntegerity(sp, "VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    StunUtils.verifyMessageIntegerity(sp5, "VOkJxbRl1RmTxUk/WvJxBt".getBytes());
    assertEquals(sp.getMessageIntegerity(), sp5.getMessageIntegerity());

    assertEquals(sp.getFingerPrint(), sp5.getFingerPrint());
  }

  @Test
  public void stunProtocolType() throws Exception {
    StunPacketBuilder sbb = new StunPacketBuilder();
    for(StunMessageType t: StunMessageType.values()) {
      sbb.setType(t);
      StunPacket sp = sbb.build();
      StunPacket sp2 = new StunPacket(sp.getBytes());
      assertEquals(sp.getMessageType(), sp2.getMessageType());
      assertEquals(sp.getTxID(), sp2.getTxID());
      assertTrue(StunUtils.isStunPacket(sp.getBytes()));
      assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
    }
  }

  @Test
  public void stunMappedAddress() throws Exception {
    StunPacketBuilder sbb = new StunPacketBuilder();
    sbb.setType(StunMessageType.REQUEST);
    for(int a=0; a<255; a++) {
      InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(new byte[] {
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt()
      }), 200);
      sbb.setMappedAddress(isa.getAddress().getAddress(), isa.getPort());
      StunPacket sp = sbb.build();
      StunPacket sp2 = new StunPacket(sp.getBytes());
      assertEquals(sp.getMessageType(), sp2.getMessageType());
      assertEquals(sp.getTxID(), sp2.getTxID());
      assertEquals(sp.getAddress(), sp2.getAddress());
      assertTrue(StunUtils.isStunPacket(sp.getBytes()));
      assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
    }
  }
  @Test
  public void stunUserName() throws Exception {
    StunPacketBuilder sbb = new StunPacketBuilder();
    sbb.setType(StunMessageType.REQUEST);
    for(int a=0; a<255; a++) {
      InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(new byte[] {
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt()
      }), 200);
      sbb.setMappedAddress(isa.getAddress().getAddress(), isa.getPort());
      sbb.setUsername(ByteBuffer.wrap(UUID.randomUUID().toString().getBytes()));
      StunPacket sp = sbb.build();
      StunPacket sp2 = new StunPacket(sp.getBytes());
      assertEquals(sp.getMessageType(), sp2.getMessageType());
      assertEquals(sp.getTxID(), sp2.getTxID());
      assertEquals(sp.getUsername(), sp2.getUsername());
      assertTrue(StunUtils.isStunPacket(sp.getBytes()));
      assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
    }
  }

  @Test
  public void stunXORAddress() throws Exception {
    StunPacketBuilder sbb = new StunPacketBuilder();
    sbb.setType(StunMessageType.REQUEST);
    for(int a=0; a<255; a++) {
      InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(new byte[] {
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt()
      }), 200);
      sbb.setXorMappedAddress(isa.getAddress().getAddress(), isa.getPort());
      StunPacket sp = sbb.build();
      StunPacket sp2 = new StunPacket(sp.getBytes());
      assertEquals(sp.getMessageType(), sp2.getMessageType());
      assertEquals(sp.getTxID(), sp2.getTxID());
      assertEquals(sp.getAddress(), sp2.getAddress());
      assertTrue(StunUtils.isStunPacket(sp.getBytes()));
      assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
    }
  }

  @Test
  public void stunXORipv6Address() throws Exception {
    StunPacketBuilder sbb = new StunPacketBuilder();
    sbb.setType(StunMessageType.REQUEST);
    for(int a=0; a<255; a++) {
      InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(new byte[] {
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt()
      }
          ), 200);
      sbb.setXorMappedAddress(isa.getAddress().getAddress(), isa.getPort());
      StunPacket sp = sbb.build();
      StunPacket sp2 = new StunPacket(sp.getBytes());
      assertEquals(sp.getMessageType(), sp2.getMessageType());
      assertEquals(sp.getTxID(), sp2.getTxID());
      assertEquals(sp.getAddress(), sp2.getAddress());
      assertTrue(StunUtils.isStunPacket(sp.getBytes()));
      assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
    }
  }
  
  @Test
  public void stunFingerPrint() throws Exception {
    StunPacketBuilder sbb = new StunPacketBuilder();
    sbb.setType(StunMessageType.REQUEST);
    byte[] keyba = new byte[32];
    ThreadLocalRandom.current().nextBytes(keyba);
    for(int a=0; a<255; a++) {
      InetSocketAddress isa = new InetSocketAddress(InetAddress.getByAddress(new byte[] {
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)a, 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(), 
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt(),
          (byte)ThreadLocalRandom.current().nextInt()
      }
          ), 200);
      sbb.setXorMappedAddress(isa.getAddress().getAddress(), isa.getPort());
      StunPacket sp = sbb.enableFingerPrint().build();
      StunPacket sp2 = new StunPacket(sp.getBytes());
      assertTrue(StunUtils.verifyFingerPrint(sp));
      assertEquals(sp.getMessageType(), sp2.getMessageType());
      assertEquals(sp.getTxID(), sp2.getTxID());
      assertEquals(sp.getAddress(), sp2.getAddress());
      assertTrue(StunUtils.isStunPacket(sp.getBytes()));
      assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
    }
  }

  @Test
  public void stunHasAddress() throws Exception {
    InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 200);
    StunPacketBuilder sbb = new StunPacketBuilder();
    TransactionID txID = StunUtils.generateTxID();
    sbb.setTxID(txID);
    assertFalse(sbb.build().hasAddress());
    sbb.setMappedAddress(isa);
    assertTrue(sbb.build().hasAddress());
    sbb.setXorMappedAddress(isa);
    assertTrue(sbb.build().hasAddress());
    sbb.clearAllAttributes();
    assertFalse(sbb.build().hasAddress());
    assertTrue(StunUtils.isStunPacket(sbb.build().getBytes()));
    assertFalse(StunUtils.isStunPacket(ByteBuffer.wrap(new byte[4])));
    ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
    bb.putShort((short)4);
    bb.position(0);
    assertFalse(StunUtils.isStunPacket(bb));
    assertEquals(txID,sbb.build().getTxID());
  }

  @Test
  public void testBadPort() throws UnknownHostException {
    StunPacketBuilder sbb = new StunPacketBuilder();
    try {
      sbb.setMappedAddress(InetAddress.getLocalHost().getAddress(), 87400);
    } catch(IllegalArgumentException e) {
      return;
    }
    fail();
  }
  
  @Test
  public void testBadXORPort() throws UnknownHostException {
    StunPacketBuilder sbb = new StunPacketBuilder();
    try {
      sbb.setXorMappedAddress(InetAddress.getLocalHost().getAddress(), 87400);
    } catch(IllegalArgumentException e) {
      return;
    }
    fail();
  }
  
  @Test
  public void testNullAddress() throws UnknownHostException {
    StunPacketBuilder sbb = new StunPacketBuilder();
    try {
      sbb.setMappedAddress(null);
    } catch(IllegalArgumentException e) {
      return;
    }
    fail();
  }
  
  @Test
  public void testNullXORAddress() throws UnknownHostException {
    StunPacketBuilder sbb = new StunPacketBuilder();
    try {
      sbb.setXorMappedAddress(null);
    } catch(IllegalArgumentException e) {
      return;
    }
    fail();
  }

}
