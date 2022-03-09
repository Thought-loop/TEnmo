package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public class JdbcTransactionDAO implements TransactionDAO{

    private JdbcTemplate jdbcTemplate;

    public List<Transaction> listTransactions(int userID){
        //query the transfer table for all transactions sent from, or sent to userID
        //converts them into transaction objects and adds all transaction objects to a list
        //returns the list of transaction objects

        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE account_from = ? OR account_to = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userID, userID);
        List<Transaction> myTransactions = new ArrayList<>();
        while(results.next()){
            myTransactions.add(mapRowToTransaction(results));
        }
        return myTransactions;
    }

    public Transaction getTransaction(int transferID){
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferID);
        if(results.next()){
            return mapRowToTransaction(results);
        }
        return null;
    }

    public Transaction create(Transaction transaction){
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?,?,?,?)";
        int id = jdbcTemplate.queryForObject(sql,Integer.class, transaction.getType(), transaction.getStatus(),
                transaction.getSenderID(), transaction.getDestinationID(),transaction.getAmount());
        transaction.setTransferID(id);
        return transaction;
    }

    private Transaction mapRowToTransaction(SqlRowSet rowSet){
        //takes in a sql rowset from transfer table
        //converts columns into a transaction object and returns that object

        Transaction transaction = new Transaction();
        transaction.setAmount(rowSet.getDouble("amount"));
        transaction.setDestinationID(rowSet.getInt("account_to"));
        transaction.setSenderID(rowSet.getInt("account_from"));
        transaction.setTransferID(rowSet.getInt("transfer_id"));
        transaction.setType(rowSet.getInt("transfer_type_id"));
        transaction.setStatus(rowSet.getInt("transfer_status_id"));
        return transaction;
    }
}
