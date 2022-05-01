package com.nonblocking.model;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptPayload implements Serializable {

  @NotBlank
  private long id;
  private String endpoint;
}
