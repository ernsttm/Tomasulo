package edu.pitt.ernst.instructions;

/**
 * Instructions which have an output register implement this interface, to allow the processor to know where to write to
 */
public interface DestinationInstruction {
  int getDestination();
}
