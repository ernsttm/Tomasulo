package edu.pitt.ernst.units;

import edu.pitt.ernst.CDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FloatingPointALU extends FunctionalUnit {
  public FloatingPointALU(int reservationStations, int fpAdderCycles) {
    super();

    stations_ = new ArrayList<>();
    for (int i = 0; i < reservationStations; i++) {
      stations_.add(new FloatingPointALURS());
    }

    executeCyclesRequired_ = fpAdderCycles;
  }

  @Override
  protected boolean tryPublish(ReservationStation station) {
    FloatingPointALURS fpRS = (FloatingPointALURS)station;
    double value = operationValues_.get(station.getInstructionId());
    if (CDB.getInstance().tryPublish(fpRS.getInstructionId(), fpRS.getDestination(), value)) {
      operationValues_.remove(station.getInstructionId());
      return true;
    }
    return false;
  }

  @Override
  protected void performOperation(ReservationStation station) {
    FloatingPointALURS fpRS = (FloatingPointALURS)station;
    switch (fpRS.getInstructionType()) {
      case ADD_FP:
        operationValues_.put(station.getInstructionId(), fpRS.getOp1() + fpRS.getOp2());
        break;
      case SUB_FP:
        operationValues_.put(station.getInstructionId(), fpRS.getOp1() - fpRS.getOp2());
        break;
    }
  }

  private Map<Integer, Double> operationValues_ = new HashMap<>();
}
