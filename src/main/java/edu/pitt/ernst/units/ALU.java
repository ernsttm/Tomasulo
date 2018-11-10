package edu.pitt.ernst.units;

import edu.pitt.ernst.CDB;

import java.util.ArrayList;

/**
 * This class represents an integer arithmetic processing unit.
 */
public class ALU extends FunctionalUnit {
  public ALU(int reservationStations, int adderExecCycles) {
    super();

    stations_ = new ArrayList<>();
    for (int i = 0; i < reservationStations; i++) {
      stations_.add(new ALUReservationStation());
    }

    executeCyclesRequired_ = adderExecCycles;
  }

  // The ALU is the only functional unit which is not pipelined, and thus cannot take advantage of
  // the general implementation.
  @Override
  public void execute() {
    // If an operation has completed attempt to publish it and reset the Functional Unit state.
    if (execCycles_ >= executeCyclesRequired_) {
      if (tryPublish(stationToExecute_)) {
        stationToExecute_.reset();
        execCycles_ = 0;
        stationToExecute_ = null;
      }
    }

    // If not currently executing an instruction choose the earliest ready in the reservation
    // stations.
    if (0 == execCycles_) {
      // Choose the earliest instruction.
      int lowestInstruction = Integer.MAX_VALUE;
      for (ReservationStation station : stations_) {
        if (station.isReady()) {
          if (station.getInstructionId() < lowestInstruction) {
            lowestInstruction = station.getInstructionId();
            stationToExecute_ = station;
          }
        }
      }

      // An operation is ready to be executed, begin it.
      if (null != stationToExecute_) {
        execCycles_++;
        stationToExecute_.start();
        performOperation(stationToExecute_);
      }
    } else {
      execCycles_++;
    }
  }

  @Override
  protected boolean tryPublish(ReservationStation station) {
    ALUReservationStation aluRS = (ALUReservationStation) station;
    return CDB.getInstance().tryPublish(aluRS.getInstructionId(), aluRS.getDestination(), value_);
  }

  @Override
  protected void performOperation(ReservationStation station) {
    ALUReservationStation aluRS = (ALUReservationStation) station;
    switch (aluRS.getInstructionType()) {
      case ADD_INT:
        value_ = aluRS.getOp1() + aluRS.getOp2();
        break;
      case SUB_INT:
        value_ = aluRS.getOp1() - aluRS.getOp2();
        break;
      case ADD_IMMEDIATE:
        value_ = aluRS.getOp1() + aluRS.getOp2();
        break;
    }
  }

  private int value_;
  private int execCycles_;
  private ReservationStation stationToExecute_;
}
