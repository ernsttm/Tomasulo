package edu.pitt.ernst;

import edu.pitt.ernst.config.ProcessorConfig;
import edu.pitt.ernst.memory.Memory;
import edu.pitt.ernst.registers.RegisterFile;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.*;

public class SimpleTest {
  @Test
  public void test1A() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("1A.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(1, regFile.getIntRegister(2).getValue());
    assertEquals(9, regFile.getIntRegister(4).getValue());
    assertEquals(-1, regFile.getIntRegister(7).getValue());
  }

  @Test
  public void test1B() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("1B.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(3.0, regFile.getDoubleRegister(2).getValue(), .00001);
    assertEquals(-1.0, regFile.getDoubleRegister(5).getValue(), .00001);
  }

  @Test
  public void test1C() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("1C.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(2.0, regFile.getDoubleRegister(2).getValue(), .00001);
  }

  @Test
  public void test1D() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("1D.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(10.1, regFile.getDoubleRegister(1).getValue(), .00001);
    assertEquals(9.0, regFile.getDoubleRegister(2).getValue(), .00001);
  }

  @Test
  public void test2A() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("2A.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(1, regFile.getIntRegister(2).getValue());
    assertEquals(2, regFile.getIntRegister(3).getValue());
  }

  @Test
  public void test2B() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("2B.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(-2, regFile.getIntRegister(2).getValue());
  }

  @Test
  public void test2C() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("2C.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(1.0, regFile.getDoubleRegister(2).getValue(), .00001);
    assertEquals(6.0, regFile.getDoubleRegister(3).getValue(), .00001);
  }

  @Test
  public void test3A() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("3A.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(3.0, regFile.getDoubleRegister(2).getValue(), .00001);
    assertEquals(3.0, regFile.getDoubleRegister(4).getValue(), .00001);

    Memory mem = Memory.getInstance();
    assertEquals(3.0, mem.loadFP(0), .00001);
  }

  @Test
  public void test4A() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("4A.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(2.0, regFile.getDoubleRegister(2).getValue(), .00001);
    assertEquals(2.0, regFile.getDoubleRegister(5).getValue(), .00001);
    assertEquals(-5.0, regFile.getDoubleRegister(8).getValue(), .00001);
  }

  @Test
  public void test4B() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("4B.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(1, regFile.getIntRegister(2).getValue());
    assertEquals(3.0, regFile.getDoubleRegister(2).getValue(), .00001);
  }

  @Test
  public void test5A()  throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("5A.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(3, regFile.getIntRegister(1).getValue());
    assertEquals(3, regFile.getIntRegister(2).getValue());
  }

  @Test
  public void test6A()  throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("6A.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(1, regFile.getIntRegister(2).getValue());
    assertEquals(1, regFile.getIntRegister(3).getValue());
  }

  @Test
  public void test6B()  throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/config/SimpleTest.json");
    File configFile = new File(url.toURI());

    ProcessorConfig config = ProcessorConfig.createInstance(configFile.getAbsolutePath());
    config.setInstructionFile(getInstructionFilePath("6B.txt"));
    Processor proc = new Processor(config);
    proc.executeProgram();

    // Validate the registers match the expected values
    RegisterFile regFile = RegisterFile.getInstance();
    assertEquals(4, regFile.getIntRegister(2).getValue());
    assertEquals(4, regFile.getIntRegister(3).getValue());
  }

  private String getInstructionFilePath(String testInstructionFile)
      throws URISyntaxException {
    String resourcePath = INSTRUCTION_DIR + testInstructionFile;
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource(resourcePath);
    File configFile = new File(url.toURI());

    return configFile.getAbsolutePath();
  }

  private static final String INSTRUCTION_DIR = "java/edu/pitt/ernst/instructions/";
}
