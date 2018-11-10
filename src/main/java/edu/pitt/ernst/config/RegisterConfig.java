package edu.pitt.ernst.config;

import com.google.gson.annotations.SerializedName;

public class RegisterConfig {
  public String getName() {
    return name_;
  }

  public String getValue() {
    return value_;
  }

  @SerializedName("name")
  private String name_;

  @SerializedName("value")
  private String value_;
}
