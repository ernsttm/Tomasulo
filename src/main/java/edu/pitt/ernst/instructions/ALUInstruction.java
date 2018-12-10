package edu.pitt.ernst.instructions;

/**
 * An instruction for an arithmetic operation.
 */
public class ALUInstruction extends Instruction implements DestinationInstruction {
  public ALUInstruction(InstructionTypes type, int destination, int op1, int op2, int address) {
    super(address);

    type_ = type;
    op1_ = op1;
    op2_ = op2;
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

  public int getOp1() {
    return op1_;
  }

  public int getOp2() {
    return op2_;
  }

  private int op1_;
  private int op2_;
  private int destination_;
  private InstructionTypes type_;
}
