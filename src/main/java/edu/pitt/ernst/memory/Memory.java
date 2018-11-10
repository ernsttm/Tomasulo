package edu.pitt.ernst.memory;

import edu.pitt.ernst.config.MemoryConfig;

import java.util.Map;

public class Memory {
  private Memory() {
    words = new Word[NUM_BYTES/4];
    for (int i = 0; i < NUM_BYTES/4; i++) {
      words[i] = new Word(0);
    }
  }

  private Memory(Map<Integer, String> startValues) {
    // Initialize the memory instances.
    this();

    for (Map.Entry<Integer, String> value : startValues.entrySet()) {
      int index = value.getKey() / 4;

      try {
        words[index].setValue(Integer.parseInt(value.getValue()));
      } catch (NumberFormatException e) {
        words[index].setValue(Double.parseDouble(value.getValue()));
      }
    }
  }

  public void store(int address, double value) {
    validateAddress(address);

    words[address / 4].setValue(value);
  }

  public double loadFP(int address) {
    validateAddress(address);

    return words[address/ 4].getFPValue();
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Memory Values\nAddress\t\tValue\n");

    for (int i = 0; i < NUM_BYTES/4; i++) {
      builder.append(i * 4).append("\t\t\t");
      builder.append(words[i].getFPValue());
      builder.append("\n");
    }

    return builder.toString();
  }

  public static Memory createInstance() {
    return instance_ = new Memory();
  }

  public static Memory createInstance(Map<Integer, String> startValues) {
    return instance_ = new Memory(startValues);
  }

  public static Memory getInstance() {
    if (null == instance_) {
      throw new IllegalStateException("Memory has not been initialized.");
    }

    return instance_;
  }

  private void validateAddress(int address) {
    if (address > NUM_BYTES | 0 != (address % 4)) {
      throw new IllegalArgumentException("Address is out of bounds, or not properly aligned.");
    }
  }

  private Word[] words;

  private static Memory instance_ = null;

  // The number of bytes in memory;
  private static final int NUM_BYTES = 256;

  private class Word {
    Word(double value) {
      setValue(value);
    }

    void setValue(double value) {
      fPValue_ = value;
    }

    double getFPValue() {
      return fPValue_;
    }

    private double fPValue_;
  }
}
