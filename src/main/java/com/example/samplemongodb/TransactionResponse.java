package com.example.samplemongodb;

import lombok.Data;

import java.util.Date;

@Data
class TransactionResponse {
  private String id;
  private Long createdDate;
  private Long transactionDate;
  private Date transactionDateForm;
  private ActionName actionName;
  private long amount;
  private String merchantId;
  private String status;
  private String paymentType;

  public Date getTransactionDateForm() {
    return new Date(transactionDate);
  }

  public Date getCreatedDateFrom() {
    return new Date(createdDate);
  }
}
