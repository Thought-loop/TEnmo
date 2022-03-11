package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransactionDAO implements TransactionDAO{

    private JdbcTemplate jdbcTemplate;

    //constructor allows dependency injection
    public JdbcTransactionDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transaction[] listTransactions(int userID){
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

        Transaction[] transactions = new Transaction[myTransactions.size()];
        for(int i = 0; i < transactions.length; i++){
            transactions[i] = myTransactions.get(i);
        }
        return transactions;
    }

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

    @Override
    public Transaction create(Transaction transaction){
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?,?,?,?) RETURNING transfer_id;";
        Integer id = jdbcTemplate.queryForObject(sql,Integer.class, transaction.getType(), transaction.getStatus(),
                transaction.getSenderAccountID(), transaction.getDestinationAccountID(), transaction.getAmount());
        transaction.setTransferID(id);
        return transaction;
    }

    private Transaction mapRowToTransaction(SqlRowSet rowSet){
        //takes in a sql rowset from transfer table
        //converts columns into a transaction object and returns that object

        Transaction transaction = new Transaction();
        transaction.setAmount(rowSet.getBigDecimal("amount"));
        transaction.setDestinationAccountID(rowSet.getInt("account_to"));
        transaction.setSenderAccountID(rowSet.getInt("account_from"));
        transaction.setTransferID(rowSet.getInt("transfer_id"));
        transaction.setType(rowSet.getInt("transfer_type_id"));
        transaction.setStatus(rowSet.getInt("transfer_status_id"));

        String sql = "SELECT username from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id WHERE account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transaction.getDestinationAccountID());
        if(results.next()){
            transaction.setDestinationName(results.getString("username"));
        }

        sql = "SELECT username from tenmo_user JOIN account ON tenmo_user.user_id = account.user_id WHERE account_id = ?;";
        results = jdbcTemplate.queryForRowSet(sql, transaction.getSenderAccountID());
        if(results.next()){
            transaction.setSenderName(results.getString("username"));
        }

        return transaction;
    }
}
