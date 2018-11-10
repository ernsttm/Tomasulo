package edu.pitt.ernst;

public interface CDBListener {
  void listenForInt(int instructionId, int register, int value);
  void listenForDouble(int instructionId, int register, double value);
}
