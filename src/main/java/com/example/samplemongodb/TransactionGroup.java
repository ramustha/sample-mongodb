package com.example.samplemongodb;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
class TransactionGroup {
  private Long id;
  private List<TransactionResponse> data;

  public Date getOriginGroup() {
    return new Date(id);
  }
}
