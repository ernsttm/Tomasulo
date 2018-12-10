package edu.pitt.ernst.rob;

import edu.pitt.ernst.CDB;
import edu.pitt.ernst.CDBListener;
import edu.pitt.ernst.Processor;
import edu.pitt.ernst.RegisterAliasingTable;
import edu.pitt.ernst.instructions.DestinationInstruction;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.memory.Memory;
import edu.pitt.ernst.registers.RegisterFile;
import edu.pitt.ernst.units.MemoryReservationStation;
import edu.pitt.ernst.units.MemoryUnit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;

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
        entry.setValue(register, value);
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
        entry.setValue(register, value);
        entry.getInstruction().changeState(InstructionState.WRITE_BACK, Processor.getCycle());
        entry_ = entry;
        return;
      }
    }
  }

  public void branchRollback(int id) {
    // A branch prediction has failed.  Rollback all entries in the buffer since the branch
    ArrayList<BufferEntry> entriesToRemove = new ArrayList<>();
    for (BufferEntry entry : instructions_) {
      if (entry.getInstruction().getId() > id) {
        entriesToRemove.add(entry);
      }
    }

    for (BufferEntry entryToRemove : entriesToRemove) {
      instructions_.remove(entryToRemove);
    }
  }

  public void commit(MemoryUnit memoryUnit, RegisterAliasingTable rat, Map<Integer, RegisterAliasingTable> rats) {
    BufferEntry commitEntry = instructions_.peek();

    if (commitEntry != null && commitEntry.isReadyToCommit()) {
      Instruction instruction = commitEntry.getInstruction();
      if (InstructionTypes.isOutputInstruction(instruction.getInstructionType())) {
        int destination = ((DestinationInstruction)instruction).getDestination();

        if (InstructionTypes.isFPInstruction(instruction.getInstructionType())) {
          rat.freeRegister(commitEntry.getDestination(), true);
          for (RegisterAliasingTable oldRat : rats.values()) {
            oldRat.freeRegister(commitEntry.getDestination(), true);
          }

          RegisterFile.getInstance().setRegister(destination, commitEntry.getFloatValue());
        } else {
          rat.freeRegister(commitEntry.getDestination(), false);
          for (RegisterAliasingTable oldRat : rats.values()) {
            oldRat.freeRegister(commitEntry.getDestination(), false);
          }

          RegisterFile.getInstance().setRegister(destination, commitEntry.getIntValue());
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
