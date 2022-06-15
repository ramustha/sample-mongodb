package com.example.samplemongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transactions")
class Transaction {

  @Id
  private String id;

  @CreatedDate
  private Long createdDate;

  @CreatedBy
  private String createdBy;

  @LastModifiedDate
  private Long lastModifiedDate;

  @LastModifiedBy
  private String lastModifiedBy;

  @Version
  private Long version;

  private String memberId;

  private Long transactionDate;

  private String referenceId;

  private String paymentType;

  private String paymentTypeDetailId;

  private ActionName actionName;

  private long amount;

  private String description;

  private TransactionStatus status;

  private String source;

  private String referenceTypeId;

  private String referenceType;

  private String merchantId;

  private String merchantGroup;

  private String maskedCard;

  private String cardNetwork;

  private boolean refunded;
}
