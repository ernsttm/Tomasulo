package edu.pitt.ernst.instructions;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.*;

public class TestInstructionBuffer {
  @Test
  public void testBuffer() throws URISyntaxException {
    ClassLoader loader = getClass().getClassLoader();
    URL url = loader.getResource("java/edu/pitt/ernst/instructions/Instructions.txt");
    File instructionFile = new File(url.toURI());

    InstructionBuffer buffer = new InstructionBuffer(instructionFile.getAbsolutePath());
    Instruction instruction = buffer.getNext();
    assertEquals(InstructionTypes.ADD_INT, instruction.getInstructionType());
    assertEquals(1, ((ALUInstruction)instruction).getDestination());
    assertEquals(2, ((ALUInstruction)instruction).getOp1());
    assertEquals(3, ((ALUInstruction)instruction).getOp2());

    instruction = buffer.getNext();
    assertEquals(InstructionTypes.BRANCH_EQUAL, instruction.getInstructionType());
    assertEquals(4, ((BranchInstruction)instruction).getOp1());
    assertEquals(5, ((BranchInstruction)instruction).getOp2());
    assertEquals(6, ((BranchInstruction)instruction).getOffset());

    instruction = buffer.getNext();
    assertEquals(InstructionTypes.STORE_FP, instruction.getInstructionType());
    assertEquals(7, ((MemoryInstruction)instruction).getDestination());
    assertEquals(9, ((MemoryInstruction)instruction).getRegister());
    assertEquals(8, ((MemoryInstruction)instruction).getOffset());
  }
}
