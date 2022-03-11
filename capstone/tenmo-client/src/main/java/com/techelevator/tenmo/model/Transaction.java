package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transaction {
    //TODO copy of tenmo-server Transaction class (will not need @notblank @min, etc annotations)

    private int transferID;
    private BigDecimal amount;
    private String senderName;
    private int senderUserID;
    private String destinationName;
    private int destinationUserID;
    private int status;
    private int type;

    public Transaction(){}

    public Transaction(BigDecimal amount, String senderName, String destinationName, int type) {
        this.amount = amount;
        this.senderName = senderName;
        this.destinationName = destinationName;
        this.type = type;
    }

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getSenderUserID() {
        return senderUserID;
    }

    public void setSenderUserID(int senderUserID) {
        this.senderUserID = senderUserID;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getDestinationUserID() {
        return destinationUserID;
    }

    public void setDestinationUserID(int destinationUserID) {
        this.destinationUserID = destinationUserID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transferID=" + transferID +
                ", amount=" + amount +
                ", senderName='" + senderName + '\'' +
                ", senderID=" + senderUserID +
                ", destinationName='" + destinationName + '\'' +
                ", destinationID=" + destinationUserID +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
