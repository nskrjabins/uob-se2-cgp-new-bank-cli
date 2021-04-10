package newbank.server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import newbank.server.CustomerID;

public class NewBank {
  public static final String NOT_ACCOUNT_NAME_MSG = "PLEASE PROVIDE AN ACCOUNT NAME";
  public static final String SELECT_ACTION_MSG = "PLEASE SELECT AN ACTION";
  public static final String FAIL_MSG = "FAIL";
  public static final String ACCOUNT_CREATED_SUCCESSFULLY_MSG = "ACCOUNT CREATED SUCCESSFULLY";
  public static final String ACCOUNT_ALREADY_EXISTS_MSG = "THE ACCOUNT ALREADY EXISTS";
  public static final String ANYTHING_ELSE_MSG = "Anything else?";

  private static final NewBank bank = new NewBank();
  private HashMap<String,Customer> customers;
  private List<Loan> loans;

  public NewBank() {
    customers = new HashMap<>();
    loans = new ArrayList<>();
    addTestData();
  }

  private void addTestData() {
    try {
      Customer testUser = new Customer();
      testUser.addAccount("Main", 1000.0);
      customers.put("test", testUser);
    } catch (Customer.AccountAlreadyExists e) {
      System.out.println("Error creating user");
    }
  }

  public CustomerID addCustomer(String customerUsername, String customerPassword, PrintWriter out) {
    try {
      if (usernameExists(customerUsername)) {
        out.println("\nThe selected username already exists, please pick another one.");
        return null;
      } else {
        Customer newCustomer = new Customer();
        newCustomer.addAccount("Main", 0);
        customers.put(customerUsername, newCustomer);
        out.println("\nYour account has been created!");
        return new CustomerID(customerUsername); 
      }
    } catch (Customer.AccountAlreadyExists e) {
      out.println("\nError creating user");
      return null;
    }
  }

  private boolean usernameExists(String customerUsername) {
    return customers.containsKey(customerUsername);
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    if(customers.containsKey(userName)) {
      return new CustomerID(userName);
    }
    return null;
  }

  /**
   * Dispatcher Function. It will check the command, validate the parameters passed
   * and if valid pass the parameters to the logic for the requested command.
   *
   * @param customer {@code CustomerID} Id of the customer that is interacting.
   * @param request {@code String} Command sent to server.
   *
   * @return {@code String} Response to be sent to the client.
   * */
  public synchronized String processRequest(CustomerID customer, String request) {
    if(customers.containsKey(customer.getKey())) {
      String[] cmd = request.trim().split("\\s+");
      Customer customer_session = customers.get(customer.getKey());
      switch(cmd[0]) {
        case "SHOWMYACCOUNTS":
          return showMyAccounts(customer_session);
        case "NEWACCOUNT":
          if(cmd.length == 1) {
            return NOT_ACCOUNT_NAME_MSG;
          }
          return addAccount(customer_session, cmd[1]);
        case "HELP" :
          return NewBankClientHandler.printHelp();
        case "MOVE":
          return moveMoneyBetweenAccounts(customer_session, cmd);
        case "PAY":
          return "PAY PLACEHOLDER";
        case "REQUEST-LOAN":
          return requestLoan(customer_session, cmd);
        case "":
          return SELECT_ACTION_MSG; // On user enter pressed without typing a command
        default:
          return FAIL_MSG;
      }
    }
    return FAIL_MSG;
  }

  /**
   * It will create an account under the passed name for the customer.
   *
   * @param customer {@code Customer} Customer session that is interacting right now.
   * @param name {@code String} Account Name.
   *
   * @return {@code String} Response to be sent to the client.
   * */
  private String addAccount(Customer customer, String name) {
    try {
      customer.addAccount(name);
      return String.format("'%s' %s. %s", name, ACCOUNT_CREATED_SUCCESSFULLY_MSG, ANYTHING_ELSE_MSG);
    } catch (Customer.AccountAlreadyExists error) {
      System.out.printf("Error: %s", error.getMessage());
      return ACCOUNT_ALREADY_EXISTS_MSG;
    }
  }

  private String showMyAccounts(Customer customer) {
    return customer.accountsToString();
  }

  /**
   * Parse and excute an instruction to move money between accounts
   *
   * @param customer the logged in customer
   * @param userInput the command line arguments provided by the user
   * @return a string detailing if the transfer was successful or an error message
   */
  private String moveMoneyBetweenAccounts(Customer customer, String[] userInput){
    assert userInput[0].equals("MOVE");

    // Ensure the user has entered the correct number of arguments
    if (userInput.length != 4){
      return "FAIL: wrong number of arguments.\nPlease try again";
    }

    // Convert the amount to an integer
    int amount;
    try{
      amount = Integer.parseInt(userInput[1]);
    }
    catch (NumberFormatException ex){
      return "FAIL: invalid number for amount\nPlease try again";
    }

    // Check the transfer amount is not negative
    if (amount < 0) {
      return "FAIL: invalid transfer amount\nPlease try again";
    }

    // Try to move the customers money
    String errorMessage = customer.moveMoneyBetweenAccounts(amount, userInput[2], userInput[3]);

    // Inform the user of the transfer results
    if (errorMessage == null) {
      return "SUCCESS, Anything else?";
    } else {
      return String.format("FAIL: %s\nPlease try again", errorMessage);
    }
  }

  /**
   *  Allow users to request a loan
   *
   * @param customer the logged in customer
   * @param userInput the command line arguments provided by the user
   * @return a string detailing if the loan request has been accepted
   */
  private String requestLoan(Customer customer, String[] userInput){

    // Ensure the user has entered the correct number of arguments
    if (userInput.length != 2){
      return "FAIL: wrong number of arguments.\nPlease try again";
    }

    // Convert the amount to an integer
    int amount;
    try{
      amount = Integer.parseInt(userInput[1]);
    }
    catch (NumberFormatException ex){
      return "FAIL: invalid number for amount\nPlease try again";
    }

    // Check the loan amount is not negative
    if (amount < 0) {
      return "FAIL: invalid loan amount\nPlease try again";
    }

    Loan newLoanRequest = new Loan(amount, customer);
    loans.add(newLoanRequest);
    return "SUCCESS: Your loan has been requested.";
  }



}
