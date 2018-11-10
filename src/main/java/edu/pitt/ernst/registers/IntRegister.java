package edu.pitt.ernst.registers;

public class IntRegister {
  public IntRegister(int value) {
    value_ = value;
  }

  public int getValue() {
    return value_;
  }

  public void setValue(int value) {
    value_ = value;
  }

  private int value_;
}
