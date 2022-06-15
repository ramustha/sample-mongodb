package com.example.samplemongodb;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@RestController
class TransactionResources {
  @Autowired
  private MongoTemplate mongoTemplate;

  @PostMapping(value = "/transactions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<TransactionGroup> getTransactions(@RequestBody TransactionRequest request) {
    Criteria criteria = null;
    //        Criteria.where("memberId")
    //            .is(memberId)
    //            .and("merchantGroup")
    //            .is(merchantGroup);

    if (!CollectionUtils.isEmpty(request.getActionName()) && !request.getActionName()
        .contains("ALL")) {
      criteria = Criteria.where("actionName").in(request.getActionName());
    }

    if (!CollectionUtils.isEmpty(request.getStatus()) && !request.getActionName().contains("ALL")) {
      criteria = criteria.and("status").in(request.getStatus());
    }

    if (!CollectionUtils.isEmpty(request.getPaymentType()) && !request.getPaymentType()
        .contains("ALL")) {
      criteria = criteria.and("paymentType").in(request.getPaymentType());
    }

    List<Criteria> createdDateCriteriaList = new ArrayList<>(2);
    if (request.getCreatedDateFrom() > 0) {
      createdDateCriteriaList.add(Criteria.where("createdDate").gte(request.getCreatedDateFrom()));
    }

    if (request.getCreatedDateTo() > 0) {
      createdDateCriteriaList.add(Criteria.where("createdDate").lte(request.getCreatedDateTo()));
    }

    if (!CollectionUtils.isEmpty(createdDateCriteriaList)) {
      criteria = criteria.andOperator(createdDateCriteriaList.toArray(new Criteria[0]));
    }

    MatchOperation matchOps = match(criteria);

    SortOperation sortOps =
        sort(Sort.by(Sort.Direction.valueOf(request.getSortOrder()), request.getSortBy()));

    AggregationOperation addFields = context -> new Document("$addFields",
        new Document("groupDate", groupBy(request.getGroupBy(), request.getGroupType())));

    GroupOperation groupOps = group("groupDate").push(Aggregation.ROOT).as("data");

    Sort groupSort = Sort.by(Sort.Direction.valueOf(request.getGroupOrder()), "_id");
    SortOperation sortGroupOps = sort(groupSort);
    long pageToSkip = request.getPage() > 0 ? ((long) request.getPage() * request.getSize()) : 0;
    SkipOperation skipOps = skip(pageToSkip);

    LimitOperation limitOps = limit(request.getSize());

    Aggregation aggregation;
    if (request.getGroupSize() != -1) {
      ProjectionOperation projectOps = project("$data").andExpression("data")
          .slice(request.getGroupSize()).as("data")
          .and(context -> new Document("$toLong",
              new Document("$dateFromString",
                  new Document("format", "%Y-%m-%d").append("dateString",
                      dateGroupConcat(request.getGroupType()))))).as("_id");

      aggregation = newAggregation(matchOps,
          sortOps,
          addFields,
          groupOps,
          sortGroupOps,
          skipOps,
          limitOps,
          projectOps);
    } else {
      aggregation =
          newAggregation(matchOps, sortOps, addFields, groupOps, sortGroupOps, skipOps, limitOps);
    }

    System.out.println("aggregation.toString() = " + aggregation);
    AggregationResults<TransactionGroup> output =
        mongoTemplate.aggregate(aggregation, Transaction.class, TransactionGroup.class);

    return output.getMappedResults();
  }

  static Object groupBy(String groupField, String groupFormat) {
    if (groupField.equals("createdDate") || groupField.equals("transactionDate")) {
      return new Document("$dateToString",
          new Document("format", dateGroupFormat(groupFormat)).append("date",
              new Document("$toDate", String.format("$%s", groupField))));
    }
    return groupBy("transactionDate", groupFormat);
  }

  static String dateGroupFormat(String value) {
    if (value.equals("year")) {
      return "%Y";
    } else if (value.equals("month")) {
      return "%Y-%m";
    }
    return "%Y-%m-%d";
  }

  static Object dateGroupConcat(String value) {
    if (value.equals("year")) {
      return new Document("$concat", List.of("$_id", "-01-01"));
    } else if (value.equals("month")) {
      return new Document("$concat", List.of("$_id", "-01"));
    }
    return "$_id";
  }
}
