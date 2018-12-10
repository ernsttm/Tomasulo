package edu.pitt.ernst;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class RegisterAliasingTable {
  public RegisterAliasingTable(int hardwareIntRegisters, int hardwareFPRegisters) {
    fpFreeList_ = new ArrayDeque<>();
    for (int i = 0; i < hardwareFPRegisters; i++) {
      fpFreeList_.add(i);
    }

    intFreeList_ = new ArrayDeque<>();
    for (int i = 0; i < hardwareIntRegisters; i++) {
      intFreeList_.add(i);

    }

    fpRegMap_ = new HashMap<>();
    intRegMap_ = new HashMap<>();
    for (int i = 0; i < 32; i++) {
      fpRegMap_.put(i, new TableEntry());
      intRegMap_.put(i, new TableEntry());
    }
  }

  public RegisterAliasingTable(RegisterAliasingTable oldRat) {
    fpFreeList_ = oldRat.getFpFreeList();
    intFreeList_ = oldRat.getIntFreeList();

    fpRegMap_ = new HashMap<>();
    intRegMap_ = new HashMap<>();
    for (int i = 0; i < 32; i++) {
      TableEntry fpEntry = new TableEntry();
      TableEntry intEntry = new TableEntry();

      if (oldRat.isValidRegister(i, false)) {
        TableEntry oldEntry = oldRat.getIntEntry(i);
        intEntry.setValue(oldEntry.getInt(), oldEntry.getHardwareId());
      } else if (oldRat.isReservedRegister(i, false)) {
        intEntry.setHardwareId(oldRat.getHardwareRegister(i, false));
      }

      if (oldRat.isValidRegister(i, true)) {
        TableEntry oldEntry = oldRat.getIntEntry(i);
        fpEntry.setValue(oldEntry.getFloat(), oldEntry.getHardwareId());
      } else if (oldRat.isReservedRegister(i, true)) {
        fpEntry.setHardwareId(oldRat.getHardwareRegister(i, true));
      }

      fpRegMap_.put(i, fpEntry);
      intRegMap_.put(i, intEntry);
    }
  }

  public boolean isReservedRegister(int fileRegister, boolean useFp) {
    if (useFp) {
      return fpRegMap_.get(fileRegister).getHardwareId() != null;
    } else {
      return intRegMap_.get(fileRegister).getHardwareId() != null;
    }
  }

  public boolean isValidRegister(int fileRegister, boolean useFP) {
    if (useFP) {
      return fpRegMap_.get(fileRegister).isValid();
    } else {
      return intRegMap_.get(fileRegister).isValid();
    }
  }

  public int getHardwareRegister(int fileRegister, boolean useFp) {
    if (useFp) {
      return fpRegMap_.get(fileRegister).getHardwareId();
    } else {
      return intRegMap_.get(fileRegister).getHardwareId();
    }
  }

  public int getIntRegisterValue(int fileRegister) {
    return intRegMap_.get(fileRegister).getInt();
  }

  public double getFloatRegisterValue(int fileRegister) {
    return fpRegMap_.get(fileRegister).getFloat();
  }

  public void setIntRegister(int hardwareId, int value) {
    for (Map.Entry<Integer, TableEntry> entry : intRegMap_.entrySet()) {
      if (null != entry.getValue().getHardwareId() && entry.getValue().getHardwareId() == hardwareId) {
        entry.getValue().setValue(value, hardwareId);
      }
    }
  }

  public void setFpRegister(int hardwareId, double value) {
    for (Map.Entry<Integer, TableEntry> entry : fpRegMap_.entrySet()) {
      if (null != entry.getValue().getHardwareId() && entry.getValue().getHardwareId() == hardwareId) {
        entry.getValue().setValue(value, hardwareId);
      }
    }
  }

  public int reserve(int fileRegister, boolean useFP) {
    ArrayDeque<Integer> freeList;
    Map<Integer, TableEntry> regMap;
    if (useFP) {
      freeList = fpFreeList_;
      regMap = fpRegMap_;
    } else {
      freeList = intFreeList_;
      regMap = intRegMap_;
    }

    if (freeList.isEmpty()) {
      throw new IllegalStateException("No hardware stations to reserve");
    }

    int hardwareId = freeList.poll();
    regMap.get(fileRegister).setHardwareId(hardwareId);
    return hardwareId;
  }

  public void freeRegister(int register, boolean useFP) {
    ArrayDeque<Integer> freeList;
    Map<Integer, TableEntry> regMap;
    if (useFP) {
      freeList = fpFreeList_;
      regMap = fpRegMap_;
    } else {
      freeList = intFreeList_;
      regMap = intRegMap_;
    }

    freeList.push(register);
    for (Map.Entry<Integer, TableEntry> entry : regMap.entrySet()) {
      if (null != entry.getValue().getHardwareId() && entry.getValue().getHardwareId() == register) {
        entry.getValue().reset();
        break;
      }
    }
  }

  protected ArrayDeque<Integer> getFpFreeList() {
    return new ArrayDeque<>(fpFreeList_);
  }

  protected TableEntry getFpEntry(int entryId) {
    return fpRegMap_.get(entryId);
  }

  protected ArrayDeque<Integer> getIntFreeList() {
    return new ArrayDeque<>(intFreeList_);
  }

  protected TableEntry getIntEntry(int entryId) {
    return intRegMap_.get(entryId);
  }

  private ArrayDeque<Integer> fpFreeList_;
  private Map<Integer, TableEntry> fpRegMap_;

  private ArrayDeque<Integer> intFreeList_;
  private Map<Integer, TableEntry> intRegMap_;
}
