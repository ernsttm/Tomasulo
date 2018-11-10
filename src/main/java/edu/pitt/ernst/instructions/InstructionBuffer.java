package edu.pitt.ernst.instructions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.regex.Pattern;

public class InstructionBuffer {
  public InstructionBuffer(String instructionFile) {
    instructionCounter_ = 0;
    int lineNumber = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(instructionFile))) {
      String line = reader.readLine();
      while (null != line) {
        String[] codons = line.split(" ");

        InstructionTypes type = InstructionTypes.fromString(codons[0]);
        switch (type) {
          case LOAD_FP:
          case STORE_FP: {
            int destination = Integer.parseInt(codons[1].replace(',', ' ').trim());
            String[] regOffsetPair = codons[2].split(Pattern.quote("("));
            int offset = Integer.parseInt(regOffsetPair[0].trim());
            int register = Integer.parseInt(regOffsetPair[1].replace(")", " ").trim());
            instructions_.add(new MemoryInstruction(type, destination, register, offset));
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
            instructions_.add(new ALUInstruction(type, destination, op1, op2));
            break;
          }
          case BRANCH_EQUAL:
          case BRANCH_NOT_EQUAL: {
            int op1 = Integer.parseInt(codons[1].replace(',', ' ').trim());
            int op2 = Integer.parseInt(codons[2].replace(',', ' ').trim());
            int offset = Integer.parseInt(codons[3].trim());
            instructions_.add(new BranchInstruction(type, op1, op2, offset));
            break;
          }
        }

        lineNumber++;
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
    return instructions_.poll();
  }

  /**
   * Add the given instruction to the beginning of the instruction buffer.  This is especially useful if an instruction
   * cannot be issued this cycle, and must wait.
   *
   * @param instruction the instruction to requeue.
   */
  public void requeue(Instruction instruction) {
    instructions_.addFirst(instruction);
  }

  public boolean hasNext() {
    return instructions_.size() > 0;
  }

  public static int countInstruction() {
    return instructionCounter_++;
  }

  private ArrayDeque<Instruction> instructions_ = new ArrayDeque<>();
  private static int instructionCounter_;
}
