package edu.pitt.ernst.units;

import edu.pitt.ernst.Processor;
import edu.pitt.ernst.RegisterAliasingTable;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.rob.InstructionState;

public abstract class ReservationStation {
  ReservationStation() {
    isBusy_ = false;
    isReady_ = false;
  }

  boolean isBusy() {
    return isBusy_;
  }

  boolean isReady() {
    if (isReady_) {
      return true;
    } else if (isBusy_) {
      isReady_ = true;
      return false;
    } else {
      isReady_ = false;
      return false;
    }
  }

  void reset() {
    isBusy_ = false;
    isReady_ = false;
  }

  public void reserve(Instruction instruction, RegisterAliasingTable rat) {
    isBusy_ = true;
    instruction_ = instruction;
  }

  int getInstructionId() {
    return instruction_.getId();
  }

  Instruction getInstruction() {
    return instruction_;
  }

  InstructionTypes getInstructionType() {
    return instruction_.getInstructionType();
  }

  void start() {
    instruction_.changeState(InstructionState.EXECUTE, Processor.getCycle());
  }

  void intUpdate(int register, int value) {
    // Make a no-op unless a child class explicitly implements it.
  }

  void doubleUpdate(int register, double value) {
    // Make a no-op unless a child class explicitly implements it.
  }

  private boolean isBusy_;
  private boolean isReady_;
  protected Instruction instruction_;
}
