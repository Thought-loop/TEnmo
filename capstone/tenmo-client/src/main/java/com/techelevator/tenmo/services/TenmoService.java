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

    private final String API_BASE_URL;
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;
    public void setAuthToken(String authToken){this.authToken = authToken;}

    public TenmoService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }

    //HttpEntity<Transaction> - Include both authtoken (credentials) and a transaction object to send to the server
    private HttpEntity<Transaction> makeTransactionEntity(Transaction transaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transaction, headers);
    }

    //HttpEntity<Void> -Includes only authtoken (credentials)
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }



    public Transaction[] listTransactions(){
        Transaction[] transactions = new Transaction[0];
        try {
            ResponseEntity <Transaction[]> response =
                    restTemplate.exchange(API_BASE_URL + "/transactions", HttpMethod.GET, makeAuthEntity(), Transaction[].class);
            transactions = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transactions;
    }

    public Transaction getTransaction(long transferID){
        Transaction transaction = null;
        try {
            ResponseEntity <Transaction> response =
                    restTemplate.exchange(API_BASE_URL + "/transactions/"+transferID, HttpMethod.GET, makeAuthEntity(), Transaction.class);
            transaction = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transaction;
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



    public String sendMoney(Transaction transaction) {
        Transaction responseTransaction = null;

    //the transaction object that we pass into this method gets wrapped in an HttpEntity with the token and is sent to our server
        try {
            ResponseEntity<Transaction> response =
                    restTemplate.exchange(API_BASE_URL + "/send", HttpMethod.POST, makeTransactionEntity(transaction), Transaction.class);
            responseTransaction = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            return "Invalid transfer amount (insufficient balance or invalid transfer amount)";
        }
    return responseTransaction.toString();
    }


    public Transaction[] listPendingTransactions(){
        Transaction[] transactions = new Transaction[0];
        try {
            ResponseEntity <Transaction[]> response =
                    restTemplate.exchange(API_BASE_URL + "/request", HttpMethod.GET, makeAuthEntity(), Transaction[].class);
            transactions = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transactions;
    }

    public boolean updateRequestStatus( Transaction transaction) {
        boolean success = false;

        try {
                    restTemplate.put(API_BASE_URL + "/request", makeTransactionEntity(transaction) );
                    success = true;
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }

    public String requestMoney(Transaction transaction) {
        Transaction responseTransaction = null;

        //the transaction object that we pass into this method gets wrapped in an HttpEntity with the token and is sent to our server
        try {
            ResponseEntity<Transaction> response =
                    restTemplate.exchange(API_BASE_URL + "/request", HttpMethod.POST, makeTransactionEntity(transaction), Transaction.class);
            responseTransaction = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            return "There was a problem making request.";
        }
        return responseTransaction.toString();
    }




}
