package edu.pitt.ernst.units;

import edu.pitt.ernst.CDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FloatingPointMLU extends FunctionalUnit {
  public FloatingPointMLU(int reservationStations, int fpMultiplierCycles) {
    super();

    stations_ = new ArrayList<>();
    for (int i = 0; i < reservationStations; i++) {
      stations_.add(new FloatingPointALURS());
    }

    executeCyclesRequired_ = fpMultiplierCycles;
  }

  @Override
  protected boolean tryPublish(ReservationStation station) {
    FloatingPointALURS fpRS = (FloatingPointALURS)station;
    double value = operationValues_.get(station.getInstructionId());
    operationValues_.remove(station.getInstructionId());
    return CDB.getInstance().tryPublish(fpRS.getInstructionId(), fpRS.getDestination(), value);
  }

  @Override
  protected void performOperation(ReservationStation station) {
    FloatingPointALURS fpRS = (FloatingPointALURS)station;
    switch (fpRS.getInstructionType()) {
      case MULTIPLY_FP:
        operationValues_.put(station.getInstructionId(), fpRS.getOp1() * fpRS.getOp2());
        break;
    }
  }

  private Map<Integer, Double> operationValues_ = new HashMap<>();
}
