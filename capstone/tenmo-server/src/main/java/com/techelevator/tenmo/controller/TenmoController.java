package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransactionDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.FraudulentTransferException;
import com.techelevator.tenmo.exception.InvalidTransferAmountException;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    private UserDao userDao;
    private TransactionDAO transactionDao;


    //Rest controller is automatically injecting a jdbcUserDao
    public TenmoController(UserDao userDao, TransactionDAO transactionDAO) {
        this.userDao = userDao;
        this.transactionDao = transactionDAO;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        //How do we get user id from token?
        //We will be passing in a User object
        //@Valid says it has to be a valid token, then constructs a user object
        return userDao.getBalance(principal.getName());
    }


    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path="/send", method = RequestMethod.POST)
    public Transaction sendTransfer(@RequestBody @Valid Transaction transaction, Principal principal)
            throws InvalidTransferAmountException, FraudulentTransferException {

        //first verify that the source account in the transaction object matches the currently logged-in user
        //if the names don't match, throw a FraudulentTransferException and exit the method
        if(!transaction.getSenderName().equals(principal.getName())){
            throw new FraudulentTransferException();
        }

        //check to see if the transfer amount is more than the sender's account balance
        //if so, throw an exception and exit the method
        BigDecimal usersBalance = userDao.getBalance(transaction.getSenderName());
        BigDecimal transferAmount = transaction.getAmount();
        if(usersBalance.compareTo(transferAmount)==-1){
            throw new InvalidTransferAmountException();
        }

        //process send and receive sides of the transfer
        userDao.send(transaction.getSenderName(), transaction.getAmount());
        userDao.receive(transaction.getDestinationName(), transaction.getAmount());

        //set the transaction type to (2 - send)
        //set the transaction status to (2 - approved)
        transaction.setType(2);
        transaction.setStatus(2);

        //record the transaction in the DAO and return the transaction object
        //the returned transaction object will now have a transaction ID
        return transactionDao.create(transaction);
    }


    @RequestMapping(path = "/transactions", method = RequestMethod.GET)
    public Transaction[] listTransactions(Principal principal) {
        return transactionDao.listTransactions(userDao.findAccountIdByUsername(principal.getName()));
    }


    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction get(@PathVariable int id) {
        return transactionDao.getTransaction(id);
    }

    //returns a list of users (with passwords redacted)
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public User[] getAllUsers(){
        return userDao.findAll();

    }
    // This enters the requested transaction into the database and then returns the transaction now including an ID to the client
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public Transaction createRequest(@RequestBody @Valid Transaction transaction, Principal principal) {
        return transactionDao.create(transaction);
    }

    // This retrieves a list of transactions that are pending requests to the logged-in user and sends it to the client
    @RequestMapping(path = "/request", method = RequestMethod.GET)
    public Transaction[] getPendingRequests(Principal principal) {
        return transactionDao.listPendingTransactions(userDao.findAccountIdByUsername(principal.getName()));

    }

    // This updates a pending transfer status in the database
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/request", method = RequestMethod.PUT)
    public void updateRequest(@RequestBody @Valid Transaction transaction, Principal principal)
            throws InvalidTransferAmountException {

     if (transaction.getStatus()== 2) {
         BigDecimal usersBalance = userDao.getBalance(transaction.getSenderName());
         BigDecimal transferAmount = transaction.getAmount();
         if(usersBalance.compareTo(transferAmount)==-1){
             throw new InvalidTransferAmountException();
         }
         userDao.send(transaction.getSenderName(), transaction.getAmount());
         userDao.receive(transaction.getDestinationName(), transaction.getAmount());
     }
        transactionDao.updateTransferStatus(transaction);

    }





}
