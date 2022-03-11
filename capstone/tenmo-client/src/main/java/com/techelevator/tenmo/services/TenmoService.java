package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

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
    private HttpEntity<Void> makeVoidTransactionEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>( headers);
    }


    //
    //TODO Need methods for each server endpoint /balance, /send, /transactions, transactions/[id]

    public Transaction[] listTransactions(int UserId){
        Transaction[] transactions = null;
        try {
            ResponseEntity<Transaction[]> response =
                    restTemplate.exchange(API_BASE_URL + "/transaction", HttpMethod.GET, makeAuthEntity(), Transaction[].class);
            transactions = response.getBody();
        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transactions;
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}
