package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transaction {
    //TODO copy of tenmo-server Transaction class (will not need @notblank @min, etc annotations)
    //Only difference is the toString and statusToString otherwise this model is identical to the server side transaction model

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

    //Overriding toString method to print out transaction format in nice format
    @Override
    public String toString() {
        String statusString = "";
        String typeString = "";

        switch(status){
            case 1: statusString = "Pending";
                    break;
            case 2: statusString = "Approved";
                    break;
            case 3: statusString = "Rejected";
                    break;
        }

        switch(type){
            case 1: typeString = "Request";
                break;
            case 2: typeString = "Send";
                break;

        }

        String output = "--------------------------------------------"+ System.lineSeparator() +
                "Transfer Details" + System.lineSeparator() +
                "--------------------------------------------" + System.lineSeparator() +
                "ID: " + transferID + System.lineSeparator() +
                "From: " + senderName  + System.lineSeparator() +
                "To: " + destinationName  + System.lineSeparator() +
                "Type: " + typeString +  System.lineSeparator() +
                "Status: " + statusString + System.lineSeparator() +
                "Amount: " + amount + System.lineSeparator() +
                "--------------------------------------------";


        return output;
    }

    //Converts status code to string description for use in viewTransferHistory
    public String statusToString(){
        String statusString = "";

        switch(status){
            case 1: statusString = "Pending";
                break;
            case 2: statusString = "Approved";
                break;
            case 3: statusString = "Rejected";
                break;
        }
        return statusString;
    }
}
