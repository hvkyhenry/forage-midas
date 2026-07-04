package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Autowired

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional  // This ensures there is a fallback . The transcation is rolled back if there is an error
    public void processTransaction(long senderId, long recipientId, float amount) {
        //validate if sender and recipient exist in the database
        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null || recipient == null){
            return; // Either sender or recipient does not exist, so we cannot process the transaction
        }

        // if sender has enough money
        if(sender.getBalance() < amount){
            return; // Sender does not have enough money, so we cannot process the transaction
        }

        // Deduct the amount from the sender's balance
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        //save the updated user records
        userRepository.save(sender);
        userRepository.save(recipient);

        //Record teh transaction permanently in the database
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(transactionRecord);
    }

}
