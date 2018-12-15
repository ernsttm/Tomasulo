package edu.pitt.ernst.instructions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class InstructionBuffer {
  public InstructionBuffer(String instructionFile) {
    instructionPointer_ = 0;
    instructionCounter_ = 0;
    int lineNumber = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(instructionFile))) {
      String line = reader.readLine();
      while (null != line) {
        instructions_.add(line);

        line = reader.readLine();
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Instruction file is invalid at line : " + lineNumber, e);
    }
  }

  /**
   * @return the next instruction in the queue.
   */
  public Instruction getNext() {
    int address = instructionPointer_++;
    String inst = instructions_.get(address);
    String[] codons =  inst.split(" ");

    Instruction instruction = null;
    InstructionTypes type = InstructionTypes.fromString(codons[0]);
    switch (type) {
      case LOAD_FP:
      case STORE_FP: {
        int destination = Integer.parseInt(codons[1].replace(',', ' ').trim());
        String[] regOffsetPair = codons[2].split(Pattern.quote("("));
        int offset = Integer.parseInt(regOffsetPair[0].trim());
        int register = Integer.parseInt(regOffsetPair[1].replace(")", " ").trim());
        instruction = new MemoryInstruction(type, destination, register, offset, address, inst);
        break;
      }
      case ADD_INT:
      case SUB_INT:
      case ADD_IMMEDIATE:
      case ADD_FP:
      case SUB_FP:
      case MULTIPLY_FP: {
        int destination = Integer.parseInt(codons[1].replace(',', ' ').trim());
        int op1 = Integer.parseInt(codons[2].replace(',', ' ').trim());
        int op2 = Integer.parseInt(codons[3].trim());
        instruction = new ALUInstruction(type, destination, op1, op2, address, inst);
        break;
      }
      case BRANCH_EQUAL:
      case BRANCH_NOT_EQUAL: {
        int op1 = Integer.parseInt(codons[1].replace(',', ' ').trim());
        int op2 = Integer.parseInt(codons[2].replace(',', ' ').trim());
        int offset = Integer.parseInt(codons[3].trim());
        instruction = new BranchInstruction(type, op1, op2, offset, address, inst);
        break;
      }
    }

    return instruction;
  }

  /**
   * Cause the instruction pointer to "jump" the given number of addresses.
   *
   * @param offset the number of instructions to skip;
   */
  public void jump(int offset) {
    instructionPointer_ += offset;
  }

  public void setAddress(int address) {
    instructionPointer_ = address + 1;
  }

  public int getPreviousInstructionAddress() {
    return instructionPointer_ - 1;
  }

  /**
   * Add the given instruction to the beginning of the instruction buffer.  This is especially useful if an instruction
   * cannot be issued this cycle, and must wait.
   */
  public void requeue() {
    instructionPointer_--;
  }

  public boolean hasNext() {
    return instructionPointer_ < instructions_.size();
  }

  public static int countInstruction() {
    return instructionCounter_++;
  }

  private int instructionPointer_;
  private ArrayList<String> instructions_ = new ArrayList<>();
  private static int instructionCounter_;
}
