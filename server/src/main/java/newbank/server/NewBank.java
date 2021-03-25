package newbank.server;

import java.util.HashMap;

public class NewBank {
  public static final String NOT_ACCOUNT_NAME_MSG = "PLEASE PROVIDE AN ACCOUNT NAME";
  public static final String SELECT_ACTION_MSG = "PLEASE SELECT AN ACTION";
  public static final String FAIL_MSG = "FAIL";
  public static final String ACCOUNT_CREATED_SUCCESSFULLY_MSG = "ACCOUNT CREATED SUCCESSFULLY";
  public static final String ACCOUNT_ALREADY_EXISTS_MSG = "THE ACCOUNT ALREADY EXISTS";
  public static final String ANYTHING_ELSE_MSG = "Anything else?";

  private static final NewBank bank = new NewBank();
  private HashMap<String,Customer> customers;

  public NewBank() {
    customers = new HashMap<>();
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
          return "MOVE PLACEHOLDER";
        case "PAY":
          return "PAY PLACEHOLDER";
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

}
