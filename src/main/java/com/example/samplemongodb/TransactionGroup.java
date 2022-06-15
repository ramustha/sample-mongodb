package com.example.samplemongodb;

import lombok.Data;

import java.util.List;

@Data
class TransactionGroup {
  private String id;
  private List<TransactionResponse> data;
}
