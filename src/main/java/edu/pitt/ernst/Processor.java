package edu.pitt.ernst;

import edu.pitt.ernst.config.ProcessorConfig;
import edu.pitt.ernst.config.ReservationStationConfig;
import edu.pitt.ernst.instructions.Instruction;
import edu.pitt.ernst.instructions.InstructionBuffer;
import edu.pitt.ernst.memory.Memory;
import edu.pitt.ernst.registers.RegisterFile;
import edu.pitt.ernst.rob.InstructionState;
import edu.pitt.ernst.rob.ReorderBuffer;
import edu.pitt.ernst.units.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Processor {
  public Processor(ProcessorConfig config) {
    cycle_ = 1;
    stopCycle_ = 0;

    Memory.createInstance(config.getMemoryConfig().getStartValues());
    RegisterFile.createInstance(config.getRegistersConfig());

    ReservationStationConfig stationConfig = config.getReservationStationConfig();
    alu_ = new ALU(stationConfig.getAdderRS(), stationConfig.getAdderExecCycles());
    fpAlu_ = new FloatingPointALU(stationConfig.getFPAdderRS(), stationConfig.getFPAdderExecCycles());
    fpMlu_ = new FloatingPointMLU(stationConfig.getFPMultiplierRS(), stationConfig.getFPMultiplierExecCycles());
    memUnit_ = new MemoryUnit(stationConfig.getMemoryRS(), stationConfig.getMemoryExecCycles(),
        stationConfig.getMemoryAccessCycles());
    rob_ = new ReorderBuffer(config.getRobEntries());
    rat_ = new RegisterAliasingTable(config.getHardwareIntRegisters(), config.getHardwareFPRegisters());
    instructionBuffer_ = new InstructionBuffer(config.getInstructionFile());
  }

  public void executeCycle() {
    while (instructionBuffer_.hasNext() || !rob_.complete()) {
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
      CDB.getInstance().writeBack();

      // Finally commit an operation
      rob_.commit(memUnit_);

      cycle_++;
    }

    outputResults();
  }

  private void issue() {
    if (instructionBuffer_.hasNext()) {
      Instruction instruction = instructionBuffer_.getNext();

      ReservationStation station = null;
      switch (instruction.getInstructionType()) {
        case ADD_INT:
        case SUB_INT:
        case ADD_IMMEDIATE:
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
      } else {
        instructionBuffer_.requeue(instruction);
      }
    }
  }

  private void execute() {
    alu_.execute();
    fpAlu_.execute();
    fpMlu_.execute();
    memUnit_.execute();
  }

  private void memory() {
    memUnit_.memory();
  }

  // A method to enter commands to the processor, to aid in debugging.
  private void handleInput() {

  }

  private void outputResults() {
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

  private ALU alu_;
  private MemoryUnit memUnit_;
  private FloatingPointALU fpAlu_;
  private FloatingPointMLU fpMlu_;
  private ReorderBuffer rob_;
  private RegisterAliasingTable rat_;
  private InstructionBuffer instructionBuffer_;

  private static String outputFile_ = null;

  private static int cycle_;
}