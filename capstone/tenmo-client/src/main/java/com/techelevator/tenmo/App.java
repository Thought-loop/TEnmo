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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class App {

    private static final String API_BASE_URL = "https://www.thought-loop.com/tenmo/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    //We create a new TenmoService passing in the API_BASE_URL
    private final TenmoService tenmoService = new TenmoService(API_BASE_URL);

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

    //Added setting tenmoService.setAuthToken for a successful login
    //Otherwise the token would not be saved anywhere
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

    // Prints balance by calling tenmoService.getBalance()
	private void viewCurrentBalance() {
        BigDecimal balance = tenmoService.getBalance();
        System.out.println("Your current account balance is: $" + balance);
	}

    //Prints out current users transfer history not including pending requests
    //If the user has no history then it prints "You have no ..."
	private void viewTransferHistory() {
		// TODO add ability to get info about a specific transaction from the list of transactions
        Transaction[] transfers = tenmoService.listTransactions();
        List<Integer> transferIDs = new ArrayList<>();
		if(transfers.length == 0){
            System.out.println("*********You have no transfers in your history********");
        }
        else{
            User user = currentUser.getUser();
            System.out.println("--------------------------------------------");
            System.out.println("ID-------FROM/TO--------STATUS---------AMOUNT");
            for(int i = 0; i < transfers.length; i++){
                //add the transfer ID to a list of transfer IDS
                transferIDs.add(transfers[i].getTransferID());
                //if user was sender, show as TO
                if(user.getUsername().equals(transfers[i].getSenderName())){
                    System.out.println(transfers[i].getTransferID()+"-----TO:" + transfers[i].getDestinationName() + "-----"  + transfers[i].statusToString()  + "---------$" + transfers[i].getAmount());
                }
                //if user received, show as FROM
                else if(user.getUsername().equals(transfers[i].getDestinationName())){
                    System.out.println(transfers[i].getTransferID()+"-----FROM:" + transfers[i].getSenderName() + "---" + transfers[i].statusToString() + "---------$" + transfers[i].getAmount());
                }
                //if user doesn't match any part of transaction, we have a problem :(
                else{
                    System.out.println("----------------INVALID TRANSFER IN DATABASE----------------");
                }
            }
            System.out.println("--------------------------------------------");
        }

        int userInput = consoleService.promptForInt("Enter a transfer ID to see more details. Enter (0) to return to the menu: ");
        while(userInput != 0 && (!transferIDs.contains(userInput))){
            userInput = consoleService.promptForInt("Not a valid ID. Enter a transfer ID or enter (0) to return to the menu: ");
        }
        if(userInput != 0){
            Transaction singleTransaction = tenmoService.getTransaction(userInput);
            System.out.println();
            System.out.println(singleTransaction);
        }
	}

    //Prints a list of pending requests involving the current user
    //Also allows the user to approve or reject a request
	private void viewPendingRequests() {
        Transaction[] transfers = tenmoService.listPendingTransactions();
        List<Integer> transferIDs = new ArrayList<>();
        if(transfers.length == 0){
            System.out.println("*********You have no transfers in your history********");
        }
        else{
            User user = currentUser.getUser();
            System.out.println("--------------------------------------------");
            System.out.println("ID------FROM/TO---------------------AMOUNT");
            for(int i = 0; i < transfers.length; i++){
                //add the transfer ID to a list of transfer IDS
                transferIDs.add(transfers[i].getTransferID());
                //if user was sender, show as TO
                if(user.getUsername().equals(transfers[i].getSenderName())){
                    System.out.println(transfers[i].getTransferID()+"-----TO:" + transfers[i].getDestinationName() + "-------------------$" + transfers[i].getAmount());
                }
                //if user received, show as FROM
                else if(user.getUsername().equals(transfers[i].getDestinationName())){
                    System.out.println(transfers[i].getTransferID()+"-----FROM:" + transfers[i].getSenderName() + "-----------------$" + transfers[i].getAmount());
                }
                //if user doesn't match any part of transaction, we have a problem :(
                else{
                    System.out.println("----------------INVALID TRANSFER IN DATABASE----------------");
                }
            }
            System.out.println("--------------------------------------------");
        }

        int userInput = consoleService.promptForInt("Enter a transfer ID to approve or reject. Enter (0) to return to the menu: ");
        while(userInput != 0 && (!transferIDs.contains(userInput))){
            userInput = consoleService.promptForInt("Not a valid ID. Enter a transfer ID or enter (0) to return to the menu: ");
        }
        if(userInput != 0){
            Transaction singleTransaction = tenmoService.getTransaction(userInput);
            System.out.println();
            System.out.println(singleTransaction);
            System.out.println("--------------------------------------------"+ System.lineSeparator() +
                    "1: Approve" + System.lineSeparator() +
                    "2: Reject " + System.lineSeparator() +
                    "0: Don't approve or reject" + System.lineSeparator() +
                    "--------------------------------------------");
            userInput = consoleService.promptForInt("Please enter an option.");
            while(userInput != 0 && userInput != 1 && userInput != 2) {
                userInput = consoleService.promptForInt("Not a valid choice. Please enter an option.");
            }
            if(userInput == 1) {
                singleTransaction.setStatus(2);
                boolean success = tenmoService.updateRequestStatus(singleTransaction);
                if (!success) {
                    System.out.println("Unable to approve this request. Insufficient balance.");
                }

            }
            else if (userInput == 2) {
                singleTransaction.setStatus(3);
                boolean success = tenmoService.updateRequestStatus(singleTransaction);
                if (!success) {
                    System.out.println("There was an error. Please try again.");
                }

            }
        }
    }


		


    //Sends a new sendBucks transaction to the server and prints out the server's transaction response
    //In order to allow user to cancel the transaction, we add one to the index that the user is choosing (line 239)
    //so we must subtract one to match up the selection
	private void sendBucks() {
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

    //Sends a new requestBucks transaction to the server and prints out the server's transaction response
	private void requestBucks() {

        User[] allUsers = tenmoService.getAllUsers();
        int currentUserIndex = -1;
        for(int i = 0; i < allUsers.length; i++){
            if (allUsers[i].getUsername().equals(currentUser.getUser().getUsername())){
                currentUserIndex = i;
            } else {
                System.out.println((i+1) + ") " +  allUsers[i].getId() + "----" + allUsers[i].getUsername());
            }
        }
        int userSelection = consoleService.promptForMenuSelection("Please choose a user to request TE bucks from (0 to exit): ")-1;
        while ((userSelection < -1) || (userSelection >= allUsers.length ) ||(userSelection == currentUserIndex)){
            System.out.println("Hit the road Jack!");
            userSelection = consoleService.promptForMenuSelection("Please choose a user to request TE bucks from (0 to exit): ")-1;
        }
        if(userSelection != -1) {
            Transaction transaction = new Transaction();
            BigDecimal amount = consoleService.promptForBigDecimal("Please enter amount to request: ");
            transaction.setAmount(amount);
            transaction.setDestinationName(currentUser.getUser().getUsername());
            //user chosen is retrieved using username from array of users
            transaction.setSenderName(allUsers[userSelection].getUsername());
            transaction.setDestinationUserID(currentUser.getUser().getId().intValue());
            transaction.setSenderUserID(allUsers[userSelection].getId().intValue());
            transaction.setStatus(1);
            transaction.setType(1);
            System.out.println(tenmoService.requestMoney(transaction));
        }
    }


		
	}


