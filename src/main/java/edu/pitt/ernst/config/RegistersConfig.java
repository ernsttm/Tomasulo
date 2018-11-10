package edu.pitt.ernst.config;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class RegistersConfig {
  public Map<Integer, Integer> getIntegerRegisterValues() {
    Map<Integer, Integer> startValues = new HashMap<>();

    for (RegisterConfig config : startValues_) {
      if (config.getName().startsWith(INT_PREFIX)) {
        int regIndex = Integer.parseInt(config.getName().substring(INT_PREFIX.length()));
        startValues.put(regIndex, Integer.parseInt(config.getValue()));
      }
    }

    return startValues;
  }

  public Map<Integer, Double> getFPRegisterValues() {
    Map<Integer, Double> startValues = new HashMap<>();

    for (RegisterConfig config : startValues_) {
      if (config.getName().startsWith(FP_PREFIX)) {
        int regIndex = Integer.parseInt(config.getName().substring(FP_PREFIX.length()));
        startValues.put(regIndex, Double.parseDouble(config.getValue()));
      }
    }

    return startValues;
  }

  @SerializedName("start_values")
  private RegisterConfig[] startValues_ = { };

  private static final String INT_PREFIX = "int_";
  private static final String FP_PREFIX = "fp_";
}
