package edu.pitt.ernst;

import edu.pitt.ernst.config.ProcessorConfig;
import edu.pitt.ernst.config.ReservationStationConfig;
import edu.pitt.ernst.instructions.BranchInstruction;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionBuffer;
import edu.pitt.ernst.instructions.InstructionTypes;
import edu.pitt.ernst.memory.Memory;
import edu.pitt.ernst.registers.RegisterFile;
import edu.pitt.ernst.rob.InstructionState;
import edu.pitt.ernst.rob.ReorderBuffer;
import edu.pitt.ernst.units.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Processor {
  public Processor(ProcessorConfig config) {
    cycle_ = 1;
    stopCycle_ = 0;
    mispredictedInstruction_ = null;

    rats_ = new HashMap<>();

    BTB.createInstance();
    Memory.createInstance(config.getMemoryConfig().getStartValues());
    RegisterFile.createInstance(config.getRegistersConfig());

    ReservationStationConfig stationConfig = config.getReservationStationConfig();
    alu_ = new ALU(stationConfig.getAdderRS(), stationConfig.getAdderExecCycles(), this);
    fpAlu_ = new FloatingPointALU(stationConfig.getFPAdderRS(), stationConfig.getFPAdderExecCycles());
    fpMlu_ = new FloatingPointMLU(stationConfig.getFPMultiplierRS(), stationConfig.getFPMultiplierExecCycles());
    memUnit_ = new MemoryUnit(stationConfig.getMemoryRS(), stationConfig.getMemoryExecCycles(),
        stationConfig.getMemoryAccessCycles());
    rob_ = new ReorderBuffer(config.getRobEntries());
    rat_ = new RegisterAliasingTable(config.getHardwareIntRegisters(), config.getHardwareFPRegisters());
    instructionBuffer_ = new InstructionBuffer(config.getInstructionFile());
  }

  public void executeProgram() {
    while (instructionBuffer_.hasNext() || !rob_.complete()) {
      executeCycle();
    }

    outputState();
  }

  public boolean executeCycle() {
    if (!instructionBuffer_.hasNext() && rob_.complete()) {
      return true;
    }

    if (stopCycle_ == cycle_) {
      handleInput();
    }

    // Handle issuing the next instruction
    issue();

    // Execute any instructions which have all the necessary parameters.
    execute();

    // Execute the Memory phase of the pipeline
    memory();

    // If the CDB has a value, write it back.
    CDB.getInstance().writeBack(rat_);

    // Finally commit an operation
    rob_.commit(memUnit_, rat_, rats_);

    if (null != mispredictedInstruction_) {
      BranchInstruction bI = (BranchInstruction)mispredictedInstruction_;
      instructionBuffer_.setAddress(bI.getAddress());
      if (branchTaken_) {
        instructionBuffer_.jump(bI.getOffset());
      }
      // A misprediction has occurred, revert state and charge an extra cycle.
      rollbackStations();
      RegisterAliasingTable oldRat = rats_.get(mispredictedInstruction_.getId());
      rat_.rectify(oldRat);
      rat_ = oldRat;
      rob_.branchRollback(mispredictedInstruction_.getId());
      cycle_++;
      mispredictedInstruction_ = null;
    }
    cycle_++;

    return !instructionBuffer_.hasNext() && rob_.complete();
  }

  public void triggerBranchMisprediction(Instruction instruction, boolean taken) {
    branchTaken_ = taken;
    mispredictedInstruction_ = instruction;
  }

  public void outputState() {
    StringBuilder outputString = new StringBuilder(rob_.printHistory());
    outputString.append(RegisterFile.getInstance().toString());
    outputString.append(Memory.getInstance().toString());

    if (null != outputFile_) {
      try {
        Files.write(Paths.get(outputFile_), outputString.toString().getBytes());
      } catch (IOException ie) {
        System.out.println("Failed to write output file.");
      }
    } else {
      System.out.println(outputString.toString());
    }
  }

  private void issue() {
    if (instructionBuffer_.hasNext()) {
      Instruction instruction = instructionBuffer_.getNext();

      ReservationStation station = null;
      switch (instruction.getInstructionType()) {
        case ADD_INT:
        case SUB_INT:
        case ADD_IMMEDIATE:
        case BRANCH_EQUAL:
        case BRANCH_NOT_EQUAL:
          station = alu_.getReservationStation();
          break;
        case ADD_FP:
        case SUB_FP:
          station = fpAlu_.getReservationStation();
          break;
        case MULTIPLY_FP:
          station = fpMlu_.getReservationStation();
          break;
        case LOAD_FP:
        case STORE_FP:
          station = memUnit_.getReservationStation();
          break;
      }

      if (null != station) {
        instruction.changeState(InstructionState.ISSUE, cycle_);
        station.reserve(instruction, rat_);
        rob_.addInstruction(instruction);

        if (InstructionTypes.BRANCH_EQUAL == instruction.getInstructionType() ||
            InstructionTypes.BRANCH_NOT_EQUAL == instruction.getInstructionType()) {
          rats_.put(instruction.getId(), rat_);
          rat_ = new RegisterAliasingTable(rat_);
          if (BTB.getInstance().predict(instructionBuffer_.getPreviousInstructionAddress(), instruction)) {
            int offset = ((BranchInstruction)instruction).getOffset();
            instructionBuffer_.jump(offset);
          }
        }
      } else {
        instructionBuffer_.requeue();
      }
    }
  }

  private void execute() {
    alu_.execute();
    fpAlu_.execute();
    fpMlu_.execute();
    memUnit_.execute();
  }

  private void rollbackStations() {
    alu_.branchRollback(mispredictedInstruction_.getId());
    fpAlu_.branchRollback(mispredictedInstruction_.getId());
    fpMlu_.branchRollback(mispredictedInstruction_.getId());
    memUnit_.branchRollback(mispredictedInstruction_.getId());
  }

  private void memory() {
    memUnit_.memory();
  }

  // A method to enter commands to the processor, to aid in debugging.
  private void handleInput() {

  }

  public static void main(String[] args) {
    ProcessorConfig config = ProcessorConfig.createInstance(args[0]);

    if (args.length > 1) {
      outputFile_ = args[1];
    }

    Processor processor = new Processor(config);
    processor.executeCycle();
  }

  public static int getCycle() {
    return cycle_;
  }

  private int stopCycle_;
  private boolean branchTaken_;
  private Instruction mispredictedInstruction_;

  private ALU alu_;
  private MemoryUnit memUnit_;
  private FloatingPointALU fpAlu_;
  private FloatingPointMLU fpMlu_;
  private ReorderBuffer rob_;
  private RegisterAliasingTable rat_;
  private InstructionBuffer instructionBuffer_;
  private Map<Integer, RegisterAliasingTable> rats_;

  private static String outputFile_ = null;

  private static int cycle_;
}