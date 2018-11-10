package edu.pitt.ernst.config;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class MemoryConfig {
  public Map<Integer, String> getStartValues() {
    Map<Integer, String> startValues = new HashMap<>();

    for (WordConfig config : wordConfigs) {
      if (0 != config.getAddress() % 4) {
        throw new IllegalArgumentException("Address is not memory aligned.");
      }

      startValues.put(config.getAddress(), config.getValue());
    }

    return startValues;
  }

  @SerializedName("start_values")
  private WordConfig[] wordConfigs = { };

  private class WordConfig {
    public int getAddress() {
      return address_;
    }

    public String getValue() {
      return value_;
    }

    @SerializedName("address")
    private int address_;

    @SerializedName("value")
    private String value_;
  }
}
