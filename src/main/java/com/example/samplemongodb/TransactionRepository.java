package com.example.samplemongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TransactionRepository extends MongoRepository<Transaction, Long> {

}
