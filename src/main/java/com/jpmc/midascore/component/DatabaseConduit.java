package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate ;

    @Autowired

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = new RestTemplate(); //instantiate the client to make the API call
    }

    @Transactional  // This ensures there is a fallback . The transcation is rolled back if there is an error
    public void processTransaction(Transaction transaction) {
        //validate if sender and recipient exist in the database

        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        float amount = transaction.getAmount();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);
        //Validation stage
        if (sender == null || recipient == null || sender.getBalance() < amount) {
            return; // Either sender or recipient does not exist, so we cannot process the transaction
        }

        // Calculate incentive by calling the external API
        Incentive incentiveResponse = restTemplate.postForObject("http://localhost:8080/incentive", transaction, Incentive.class);
        float incentiveAmount= incentiveResponse != null ? incentiveResponse.getAmount() : 0f;

        //Apply promotional incentive to the transaction amount
        sender.setBalance(sender.getBalance()-amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        //Save updates
        userRepository.save(sender);
        userRepository.save(recipient);

        //Create the record with the new incetive amount and save it to the database
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(transactionRecord);
  
    }

}
