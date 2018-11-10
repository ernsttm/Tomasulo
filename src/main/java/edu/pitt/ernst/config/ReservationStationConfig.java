package edu.pitt.ernst.config;

import com.google.gson.annotations.SerializedName;

public class ReservationStationConfig {
  public int getAdderRS() {
    return adderRS_;
  }

  public int getFPAdderRS() {
    return fpAdderRS_;
  }

  public int getFPMultiplierRS() {
    return fpMultiplierRS_;
  }

  public int getMemoryRS() {
    return memoryRS_;
  }

  public int getAdderExecCycles() {
    return adderExecCycles_;
  }

  public int getFPAdderExecCycles() {
    return fpAdderExecCycles_;
  }

  public int getFPMultiplierExecCycles() {
    return fpMultiplierExecCycles_;
  }

  public int getMemoryExecCycles() {
    return memoryExecCycles_;
  }

  public int getMemoryAccessCycles() {
    return memoryAccessCycles_;
  }

  @SerializedName("adder_stations")
  private int adderRS_;

  @SerializedName("fp_adder_stations")
  private int fpAdderRS_;

  @SerializedName("fp_multiplier_stations")
  private int fpMultiplierRS_;

  @SerializedName("memory_stations")
  private int memoryRS_;

  @SerializedName("adder_cycles")
  private int adderExecCycles_;

  @SerializedName("fp_adder_cycles")
  private int fpAdderExecCycles_;

  @SerializedName("fp_multiplier_cycles")
  private int fpMultiplierExecCycles_;

  @SerializedName("memory_exec_cycles")
  private int memoryExecCycles_;

  @SerializedName("memory_access_cycles")
  private int memoryAccessCycles_;
}
