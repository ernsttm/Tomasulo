package edu.pitt.ernst.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.FileReader;

public class ProcessorConfig {
  public static ProcessorConfig createInstance(String configFile) {
    // Try and read/parse the file.
    try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
      config_ = new Gson().fromJson(br, ProcessorConfig.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to parse configuration file", e);
    }

    return config_;
  }

  public static ProcessorConfig getInstance() {
    if (null == config_) {
      throw new IllegalStateException("Configuration hasn't been initialized yet.");
    }

    return config_;
  }

  public int getRobEntries() {
    return robEntries_;
  }

  public int getCdbEntries_() {
    return cdbEntries_;
  }

  public int getHardwareIntRegisters() {
    return hardwareIntRegisters_;
  }

  public int getHardwareFPRegisters() {
    return hardwareFPRegisters_;
  }

  public String getInstructionFile() {
    return instructions_;
  }

  // A convenience method to make testing multiple instruction files with the same configuration much simpler.
  public void setInstructionFile(String instructionFile) {
    instructions_ = instructionFile;
  }

  public MemoryConfig getMemoryConfig() {
    return memConfig_;
  }

  public RegistersConfig getRegistersConfig() {
    return regConfig_;
  }

  public ReservationStationConfig getReservationStationConfig() {
    return rsConfig_;
  }

  @SerializedName("rob_entries")
  private int robEntries_;

  @SerializedName("cdb_entries")
  private int cdbEntries_;

  @SerializedName("hardware_int_registers")
  private int hardwareIntRegisters_;

  @SerializedName("hardware_fp_registers")
  private int hardwareFPRegisters_;

  @SerializedName("instruction_file")
  private String instructions_;

  @SerializedName("memory")
  private MemoryConfig memConfig_;

  @SerializedName("registers")
  private RegistersConfig regConfig_;

  @SerializedName("reservation_stations")
  private ReservationStationConfig rsConfig_;

  private static ProcessorConfig config_ = null;
}
