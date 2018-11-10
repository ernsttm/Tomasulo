package edu.pitt.ernst.units;

import edu.pitt.ernst.CDB;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.memory.Memory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MemoryUnit extends FunctionalUnit {
  public MemoryUnit(int reservationStations, int memExecCycles, int memAccessCycles) {
    super();

    stations_ = new ArrayList<>();
    for (int i = 0; i < reservationStations; i++) {
      stations_.add(new MemoryReservationStation(this));
    }

    memExecCycles_ = memExecCycles;
    memAccessCycles_ = memAccessCycles;

    memoryQueue_ = new PriorityQueue<>(reservationStations, new Comparator<MemoryReservationStation>() {
      @Override
      public int compare(MemoryReservationStation o1, MemoryReservationStation o2) {
        return o1.getInstructionId() - o2.getInstructionId();
      }
    });
  }

  @Override
  public void execute() {
    // If the address calculation has completed move the instruction to the memory stage, and prepare for the next.
    if (memExecCycles_ == execCycles_) {
      executionStation_.setAddress(executionStation_.getOffset() + executionStation_.getRegister());
      execCycles_ = 0;
      executionStation_ = null;
    }

    // If not currently executing, choose another and start.
    if (0 == execCycles_) {
      int lowestInstruction = Integer.MAX_VALUE;
      for (ReservationStation station : stations_) {
        if (station.isReady() && null == ((MemoryReservationStation)station).getAddress()) {
          if (station.getInstructionId() < lowestInstruction) {
            lowestInstruction = station.getInstructionId();
            executionStation_ = (MemoryReservationStation)station;
          }
        }
      }

      if (null != executionStation_) {
        execCycles_++;
        executionStation_.start();
      }
    } else {
      execCycles_++;
    }
  }

  public void memory() {
    if (memCycles_ >= memAccessCycles_) {
      performOperation(memoryStation_);
      if (tryPublish(memoryStation_)) {
        removeStation(memoryStation_);
        memCycles_ = 0;
        memoryStation_ = null;
      }
    }

    for (MemoryReservationStation station : memoryQueue_) {
      if (InstructionTypes.LOAD_FP == station.getInstructionType() &&
          null != station.getStoreValue()) {
        if (tryPublish(station)) {
          removeStation(station);
        }
      }
    }

    ArrayList<MemoryReservationStation> previousStores = new ArrayList<>();
    for (MemoryReservationStation station : memoryQueue_) {
      if (InstructionTypes.STORE_FP == station.getInstructionType()) {
        if (null == station.getAddress()) {
          break;
        } else {
          previousStores.add(station);
        }
      } else {
        for (MemoryReservationStation prevStation : previousStores) {
          if (null != station.getAddress() &&
              station.getAddress().equals(prevStation.getAddress())) {
            station.setStoreValue(prevStation.getStoreValue());
            station.startMemory();
          }
        }
      }
    }

    if (0 == memCycles_) {
      ArrayList<Integer> prevAddresses = new ArrayList<>();
      for (MemoryReservationStation station : memoryQueue_) {
        if (InstructionTypes.STORE_FP == station.getInstructionType() &&
            null == station.getAddress()) {
          // If there is store whose address is unknown in the queue, then it stalls
          // the rest of the executions (since it could effect later loads)
          break;
        } else {
          if (InstructionTypes.LOAD_FP == station.getInstructionType()) {
            if (!prevAddresses.contains(station.getAddress()) &&
                null != station.getAddress()) {
              memoryStation_ = station;
            }
          } else {
            prevAddresses.add(station.getAddress());

            if (null != station.getAddress() && null != station.getStoreValue()) {
              memoryStation_ = station;
              break;
            }
          }
        }
      }

      if (null != memoryStation_) {
        memCycles_++;
        memoryStation_.startMemory();
      }
    } else {
      memCycles_++;
    }
  }

  @Override
  public boolean tryPublish(ReservationStation station) {
    MemoryReservationStation memStation = (MemoryReservationStation)station;
    if (InstructionTypes.LOAD_FP == station.getInstructionType()) {
      return CDB.getInstance().tryPublish(memStation.getInstructionId(),
          memStation.getDestination(), memStation.getStoreValue());
    } else {
      return CDB.getInstance().tryPublish(memStation.getInstructionId());
    }
  }

  @Override
  public void performOperation(ReservationStation station) {
    MemoryReservationStation memStation = (MemoryReservationStation)station;
    if (InstructionTypes.LOAD_FP == memoryStation_.getInstructionType()) {
      memStation.setStoreValue(Memory.getInstance().loadFP(memoryStation_.getAddress()));
    }
  }

  public void addToQueue(MemoryReservationStation station) {
    memoryQueue_.add(station);
  }

  public MemoryReservationStation getFromQueue(int instructionId) {
    for (MemoryReservationStation station : memoryQueue_) {
      if (station.getInstructionId() == instructionId) {
        return station;
      }
    }

    return null;
  }

  public void removeFromQueue(int instructionId) {
    MemoryReservationStation removeStation = null;
    for (MemoryReservationStation station : memoryQueue_) {
      if (station.getInstructionId() == instructionId) {
        removeStation = station;
        break;
      }
    }

    removeStation.reset();
    memoryQueue_.remove(removeStation);
  }

  private void removeStation(MemoryReservationStation station) {
    if (InstructionTypes.LOAD_FP == station.getInstructionType()) {
      memoryQueue_.remove(station);
      station.reset();
    }
  }

  private int memCycles_;
  private int execCycles_;

  private int memExecCycles_;
  private int memAccessCycles_;

  private MemoryReservationStation memoryStation_;
  private MemoryReservationStation executionStation_;
  private PriorityQueue<MemoryReservationStation> memoryQueue_;
}
