package edu.pitt.ernst.registers;

public class ZeroRegister extends IntRegister {
  public ZeroRegister() {
    super(0);
  }

  @Override
  public void setValue(int value) {
    throw new IllegalStateException("Can't set the value of the zero register");
  }
}
