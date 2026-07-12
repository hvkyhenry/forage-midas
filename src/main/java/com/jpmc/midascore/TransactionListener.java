package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.component.DatabaseConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TransactionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionListener.class);
    private final DatabaseConduit databaseConduit;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void receive(String transactionJson) {
    try {
            // Manually parse the raw string into a Transaction object safely
            Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);
            
            LOG.info("Processing transaction: {}", transaction);
            databaseConduit.processTransaction(
              transaction
            );
        } catch (Exception e) {
            LOG.error("Failed to parse or process transaction raw JSON string", e);
        }
    }
}