package edu.pitt.ernst;

public class TableEntry {
  TableEntry() {
    valid_ = false;

    hardwareId_ = null;
  }

  void setValue(double value, int hardwareId) {
    valid_ = true;
    fpValue_ = value;
    hardwareId_ = hardwareId;
  }

  void setValue(int value, int hardwareId) {
    valid_ = true;
    intValue_ = value;
    hardwareId_ = hardwareId;
  }

  void setHardwareId(int hardwareId) {
    valid_ = false;
    hardwareId_ = hardwareId;
  }

  void reset() {
    valid_ = false;
    hardwareId_ = null;
  }

  boolean isValid() {
    return valid_;
  }

  Integer getHardwareId() {
    return hardwareId_;
  }

  int getInt() {
    return intValue_;
  }

  double getFloat() {
    return fpValue_;
  }

  private boolean valid_;
  private int intValue_;
  private double fpValue_;
  private Integer hardwareId_;
}
