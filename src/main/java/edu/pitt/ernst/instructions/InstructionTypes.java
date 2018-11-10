package edu.pitt.ernst.instructions;

import java.util.HashMap;
import java.util.Map;

/**
 * The set of instructions executable on the processor.
 */
public enum InstructionTypes {
  /**
   * Load floating point value from memory.
   */
  LOAD_FP("LD"),

  /**
   * Store floating point value in memory.
   */
  STORE_FP("SD"),

  /**
   * Branch if the given values are equal.
   */
  BRANCH_EQUAL("BEQ"),

  /**
   * Branch if the given values are not equal.
   */
  BRANCH_NOT_EQUAL("BNE"),

  /**
   * Add the two integer values.
   */
  ADD_INT("ADD"),

  /**
   * Add the two floating point values.
   */
  ADD_FP("ADD.D"),

  /**
   * Add the immediate value to the given integer.
   */
  ADD_IMMEDIATE("ADDI"),

  /**
   * Subtract the given integers from each other.
   */
  SUB_INT("SUB"),

  /**
   * Subtract the given floating point values from each other.
   */
  SUB_FP("SUB.D"),

  /**
   * Multiply the given values together.
   */
  MULTIPLY_FP("MULT.D");

  InstructionTypes(String name) {
    name_ = name;
  }

  public String getName() {
    return name_;
  }

  public static InstructionTypes fromString(String typeName) {
    if (!typesMap.containsKey(typeName.toUpperCase())) {
      throw new IllegalArgumentException(typeName + " not contained in the instruction set.");
    }

    return typesMap.get(typeName.toUpperCase());
  }

  /**
   * @return true if the given instruction produces a result for the ARF, otherwise false is returned.
   */
  public static boolean isOutputInstruction(InstructionTypes type) {
    return !(type == STORE_FP || type == BRANCH_EQUAL || type == BRANCH_NOT_EQUAL);
  }

  /**
   * @return true if the given instruction is a floating point type, false otherwise.
   */
  public static boolean isFPInstruction(InstructionTypes type) {
    return (type == LOAD_FP || type == STORE_FP || type == ADD_FP || type == SUB_FP || type == MULTIPLY_FP);
  }

  private String name_;

  private static Map<String, InstructionTypes> typesMap = new HashMap<>();

  static {
    for (InstructionTypes type : InstructionTypes.values()) {
      typesMap.put(type.getName(), type);
    }
  }
}
