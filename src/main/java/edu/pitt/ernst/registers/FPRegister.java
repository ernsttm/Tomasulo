package edu.pitt.ernst.registers;

public class FPRegister {
  public FPRegister(double value) {
    value_ = value;
  }

  public double getValue() {
    return value_;
  }

  public void setValue(double value) {
    value_ = value;
  }

  private double value_;
}
