package edu.pitt.ernst.rob;

import edu.pitt.ernst.instructions.Instruction;

public class BufferEntry {
  public BufferEntry(Instruction instruction) {
    readyToCommit_ = false;

    destination_ = -1;
    instruction_ = instruction;
  }

  public Instruction getInstruction() {
    return instruction_;
  }

  public void readyToCommit() {
    readyToCommit_ = true;
  }

  public boolean isReadyToCommit() {
    return readyToCommit_;
  }

  public void setValue(int destination, int value) {
    destination_ = destination;
    intValue_ = value;
  }

  public void setValue(int destination, double value) {
    destination_ = destination;
    floatValue_ = value;
  }

  public int getDestination() {
    return destination_;
  }

  public int getIntValue() {
    return intValue_;
  }

  public double getFloatValue() {
    return floatValue_;
  }

  private int intValue_;
  private int destination_;
  private double floatValue_;
  private boolean readyToCommit_;

  private Instruction instruction_;
}
