package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TenmoService {
    //Needs two HttpEntity methods
    //TODO (1)HttpEntity<Transaction> - Include both authtoken (credentials) and a transaction object to send to the server
    //TODO (2)HttpEntity<Void> -Includes only authtoken (credentials)
    //
    //TODO Needs setAuthToken() method - Will receive auth token from App class currentUser.getToken()
    //
    //TODO Need methods for each server endpoint /balance, /send, /transactions, transactions/[id]


    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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




        private HttpEntity<Transaction> makeTransactionEntity(Transaction transaction) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);
            return new HttpEntity<>(transaction, headers);
        }


        private HttpEntity<Void> makeAuthEntity() {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            return new HttpEntity<>(headers);
        }

    }













}
