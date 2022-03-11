package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transaction;

import com.techelevator.tenmo.model.User;

import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;


import javax.xml.crypto.dsig.TransformService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TenmoService tenmoService = new TenmoService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        else{
            tenmoService.setAuthToken(currentUser.getToken());
        }
        //TODO insert our auth token into TenmoService here

    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO update placeholder to actual tenmoService method for calling /balance endpoint
        BigDecimal balance = tenmoService.getBalance();
        System.out.println("Your current account balance is: $" + balance);
	}

	private void viewTransferHistory() {
		// TODO update placeholder to actual tenmoService method for calling /transactions endpoint
        Transaction[] transfers = tenmoService.listTransactions();
		if(transfers.length == 0){
            System.out.println("*********You have no transfers in your history********");
        }
        else{
            User user = currentUser.getUser();
            System.out.println("ID-----FROM/TO-----AMOUNT");
            for(int i = 0; i < transfers.length; i++){
                //if user was sender, show as TO
                if(user.getUsername().equals(transfers[i].getSenderName())){
                    System.out.println(transfers[i].getTransferID()+"-----TO:" + transfers[i].getDestinationName() + "-----$" + transfers[i].getAmount());
                }
                //if user received, show as FROM
                else if(user.getUsername().equals(transfers[i].getDestinationName())){
                    System.out.println(transfers[i].getTransferID()+"-----FROM:" + transfers[i].getSenderName() + "-----$" + transfers[i].getAmount());
                }
                //if user doesn't match any part of transaction, we have a problem :(
                else{
                    System.out.println("----------------INVALID TRANSFER IN DATABASE----------------");
                }
            }
            System.out.println("---------------------------------------------");
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        //help user create a transaction object
       User[] allUsers = tenmoService.getAllUsers();
       int currentUserIndex = -1;
       for(int i = 0; i < allUsers.length; i++){
            if (allUsers[i].getUsername().equals(currentUser.getUser().getUsername())){
                currentUserIndex = i;
            } else {
                System.out.println((i+1) + ") " +  allUsers[i].getId() + "----" + allUsers[i].getUsername());
            }
        }
        int userSelection = consoleService.promptForMenuSelection("Please choose a user to send TE bucks to (0 to exit): ")-1;
        while ((userSelection < -1) || (userSelection >= allUsers.length ) ||(userSelection == currentUserIndex)){
            System.out.println("Hit the road Jack!");
            userSelection = consoleService.promptForMenuSelection("Please choose a user to send TE bucks to (0 to exit): ")-1;
        }
        if(userSelection != -1) {
            Transaction transaction = new Transaction();
            BigDecimal amount = consoleService.promptForBigDecimal("Please enter amount to send: ");
            transaction.setAmount(amount);
            transaction.setSenderName(currentUser.getUser().getUsername());
            //user chosen is retrieved using username from array of users
            transaction.setDestinationName(allUsers[userSelection].getUsername());
            transaction.setSenderUserID(currentUser.getUser().getId().intValue());
            transaction.setDestinationUserID(allUsers[userSelection].getId().intValue());
            System.out.println(tenmoService.sendMoney(transaction));
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
