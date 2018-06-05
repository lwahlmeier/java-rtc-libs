package me.lcw.rtc.stun;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class StunTests {

  @Test
  public void stunProtocolType() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      StunPacketBuilder sbb = new StunPacketBuilder();
      for(MessageType t: MessageType.values()) {
          sbb.setType(t);
          StunPacket sp = sbb.build();
          StunPacket sp2 = new StunPacket(sp.getBytes());
          assertEquals(sp.getType(), sp2.getType());
          assertEquals(sp.getTxID(), sp2.getTxID());
          assertTrue(StunUtils.isStunPacket(sp.getBytes()));
          assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
      }
  }
  
  @Test
  public void stunMappedAddress() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      StunPacketBuilder sbb = new StunPacketBuilder();
      sbb.setType(MessageType.REQUEST);
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
          assertEquals(sp.getType(), sp2.getType());
          assertEquals(sp.getTxID(), sp2.getTxID());
          assertEquals(sp.getAddress(), sp2.getAddress());
          assertTrue(StunUtils.isStunPacket(sp.getBytes()));
          assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
      }
  }
  @Test
  public void stunUserName() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      StunPacketBuilder sbb = new StunPacketBuilder();
      sbb.setType(MessageType.REQUEST);
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
          assertEquals(sp.getType(), sp2.getType());
          assertEquals(sp.getTxID(), sp2.getTxID());
          assertEquals(sp.getUsername(), sp2.getUsername());
          assertTrue(StunUtils.isStunPacket(sp.getBytes()));
          assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
      }
  }
  
  @Test
  public void stunXORAddress() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      StunPacketBuilder sbb = new StunPacketBuilder();
      sbb.setType(MessageType.REQUEST);
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
          assertEquals(sp.getType(), sp2.getType());
          assertEquals(sp.getTxID(), sp2.getTxID());
          assertEquals(sp.getAddress(), sp2.getAddress());
          assertTrue(StunUtils.isStunPacket(sp.getBytes()));
          assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
      }
  }
  
  @Test
  public void stunXORipv6Address() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      StunPacketBuilder sbb = new StunPacketBuilder();
      sbb.setType(MessageType.REQUEST);
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
          assertEquals(sp.getType(), sp2.getType());
          assertEquals(sp.getTxID(), sp2.getTxID());
          assertEquals(sp.getAddress(), sp2.getAddress());
          assertTrue(StunUtils.isStunPacket(sp.getBytes()));
          assertTrue(StunUtils.isStunPacket(sp2.getBytes()));
      }
  }

  @Test
  public void stunHasAddress() throws IOException, InterruptedException, ExecutionException, TimeoutException {
      InetSocketAddress isa = new InetSocketAddress("127.0.0.1", 200);
      StunPacketBuilder sbb = new StunPacketBuilder();
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
  }

}
