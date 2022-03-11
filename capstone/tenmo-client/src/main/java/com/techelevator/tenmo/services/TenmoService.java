package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import io.cucumber.java.an.E;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class TenmoService {
    //Needs two HttpEntity methods
    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();
    //TODO Needs setAuthToken() method - Will receive auth token from App class currentUser.getToken()
    private String authToken = null;
    public void setAuthToken(String authToken){this.authToken = authToken;}

    //TODO (1)HttpEntity<Transaction> - Include both authtoken (credentials) and a transaction object to send to the server
    private HttpEntity<Transaction> makeTransactionEntity(Transaction transaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transaction, headers);
    }


    //TODO (2)HttpEntity<Void> -Includes only authtoken (credentials)
    //

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    //
    //TODO Need methods for each server endpoint /balance, /send, /transactions, transactions/[id]

    public Transaction[] listTransactions(){
        Transaction[] transactions = new Transaction[0];
        try {
            ResponseEntity <Transaction[]> response =
                    restTemplate.exchange(API_BASE_URL + "/transaction", HttpMethod.GET, makeAuthEntity(), Transaction[].class);
            transactions = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transactions;
    }

    public BigDecimal getBalance(){
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(API_BASE_URL  + "/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            balance = response.getBody();
        // we're going to send a GET request to the /balance endpoint with our auth token (makeAuthEntity). Expecting BigDecimal object back.
        //Wrapper wraps the BigDecimal then gets pulled out on line 61 with response.getBody()
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public User[] getAllUsers(){
        User[] users = null;

        try {
            ResponseEntity <User[]> response =
                    restTemplate.exchange(API_BASE_URL + "/users", HttpMethod.GET, makeAuthEntity(), User[].class);
            users = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return users;

    }

    public Transaction sendMoney(Transaction transaction) {
        Transaction responseTransaction = null;
//transaction that we pass into this method gets wrapped into and Http entity with the token and is sent to our server
        try {
            ResponseEntity<Transaction> response =
                    restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, makeTransactionEntity(transaction), Transaction.class);
            responseTransaction = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    return responseTransaction;
    }



    public Transaction addTransaction(Transaction newTransaction) {
        Transaction returnedTransaction = null;

        try {
            returnedTransaction = restTemplate.postForObject(API_BASE_URL + "transactions",
                    makeTransactionEntity(newTransaction), Transaction.class);
        }
        catch (RestClientException e) {
            BasicLogger.log(e.getMessage());
        }

        return returnedTransaction;
    }



}
