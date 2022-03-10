package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.InvalidTransferAmountException;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    private UserDao userDao;

    //Rest controller is automatically injecting a jdbcUserDao
    public TenmoController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        //How do we get user id from token?
        //We will be passing in a User object
        //@Valid says it has to be a valid token, then constructs a user object
        return userDao.getBalance(principal.getName());
    }

    //As an authenticated user of the system,
    //I need to be able to *send* a transfer of a specific amount of TE Bucks to a registered user.
    //localhost:8080/send
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path="/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody @Valid Transaction transaction, Principal principal) throws InvalidTransferAmountException {

        BigDecimal usersBalance = userDao.getBalance(transaction.getSenderName());
        BigDecimal transferAmount = transaction.getAmount();
        if(usersBalance.compareTo(transferAmount)==-1){
            throw new InvalidTransferAmountException();
        }
        userDao.send(transaction.getSenderName(), transaction.getAmount());
        userDao.receive(transaction.getDestinationName(), transaction.getAmount());
    }


}