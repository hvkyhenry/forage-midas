package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity

public class TransactionRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY  )
    private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    private float amount;
    private float incentive;
    // Default constructor required by JPA
    public TransactionRecord() {
    }

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    // Getters and setters
    public long getId(){ return id;}
    public UserRecord getSender(){ return sender;}
    public UserRecord getRecipient(){ return recipient;}
    public float getAmount(){ return amount;}
    public float getIncentive(){ return incentive;}

    public void setSender(UserRecord sender){ this.sender = sender;}
    public void setRecipient(UserRecord recipient){ this.recipient = recipient;}
    public void setAmount(float amount){ this.amount = amount;}
    public void setIncentive(float incentive){ this.incentive = incentive;}
}
