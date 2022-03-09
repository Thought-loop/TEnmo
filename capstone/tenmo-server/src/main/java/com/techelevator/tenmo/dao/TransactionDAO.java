package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDAO {

    List<Transaction> listTransactions(int userID);

    Transaction getTransaction(int transferID);

    Transaction create(Transaction transaction);

}
