package edu.pitt.ernst;

import java.util.ArrayList;

/**
 * This class mimics the functionality of the Central Data Bus inside a processor.  Only one result will be allowed to
 * write to the CDB per cycle, and we will use first-come-first-served as the arbitration policy.
 */
public class CDB {
  // Use a private constructor to guarantee no-one bypasses the singleton model.
  private CDB() { }

  public boolean tryPublish(int instructionId) {
    if (!inUse_) {
      inUse_ = true;
      instructionId_ = instructionId;
      return true;
    }

    return false;
  }

  public boolean tryPublish(int instructionId, int register, int value) {
    if (!inUse_) {
      inUse_ = true;
      intValue_ = value;
      register_ = register;
      instructionId_ = instructionId;
      return true;
    }

    return false;
  }

  public boolean tryPublish(int instructionId, int register, double value) {
    if (!inUse_) {
      inUse_ = true;
      doubleValue_ = value;
      register_ = register;
      instructionId_ = instructionId;
      return true;
    }

    return false;
  }

  public void writeBack() {
    if (inUse_) {
      if (null != intValue_) {
        for (CDBListener listener : listeners) {
          listener.listenForInt(instructionId_, register_, intValue_);
        }
      } else if (null != doubleValue_) {
        for (CDBListener listener : listeners) {
          listener.listenForDouble(instructionId_, register_, doubleValue_);
        }
      } else {
        for (CDBListener listener : listeners) {
          listener.listenForInt(instructionId_, -1, -1);
        }
      }

      inUse_ = false;
      intValue_ = null;
      doubleValue_ = null;
    }
  }

  public void register(CDBListener listener) {
    listeners.add(listener);
  }

  public static CDB getInstance() {
    if (null == instance_) {
      instance_ = new CDB();
    }

    return instance_;
  }

  private int register_;
  private int instructionId_;
  private boolean inUse_;
  private Integer intValue_ = null;
  private Double doubleValue_ = null;
  private ArrayList<CDBListener> listeners = new ArrayList<>();

  private static CDB instance_ = null;
}
