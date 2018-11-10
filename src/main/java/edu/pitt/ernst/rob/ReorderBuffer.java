package edu.pitt.ernst.rob;

import edu.pitt.ernst.CDB;
import edu.pitt.ernst.CDBListener;
import edu.pitt.ernst.Processor;
import edu.pitt.ernst.instructions.DestinationInstruction;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.memory.Memory;
import edu.pitt.ernst.registers.RegisterFile;
import edu.pitt.ernst.units.MemoryReservationStation;
import edu.pitt.ernst.units.MemoryUnit;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class ReorderBuffer implements CDBListener {
  public ReorderBuffer(int size) {
    size_ = size;
    instructions_ = new ArrayDeque<>();
    instructionHistory_ = new ArrayList<>();

    CDB cdb = CDB.getInstance();
    cdb.register(this);
  }

  public void addInstruction(Instruction instruction) {
    if (instructions_.size() >= size_) {
      throw new IllegalStateException("Reorder Buffer would exceed maximum size");
    }

    BufferEntry entry = new BufferEntry(instruction);
    instructions_.add(entry);
    instructionHistory_.add(entry);
  }

  @Override
  public void listenForInt(int instructionId, int register, int value) {
    for (BufferEntry entry : instructions_) {
      if (instructionId == entry.getInstruction().getId()) {
        entry.setValue(value);
        entry.getInstruction().changeState(InstructionState.WRITE_BACK, Processor.getCycle());
        entry_ = entry;
        return;
      }
    }
  }

  @Override
  public void listenForDouble(int instructionId, int register, double value) {
    for (BufferEntry entry : instructions_) {
      if (instructionId == entry.getInstruction().getId()) {
        entry.setValue(value);
        entry.getInstruction().changeState(InstructionState.WRITE_BACK, Processor.getCycle());
        entry_ = entry;
        return;
      }
    }
  }

  public void commit(MemoryUnit memoryUnit) {
    BufferEntry firstInstruction = instructions_.peek();

    if (firstInstruction != null && firstInstruction.isReadyToCommit()) {
      Instruction instruction = firstInstruction.getInstruction();
      if (InstructionTypes.isOutputInstruction(instruction.getInstructionType())) {
        int destination = ((DestinationInstruction)instruction).getDestination();

        if (InstructionTypes.isFPInstruction(instruction.getInstructionType())) {
          RegisterFile.getInstance().setRegister(destination, firstInstruction.getFloatValue());
        } else {
          RegisterFile.getInstance().setRegister(destination, firstInstruction.getIntValue());
        }
      } else if (InstructionTypes.STORE_FP == instruction.getInstructionType()) {
        Memory memory = Memory.getInstance();
        MemoryReservationStation station = memoryUnit.getFromQueue(instruction.getId());
        memory.store(station.getAddress(), station.getStoreValue());
        memoryUnit.removeFromQueue(instruction.getId());
      }

      instruction.changeState(InstructionState.COMMIT, Processor.getCycle());
      instructions_.poll();
    }

    // If an instruction was written to the ROB during Write_back, indicate it is ready to commit on the next cycle.
    if (null != entry_) {
      entry_.readyToCommit();
      entry_ = null;
    }
  }

  public boolean complete() {
    return instructions_.isEmpty();
  }

  public String printHistory() {
    StringBuilder builder = new StringBuilder();
    builder.append("Instruction \tIssue\tExecute\tMemory\tWriteBack\tCommit\n");
    for (BufferEntry entry : instructionHistory_) {
      builder.append(entry.getInstruction().printHistory());
    }

    return builder.toString();
  }

  private int size_;
  private BufferEntry entry_;
  private ArrayDeque<BufferEntry> instructions_;

  // Keep a history of the instructions around for debugging/printing out purposes.
  private ArrayList<BufferEntry> instructionHistory_;
}
