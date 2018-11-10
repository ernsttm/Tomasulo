package edu.pitt.ernst.config;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

public class TestProcessorConfig {
  @Test
  public void testParseConfig() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/TestConfig.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    assertEquals(10, config.getRobEntries());
    assertEquals(1, config.getCdbEntries_());
    assertEquals("/opt/test_instructions.txt", config.getInstructionFile());
  }

  @Test
  public void testMemoryConfig() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/TestConfig.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    MemoryConfig memConfig = config.getMemoryConfig();
    Map<Integer, String> startValues = memConfig.getStartValues();

    assertEquals(3, startValues.size());
    assertTrue(startValues.containsKey(0));
    assertTrue(startValues.containsKey(4));
    assertTrue(startValues.containsKey(12));
    assertEquals("10.1", startValues.get(0));
    assertEquals("9", startValues.get(4));
    assertEquals("-234.556", startValues.get(12));
  }

  @Test
  public void testRegistersConfig() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/TestConfig.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    RegistersConfig regConfig = config.getRegistersConfig();
    Map<Integer, Double> fpStartValues = regConfig.getFPRegisterValues();
    Map<Integer, Integer> intStartValues = regConfig.getIntegerRegisterValues();

    assertEquals(2, fpStartValues.size());
    assertTrue(fpStartValues.containsKey(30));
    assertTrue(fpStartValues.containsKey(15));
    assertEquals(-54.1234, fpStartValues.get(30), .0001);
    assertEquals(.000012345, fpStartValues.get(15), .0000001);

    assertEquals(2, intStartValues.size());
    assertTrue(intStartValues.containsKey(1));
    assertTrue(intStartValues.containsKey(10));
    assertEquals(10, (long)intStartValues.get(1));
    assertEquals(-25, (long)intStartValues.get(10));
  }

  @Test
  public void testReservationStationConfig() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/TestConfig.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    ReservationStationConfig rsConfig = config.getReservationStationConfig();
    assertEquals(2, rsConfig.getAdderRS());
    assertEquals(3, rsConfig.getFPAdderRS());
    assertEquals(2, rsConfig.getFPMultiplierRS());
    assertEquals(3, rsConfig.getMemoryRS());
    assertEquals(1, rsConfig.getAdderExecCycles());
    assertEquals(3, rsConfig.getFPAdderExecCycles());
    assertEquals(20, rsConfig.getFPMultiplierExecCycles());
    assertEquals(1, rsConfig.getMemoryExecCycles());
    assertEquals(4, rsConfig.getMemoryAccessCycles());
  }
}
