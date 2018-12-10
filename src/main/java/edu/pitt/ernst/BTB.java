package edu.pitt.ernst;

import edu.pitt.ernst.instructions.Instruction;

import java.util.HashMap;
import java.util.Map;

public class BTB {
  private BTB() {
    predictionHistory_ = new HashMap<>();
    btbEntries = new boolean[BTB_ENTRIES];
    for (int i = 0; i < BTB_ENTRIES; i ++) {
      btbEntries[i] = false;
    }
  }

  /**
   * Based on the given address predict the result of a branching instruction.
   */
  public boolean predict(int address, Instruction instruction) {
    boolean prediction = btbEntries[address % BTB_ENTRIES];
    predictionHistory_.put(instruction.getId(), new PredictionEntry(address, prediction));
    return prediction;
  }

  /**
   * Submit the result of a branching instruction, and retrieve the value predicted.
   *
   * @param instructionId the id of the instruction causing the given result.
   * @param result true if the branch was taken, false otherwise.
   * @return true if the predictor expected the branch to be taken, false otherwise.
   */
  public boolean setPrediction(int instructionId, boolean result) {
    boolean prediction = predictionHistory_.get(instructionId).prediction;
    btbEntries[predictionHistory_.get(instructionId).address % BTB_ENTRIES] = result;
    return prediction;
  }

  public static BTB createInstance() {
    instance_ = new BTB();
    return instance_;
  }

  public static BTB getInstance() {
    if (null == instance_) {
      throw new IllegalStateException("BTB has not been initialized.");
    }

    return instance_;
  }

  private class PredictionEntry {
    public PredictionEntry(int address, boolean prediction) {
      this.address = address;
      this.prediction = prediction;
    }

    public int address;
    public boolean prediction;
  }

  private boolean btbEntries[];
  private Map<Integer, PredictionEntry> predictionHistory_;

  private static BTB instance_;
  private static final int BTB_ENTRIES = 8;
}
