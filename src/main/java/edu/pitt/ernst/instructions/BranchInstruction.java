package edu.pitt.ernst.instructions;

/**
 * An instruction for a branch operation.
 */
public class BranchInstruction extends Instruction {
  public BranchInstruction(InstructionTypes type, int op1, int op2, int offset, int address, String inst) {
    super(address, inst);
    type_ = type;

    op1_ = op1;
    op2_ = op2;
    offset_= offset;
  }

  @Override
  public InstructionTypes getInstructionType() {
    return type_;
  }

  public int getOp1() {
    return op1_;
  }

  public int getOp2() {
    return op2_;
  }

  public int getOffset() {
    return offset_;
  }

  private int op1_;
  private int op2_;
  private int offset_;
  private InstructionTypes type_;
}
