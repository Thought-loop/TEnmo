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

    //Constructor for TenmoService, which sets base URL
    public TenmoService(String API_BASE_URL) {
        this.API_BASE_URL = API_BASE_URL;
    }

    //Any time we make a request to the server we include an HttpEntity that always includes our JWT
    //We use makeTransactionEntity when we also want to send a transaction object to the server
    //We use makeAuthEntity when don't need to send a transaction object to the server

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


//Requests a list of transactions from the server
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
//Request a single transaction from the server
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


//Requests the balance from the server
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


//Requests a list of all users from the server
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


//Sends a new sendMoney transaction to the server and returns a response. For a successful transaction it returns details about the transaction
    //otherwise it returns and error message
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

//Requests a list of transactions from the server and involving the current user
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

    //Updates an existing transfer request on the server
    //The http method is implied with restTemplate.put
    //This method returns a boolean, so success returns true
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

    //Sends a new transaction transfer request to the server
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
