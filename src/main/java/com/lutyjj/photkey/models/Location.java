package com.lutyjj.photkey.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents location.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

  @Getter
  @Setter
  @JsonProperty("display_name")
  private String city;
}
