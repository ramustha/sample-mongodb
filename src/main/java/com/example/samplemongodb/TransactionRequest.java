package com.example.samplemongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TransactionRequest {
  private int size = 10;
  private int groupSize = 10;
  private int page = 0;
  private long createdDateFrom;
  private long createdDateTo;
  private String sortOrder = "ASC";
  private String sortBy = "createdDate";
  private String groupOrder = "ASC";
  private String groupBy = "createdDate";
  private String groupType = "day";
  private List<String> actionName;
  private List<String> status;
  private List<String> paymentType;
}
