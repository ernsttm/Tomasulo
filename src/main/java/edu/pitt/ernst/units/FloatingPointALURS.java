package edu.pitt.ernst.units;

import edu.pitt.ernst.RegisterAliasingTable;
import edu.pitt.ernst.instructions.ALUInstruction;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.registers.RegisterFile;

/**
 * This class represents a floating point arithmetic processing unit.
 */
public class FloatingPointALURS extends ReservationStation {
  FloatingPointALURS() {
    regIds_ = new Integer[2];
    values_ = new Double[2];

    for (int i = 0; i < 2; i++) {
      regIds_[i] = null;
      values_[i] = null;
    }
  }

  @Override
  public void reserve(Instruction instruction, RegisterAliasingTable rat) {
    super.reserve(instruction, rat);

    ALUInstruction aluInstruction = (ALUInstruction)instruction;
    getValue(aluInstruction.getOp1(), 0, rat);
    getValue(aluInstruction.getOp2(), 1, rat);
    destination_ = rat.reserve(aluInstruction.getDestination(), true);
  }

  @Override
  public void reset() {
    super.reset();

    for (int i = 0; i < 2; i++) {
      regIds_[i] = null;
      values_[i] = null;
    }
  }

  @Override
  public boolean isReady() {
    if (!super.isReady()) {
      return false;
    }

    for (int i = 0; i < 2; i++) {
      if (null == values_[i]) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void doubleUpdate(int register, double value) {
    for (int i = 0; i < 2; i++) {
      if (null != regIds_[i] && regIds_[i] == register) {
        values_[i] = value;
      }
    }
  }

  public double getOp1() {
    return values_[0];
  }

  public double getOp2() {
    return values_[1];
  }

  public int getDestination() {
    return destination_;
  }

  private void getValue(int fileRegister, int index, RegisterAliasingTable rat) {
    if (rat.isReservedRegister(fileRegister, true)) {
      if (rat.isValidRegister(fileRegister, true)) {
        values_[index] = rat.getFloatRegisterValue(fileRegister);
      } else {
        regIds_[index] = rat.getHardwareRegister(fileRegister, true);
      }
    } else {
      // If there is no mapping, then the register contains the acceptable value.
      values_[index] = RegisterFile.getInstance().getDoubleRegister(fileRegister).getValue();
    }
  }

  private int destination_;
  private Integer[] regIds_;
  private Double[] values_;
}
