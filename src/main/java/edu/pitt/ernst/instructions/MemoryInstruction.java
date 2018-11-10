package edu.pitt.ernst.instructions;

/**
 * An instruction for a memory access/store
 */
public class MemoryInstruction extends Instruction implements DestinationInstruction {
  public MemoryInstruction(InstructionTypes type, int destination, int register, int offset) {
    type_ = type;

    offset_ = offset;
    register_ = register;
    destination_ = destination;
  }

  @Override
  public InstructionTypes getInstructionType() {
    return type_;
  }

  @Override
  public int getDestination() {
    return destination_;
  }

  public int getRegister() {
    return register_;
  }

  public int getOffset() {
    return offset_;
  }

  private int offset_;
  private int register_;
  private int destination_;
  private InstructionTypes type_;
}
