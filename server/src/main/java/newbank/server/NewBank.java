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
        case "PAY-LOAN":
          return payLoan(customer_session, cmd);
        case "GIVE-LOAN":
          return giveLoan(customer_session, cmd);
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

  /**
   *  Allow users to give a loan
   *
   * @param customer the logged in customer
   * @param userInput the command line arguments provided by the user
   * @return a string detailing if the loan has been given
   */
  private String giveLoan(Customer customer, String[] userInput){

    // Ensure the user has entered the correct number of arguments
    if (userInput.length != 4){
      return "FAIL: wrong number of arguments.\nPlease try again";
    }

    Account userAccount;
    int amount;
    String receiversUsername = userInput[2];

    // Get the user account
    userAccount = customer.getAccount(userInput[1]);
    if (userAccount == null) {
      return "FAIL: invalid account name\nPlease try again";
    }

    // Convert the amount to an integer
    try{
      amount = Integer.parseInt(userInput[3]);
    }
    catch (NumberFormatException ex){
      return "FAIL: invalid number for amount\nPlease try again";
    }

    // Check the loan amount is not negative
    if (amount < 0) {
      return "FAIL: invalid loan amount\nPlease try again";
    }

    Customer receiver = customers.get(receiversUsername);

    if (receiver == null) {
      return "FAIL: invalid receiver\nPlease try again";
    }

    for (Loan loan:loans
         ) {
      // Skip over any loan that is not the loan we are looking for
      if (loan.getCustomerRequestingLoan() != receiver){
        continue;
      }
      if(loan.getAmount() != amount){
        continue;
      }

      // A loan can only be provided by one user
      if(loan.getLoanProvider() != null){
        continue;
      }

      //Actually provide the loan
      Account accountToTransferTo = loan.getCustomerRequestingLoan().receivingAccount();
      boolean succeed = userAccount.transfer(amount, accountToTransferTo);
      if (succeed){
        loan.setLoanProvider(customer);
        return "SUCCESS: The loan has been provided.";
      }
      else {
        return "FAIL: You do not have enough funds.";
      }
    }

    return "FAIL: no loan request matched the provided details.\nPlease try again";

  }


  /**
   *  Allow users to pay their loan
   *
   * @param customer the logged in customer
   * @param userInput the command line arguments provided by the user
   * @return a string detailing if the loan has been paid
   */
  private String payLoan(Customer customer, String[] userInput){
    // Ensure the user has entered the correct number of arguments
    if (userInput.length != 3){
      return "FAIL: wrong number of arguments.\nPlease try again";
    }

    Account userAccount;
    int amount;

    // Get the users account
    userAccount = customer.getAccount(userInput[1]);
    if (userAccount == null) {
      return "FAIL: invalid account name\nPlease try again";
    }

    // Convert the amount to an integer
    try{
      amount = Integer.parseInt(userInput[2]);
    }
    catch (NumberFormatException ex){
      return "FAIL: invalid number for amount\nPlease try again";
    }

    // Check the pay amount is not negative
    if (amount < 0) {
      return "FAIL: invalid loan amount\nPlease try again";
    }

    for (Loan loan:loans
    ) {

      // Check for the loan belonging to the user
      if (loan.getCustomerRequestingLoan() != customer){
        continue;
      }
      if(loan.getAmount() != amount){
        continue;
      }

      // Only a paid loan can be paid back
      if(loan.getLoanProvider() == null){
        continue;
      }

      // Pay the loan
      Account accountToTransferTo = loan.getLoanProvider().receivingAccount();
      boolean succeed = userAccount.transfer(amount, accountToTransferTo);
      if (succeed){
        // Remove the paid back loan from the list of loans
        loans.remove(loan);
        return "SUCCESS: The loan has been paid back.";
      }
      else {
        return "FAIL: You do not have enough funds to pay back the loan.";
      }
    }

    return "FAIL: no loan request matched the provided details.\nPlease try again";
  }

}
