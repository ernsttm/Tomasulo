package edu.pitt.ernst.units;

import edu.pitt.ernst.CDB;
import edu.pitt.ernst.CDBListener;
import edu.pitt.ernst.Processor;

import java.util.*;

/**
 * A class representing unit of capability within a CPU. Some examples are FloatingPointALU, ALU,
 * Branching Unit.
 */
public abstract class FunctionalUnit implements CDBListener {
  FunctionalUnit() {
    executingStations_ = new PriorityQueue<>(10, new Comparator<ReservationStation>() {
      @Override
      public int compare(ReservationStation o1, ReservationStation o2) {
        return o1.getInstructionId() - o2.getInstructionId();
      }
    });

    CDB.getInstance().register(this);
  }

  public ReservationStation getReservationStation() {
    for (ReservationStation station : stations_) {
      if (!station.isBusy()) {
        return station;
      }
    }

    return null;
  }

  public void branchRollback(int id) {
    for (ReservationStation station : stations_) {
      if (station.isBusy() && id < station.getInstructionId()) {
        station.reset();
      }
    }
  }

  public void execute() {
    // First choose the earliest instruction ready and attempt to publish it
    ReservationStation stationToRemove = null;
    for (ReservationStation station : executingStations_) {
      if (startCycles_.containsKey(station) &&
          Processor.getCycle() - startCycles_.get(station) >= executeCyclesRequired_) {
        if (tryPublish(station)) {
          station.reset();
          stationToRemove = station;
          break;
        }
      }
    }

    if (null != stationToRemove) {
      startCycles_.remove(stationToRemove);
      executingStations_.remove(stationToRemove);
    }

    // Then iterate through all the reservations and add any station ready to start executing
    for (ReservationStation station : stations_) {
      if (station.isReady() && !executingStations_.contains(station)) {
        executingStations_.add(station);
      }
    }

    // Finally trigger the first instruction not being executed to start.
    for (ReservationStation station : executingStations_) {
      if (!startCycles_.containsKey(station)) {
        station.start();
        performOperation(station);
        startCycles_.put(station, Processor.getCycle());
        // Only one station can start on the pipeline per cycle.
        break;
      }
    }
  }

  protected abstract boolean tryPublish(ReservationStation station);

  protected abstract void performOperation(ReservationStation station);

  @Override
  public void listenForInt(int instructionId, int register, int value) {
    for (ReservationStation station : stations_) {
      station.intUpdate(register, value);
    }
  }

  @Override
  public void listenForDouble(int instructionId, int register, double value) {
    for (ReservationStation station : stations_) {
      station.doubleUpdate(register, value);
    }
  }

  protected int executeCyclesRequired_;
  protected Collection<ReservationStation> stations_;
  private PriorityQueue<ReservationStation> executingStations_;
  private Map<ReservationStation, Integer> startCycles_ = new HashMap<>();
}
