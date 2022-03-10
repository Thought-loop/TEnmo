package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class Transaction {
    //where/who going to
    //where/who coming from
    //amount of transfer

    private int transferID;
    @DecimalMin(value = "0.0", inclusive = false, message = "Transfer amount must be greater than 0")
    private BigDecimal amount;
    @NotBlank(message = "The field 'senderName' is required.")
    private String senderName;
    private int senderID;
    @NotBlank(message = "The field 'destinationName' is required.")
    private String destinationName;
    private int destinationID;
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

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(int destinationID) {
        this.destinationID = destinationID;
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
}
