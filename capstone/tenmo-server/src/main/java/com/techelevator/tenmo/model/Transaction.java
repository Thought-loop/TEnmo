package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class Transaction {

    private int transferID;

    @DecimalMin(value = "0.0", inclusive = false, message = "Transfer amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "The field 'senderName' is required.")
    private String senderName;

    @Min(value = 1000, message = "Must have a valid sender ID")
    private int senderAccountID;

    @NotBlank(message = "The field 'destinationName' is required.")
    private String destinationName;

    @Min(value = 1000, message = "Must have a valid destination ID")
    private int destinationAccountID;

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

    public int getSenderAccountID() {
        return senderAccountID;
    }

    public void setSenderAccountID(int senderAccountID) {
        this.senderAccountID = senderAccountID;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public int getDestinationAccountID() {
        return destinationAccountID;
    }

    public void setDestinationAccountID(int destinationAccountID) {
        this.destinationAccountID = destinationAccountID;
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
                ", senderID=" + senderAccountID +
                ", destinationName='" + destinationName + '\'' +
                ", destinationID=" + destinationAccountID +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
