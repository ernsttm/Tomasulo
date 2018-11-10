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
      fpRegMap_.put(i, null);
      intRegMap_.put(i, null);
    }
  }

  public Integer getRegister(int fileRegister, boolean useFP) {
    if (useFP) {
      return fpRegMap_.get(fileRegister);
    } else {
      return intRegMap_.get(fileRegister);
    }
  }

  public int reserve(int fileRegister, boolean useFP) {
    ArrayDeque<Integer> freeList;
    Map<Integer, Integer> regMap;
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
    regMap.put(fileRegister, hardwareId);
    return hardwareId;
  }

  private ArrayDeque<Integer> fpFreeList_;
  private Map<Integer, Integer> fpRegMap_;

  private ArrayDeque<Integer> intFreeList_;
  private Map<Integer, Integer> intRegMap_;
}
