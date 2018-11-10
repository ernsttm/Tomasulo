package edu.pitt.ernst.registers;

import edu.pitt.ernst.config.RegistersConfig;

import java.util.Map;

public class RegisterFile {
  private RegisterFile() {
    // Initialize the register files
    for (int i = 0; i < 32; i++) {
      intRegisters_[i] = new IntRegister(0);
      fpRegisters_[i] = new FPRegister(0.0);
    }
    intRegisters_[0] = new ZeroRegister();
  }

  private RegisterFile(RegistersConfig initialConfig) {
    // Initialize the register file.
    this();

    // Iterate through the registers and add the configured values.
    for (Map.Entry<Integer, Integer> entry : initialConfig.getIntegerRegisterValues().entrySet()) {
      if (entry.getKey() > 31) {
        throw new IllegalArgumentException("Configuration exceeds number of registers: " + entry.getKey());
      }

      intRegisters_[entry.getKey()].setValue(entry.getValue());
    }

    for (Map.Entry<Integer, Double> entry : initialConfig.getFPRegisterValues().entrySet()) {
      if (entry.getKey() > 31) {
        throw new IllegalArgumentException("Configuration exceeds number of registers: " + entry.getKey());
      }

      fpRegisters_[entry.getKey()].setValue(entry.getValue());
    }
  }

  public IntRegister getIntRegister(int index) {
    return intRegisters_[index];
  }

  public FPRegister getDoubleRegister(int index) {
    return fpRegisters_[index];
  }

  public void setRegister(int index, int value) {
    intRegisters_[index].setValue(value);
  }

  public void setRegister(int index, double value) {
    fpRegisters_[index].setValue(value);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append("Register Values:\nIndex\tInt\t\tDouble\n");
    for (int i = 0; i < 32; i++) {
      builder.append(i + "\t\t" + intRegisters_[i].getValue() + "\t\t" + fpRegisters_[i].getValue() + "\n");
    }

    return builder.toString();
  }

  public static RegisterFile createInstance() {
    return instance_ = new RegisterFile();
  }

  public static RegisterFile createInstance(RegistersConfig config) {
    return instance_ = new RegisterFile(config);
  }

  public static RegisterFile getInstance() {
    if (null == instance_) {
      throw new IllegalStateException("Register File hasn't been initialized yet.");
    }

    return instance_;
  }

  private IntRegister[] intRegisters_ = new IntRegister[32];
  private FPRegister[] fpRegisters_ = new FPRegister[32];

  private static RegisterFile instance_ = null;
}
