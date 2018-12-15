package edu.pitt.ernst.instructions;

import edu.pitt.ernst.rob.InstructionState;

import java.util.HashMap;
import java.util.Map;

public abstract class Instruction {
  public Instruction(int address, String instruction) {
    address_ = address;
    instruction_ = instruction;

    id_ = InstructionBuffer.countInstruction();
    transitions_ = new HashMap<>();
  }

  public abstract InstructionTypes getInstructionType();

  public int getId() {
    return id_;
  }

  public int getAddress() {
    return address_;
  }

  public void changeState(InstructionState state, int cycle) {
    transitions_.put(state, cycle);
  }

  public String printHistory() {
    String format = "%s\t\t%s\t\t%s\t\t%s\t\t%s\t\t\t%s\n";
    String instruction = id_ + " " + instruction_;
    int length = instruction.length();
    if (length < 16) {
      instruction += "\t";
    }
    String issue = getStateString(InstructionState.ISSUE);
    String execute = getStateString(InstructionState.EXECUTE);
    String memory = getStateString(InstructionState.MEMORY);
    String writeBack = getStateString(InstructionState.WRITE_BACK);
    String commit = getStateString(InstructionState.COMMIT);

    return String.format(format, instruction, issue, execute, memory, writeBack, commit);
  }

  private String getStateString(InstructionState state) {
    return transitions_.containsKey(state) ? transitions_.get(state).toString() : "n/a";
  }

  private int id_;
  private int address_;
  private String instruction_;
  private Map<InstructionState, Integer> transitions_;
}
