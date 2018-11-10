package edu.pitt.ernst.registers;

import edu.pitt.ernst.config.ProcessorConfig;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.*;

public class TestRegisterFile {
  @Test
  public void testConfigRegisters() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/TestConfig.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    RegisterFile regFile = RegisterFile.createInstance(config.getRegistersConfig());

    assertEquals(0, regFile.getIntRegister(0).getValue());
    assertEquals(10, regFile.getIntRegister(1).getValue());
    assertEquals(-25, regFile.getIntRegister(10).getValue());
    assertEquals(0, regFile.getIntRegister(31).getValue());

    assertEquals(0.0, regFile.getDoubleRegister(0).getValue(), 0.01);
    assertEquals(-54.1234, regFile.getDoubleRegister(30).getValue(), .000001);
    assertEquals(.000012345, regFile.getDoubleRegister(15).getValue(), .000001);
  }

  @Test (expected = IllegalStateException.class)
  public void setRegisters() {
    RegisterFile regFile = RegisterFile.createInstance();

    assertEquals(0, regFile.getIntRegister(1).getValue());

    regFile.setRegister(1, 10);
    assertEquals(10, regFile.getIntRegister(1).getValue());

    assertEquals(0, regFile.getDoubleRegister(0).getValue(), .000001);

    regFile.setRegister(0, 1.010);
    assertEquals(1.010, regFile.getDoubleRegister(0).getValue(), .000001);

    // This should throw an Exception, since the zero register cannot be set.
    regFile.setRegister(0, 0);
  }

  @Ignore
  @Test
  public void testToString() {
    RegisterFile regFile = RegisterFile.createInstance();
    regFile.setRegister(1, 10);
    regFile.setRegister(0, 1.010);

    System.out.println(regFile.toString());
  }
}
