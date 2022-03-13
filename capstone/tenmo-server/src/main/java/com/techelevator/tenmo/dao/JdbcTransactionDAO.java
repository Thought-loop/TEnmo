package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransactionDAO implements TransactionDAO{

    private JdbcTemplate jdbcTemplate;

    //constructor allows dependency injection
    public JdbcTransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //query the transfer table for all transactions sent from, or sent to userID that our user approved or rejected
    //converts them into transaction objects and adds all transaction objects to a list
    //returns the list of transaction objects
    @Override
    public Transaction[] listTransactions(int userID){

        String sql = "SELECT transfer_id, transfer_type_id, transfer.transfer_status_id, account_from, account_to, amount " +
                "FROM transfer JOIN transfer_status ON transfer.transfer_status_id = transfer_status.transfer_status_id" +
                " WHERE account_from = ? OR account_to = ? AND (transfer_status_desc = 'Approved' OR transfer_status_desc = 'Rejected');";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userID, userID);
        List<Transaction> myTransactions = new ArrayList<>();
        while(results.next()){
            myTransactions.add(mapRowToTransaction(results));
        }

        Transaction[] transactions = new Transaction[myTransactions.size()];
        for(int i = 0; i < transactions.length; i++){
            transactions[i] = myTransactions.get(i);
        }
        return transactions;
    }

    //query transfer table for a specific transfer
    @Override
    public Transaction getTransaction(int transferID){
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferID);
        if(results.next()){
            return mapRowToTransaction(results);
        }
        return null;
    }

    //creates a new transfer and returns the transfer with its new ID
    @Override
    public Transaction create(Transaction transaction){
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?," +
                "(SELECT account_id FROM account WHERE user_id = ?)," +
                "(SELECT account_id FROM account WHERE user_id = ?),?) RETURNING transfer_id;";
        Integer id = jdbcTemplate.queryForObject(sql,Integer.class, transaction.getType(), transaction.getStatus(),
                transaction.getSenderUserID(), transaction.getDestinationUserID(), transaction.getAmount());
        transaction.setTransferID(id);
        return transaction;
    }

    //takes in a sql rowset from transfer table
    //converts columns into a transaction object and returns that object
    private Transaction mapRowToTransaction(SqlRowSet rowSet){

        Transaction transaction = new Transaction();
        transaction.setAmount(rowSet.getBigDecimal("amount"));
        transaction.setDestinationUserID(rowSet.getInt("account_to"));
        transaction.setSenderUserID(rowSet.getInt("account_from"));
        transaction.setTransferID(rowSet.getInt("transfer_id"));
        transaction.setType(rowSet.getInt("transfer_type_id"));
        transaction.setStatus(rowSet.getInt("transfer_status_id"));

        String sql = "SELECT username from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transaction.getDestinationUserID());
        if(results.next()){
            transaction.setDestinationName(results.getString("username"));
        }

        sql = "SELECT username from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id WHERE account_id = ?;";
        results = jdbcTemplate.queryForRowSet(sql, transaction.getSenderUserID());
        if(results.next()){
            transaction.setSenderName(results.getString("username"));
        }

        return transaction;
    }

    //query the database for pending requests from the given user id
    public Transaction[] listPendingTransactions( int userID){

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE account_from = ? AND transfer_type_id = 1 AND transfer_status_id = 1;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userID);
        List<Transaction> myTransactions = new ArrayList<>();
        while(results.next()){
            myTransactions.add(mapRowToTransaction(results));
        }

        Transaction[] transactions = new Transaction[myTransactions.size()];
        for(int i = 0; i < transactions.length; i++){
            transactions[i] = myTransactions.get(i);
        }
        return transactions;

    }


    //update a transaction in the database
    public void updateTransferStatus(Transaction transaction) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
        int rowsUpdated = jdbcTemplate.update(sql, transaction.getStatus(), transaction.getTransferID());

    }





}
