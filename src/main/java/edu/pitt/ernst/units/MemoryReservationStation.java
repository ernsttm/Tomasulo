package edu.pitt.ernst.units;

import edu.pitt.ernst.Processor;
import edu.pitt.ernst.RegisterAliasingTable;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.instructions.MemoryInstruction;
import edu.pitt.ernst.registers.RegisterFile;
import edu.pitt.ernst.rob.InstructionState;

public class MemoryReservationStation extends ReservationStation {
  public MemoryReservationStation(MemoryUnit memoryUnit) {
    address_ = null;

    storeId_ = null;
    storeValue_ = null;
    registerId_ = null;
    registerValue_ = null;

    memoryUnit_ = memoryUnit;
  }

  public void reserve(Instruction instruction, RegisterAliasingTable rat) {
    super.reserve(instruction, rat);

    MemoryInstruction memInstruction = (MemoryInstruction)instruction;

    // Acquire assets shared between load and store instructions.
    offset_ = memInstruction.getOffset();
    int register = memInstruction.getRegister();
    if (rat.isReservedRegister(register, false)) {
      if (rat.isValidRegister(register, false)) {
        registerValue_ = rat.getIntRegisterValue(register);
      } else {
        registerId_ = rat.getHardwareRegister(register, false);
      }
    } else {
      // If there is no mapping, then the register contains the acceptable value.
      registerValue_ = RegisterFile.getInstance().getIntRegister(register).getValue();
    }

    if (InstructionTypes.LOAD_FP == memInstruction.getInstructionType()) {
      destination_ = rat.reserve(memInstruction.getDestination(), true);
    } else {
      int memRegister = memInstruction.getDestination();
      if (rat.isReservedRegister(memRegister, true)) {
        if (rat.isValidRegister(memRegister, true)) {
          storeValue_ = rat.getFloatRegisterValue(memRegister);
        } else {
          storeId_ = rat.getHardwareRegister(memRegister, true);
        }
      } else {
        // If there is no mapping, then the register contains the acceptable value.
        storeValue_ = RegisterFile.getInstance().getDoubleRegister(memRegister).getValue();
      }
    }

    memoryUnit_.addToQueue(this);
  }

  @Override
  public void reset() {
    super.reset();

    address_ = null;
    storeId_ = null;
    storeValue_ = null;
    registerId_ = null;
    registerValue_ = null;
  }

  @Override
  public boolean isReady() {
    if (!super.isReady()) {
      return false;
    }

    return null != registerValue_;
  }

  @Override
  public void intUpdate(int register, int value) {
    if (null != registerId_ && registerId_ == register) {
      registerValue_ = value;
    }
  }

  @Override
  public void doubleUpdate(int register, double value) {
    if (null != storeId_ && storeId_ == register) {
      storeValue_ = value;
    }
  }

  public Integer getAddress() {
    return address_;
  }

  public void setAddress(int address) {
    address_ = address;
  }

  public int getOffset() {
    return offset_;
  }

  public int getRegister() {
    return registerValue_;
  }

  public int getDestination() {
    return destination_;
  }

  public Double getStoreValue() {
    return storeValue_;
  }

  public void setStoreValue(Double storeValue) {
    storeValue_ = storeValue;
  }

  public void startMemory() {
    instruction_.changeState(InstructionState.MEMORY, Processor.getCycle());
  }

  private int offset_;
  private int destination_;

  private Integer address_;
  private Integer registerId_;
  private Integer registerValue_;

  private Integer storeId_;
  private Double storeValue_;

  private MemoryUnit memoryUnit_;
}
