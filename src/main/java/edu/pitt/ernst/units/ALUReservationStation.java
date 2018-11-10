package edu.pitt.ernst.units;

import edu.pitt.ernst.RegisterAliasingTable;
import edu.pitt.ernst.instructions.ALUInstruction;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.registers.RegisterFile;

public class ALUReservationStation extends ReservationStation {
  public ALUReservationStation() {
    regIds_ = new Integer[2];
    regIds_[0] = null;
    regIds_[1] = null;

    values_ = new Integer[2];
    values_[0] = null;
    values_[1] = null;
  }

  @Override
  public void reserve(Instruction instruction, RegisterAliasingTable rat) {
    super.reserve(instruction, rat);

    ALUInstruction aluInstruction = (ALUInstruction)instruction;
    destination_ = rat.reserve(aluInstruction.getDestination(), false);

    getValue(aluInstruction.getOp1(), 0, rat);
    if (InstructionTypes.ADD_IMMEDIATE == aluInstruction.getInstructionType()) {
      values_[1] = aluInstruction.getOp2();
    } else {
      getValue(aluInstruction.getOp2(), 1, rat);
    }


  }

  @Override
  public void reset() {
    super.reset();

    regIds_[0] = null;
    regIds_[1] = null;

    values_[0] = null;
    values_[1] = null;
  }

  public int getOp1() {
    return values_[0];
  }

  public int getOp2() {
    return values_[1];
  }

  public int getDestination() {
    return destination_;
  }

  @Override
  public boolean isReady() {
    for (int i = 0; i < 2; i++) {
      if (null == values_[i]) {
        return false;
      }
    }

    return true;
  }

  @Override
  protected void intUpdate(int register, int value) {
    for (int i = 0; i < 2; i++) {
      if (null != regIds_[i] && regIds_[i] == register) {
        values_[i] = value;
      }
    }
  }

  private void getValue(int fileRegister, int index, RegisterAliasingTable rat) {
    regIds_[index] = rat.getRegister(fileRegister, false);

    // If there is no mapping, then the register contains the acceptable value.
    if (null == regIds_[index]) {
      values_[index] = RegisterFile.getInstance().getIntRegister(fileRegister).getValue();
    }
  }

  private int destination_;
  private Integer[] regIds_;
  private Integer[] values_;
}
