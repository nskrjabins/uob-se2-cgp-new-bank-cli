package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import newbank.server.CustomerID;

public class NewBankClientHandler extends Thread{

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;

  public NewBankClientHandler(Socket s) throws IOException {
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  /**
   * Logs in a user.
   * @return A logged in customer or null.
   * @throws IOException
   */
  private CustomerID loginUser() throws IOException {
    out.println("\nEnter Username");
    String userName = in.readLine();
    out.println("\nEnter Password");
    String password = in.readLine();
    out.println("\nChecking Details...");
    // Authenticate user and get customer ID token from bank for use in subsequent requests.
    return bank.checkLogInDetails(userName, password);
  }

  /**
   * Keep logging the user in until a correct username and password are provided.
   * @return A logged in customer.
   * @throws IOException
   */
  private CustomerID loginUserLoop() throws IOException {
    // Try to log in a user
    CustomerID customer = loginUser();
    // If the login fails retry. 
    // TODO: For now the login can fail only if the username is incorrect i.e. password is not 
    // checked at all.
    while(customer == null) {
      out.println("\nIncorrect username. Please try again.");
      customer = loginUser();
    }
    out.println("\nLogin successful, welcome to NewBank!");
    out.println(printHelp());
    return customer;
  }

  /**
   * Creates a new user. As of now only a unique username and a strong password are
   * required to create a new user. By default a 'Main' Account with a balance of 0 will be created
   * for the new user.
   * @return A new customer or null.
   * @throws IOException
   */
  private CustomerID createNewUser() throws IOException {
    out.println("\nEnter Username");
    String userName = in.readLine();
    out.println("\nEnter Password");
    String password = in.readLine();
    return bank.addCustomer(userName, password, out);
  }

  /**
   * Keep creating a new user until a unique username and a valid password are provided.
   * @return A new customer.
   * @throws IOException
   */
  private void createNewUserLoop() throws IOException {
    // try to create a new user
    CustomerID newCustomer = createNewUser();
    // if the creation fails retry
    while(newCustomer == null) {
      newCustomer = createNewUser(); 
    }
  }

  /**
   * Provides the user with intial menu to either login or create a new user account.
   * @return A logged in / new customer. 
   * @throws IOException
   */
  private CustomerID loginScreen() throws IOException {
    out.println("\nEnter:\n1 to login.\n2 to open a NewBank Account.\n");
    String selectedOption = in.readLine();
    if (selectedOption.equals("1")) {
      return loginUserLoop();
    } else if (selectedOption.equals("2")) {
      createNewUserLoop();
      // After the User creates a new account, he returns to to the main menu.
      return loginScreen();
    } else {
      out.println("\nInvalid option selected."); 
      // If the user selects anything other than 1 or 2 he returns to the main menu.
      return loginScreen();
    }
  }

  /**
   * Help function provides user with list of available command options.
   * @return The available options will be listed.
   */
  public static String printHelp(){
    return (
        "\nHere are your available options:\n"
      + "\n"
      + "SHOWMYACCOUNTS\n"
      + "Returns a list of all the customers accounts along with their current balance\n"
      + "e.g. Main: 1000.0\n"
      + "\n"
      + "NEWACCOUNT <Name>\n"
      + "e.g. NEWACCOUNT Savings\n"
      + "Returns SUCCESS or FAIL\n"
      + "\n"
      + "MOVE <Amount> <From> <To>\n"
      + "e.g. MOVE 100 Main Savings\n"
      + "Returns SUCCESS or FAIL\n"
      + "\n"
      + "PAY <Person/Company> <Amount>\n"
      + "e.g. PAY John 100\n"
      + "Returns SUCCESS or FAIL\n"
      + "\n"
      + "GIVE-LOAN <Account> <ReceiverName> <Amount>\n"
      + "e.g. GIVE-LOAN Main John 100\n"
      + "Returns SUCCESS or FAIL\n"
      + "\n"
      + "REQUEST-LOAN <Amount>\n"
      + "e.g. REQUEST-LOAN 100\n"
      + "Returns SUCCESS or FAIL\n"
      + "\n"
      + "PAY-LOAN <Account> <Amount>\n"
      + "e.g. PAY-LOAN Main 100\n"
      + "Returns SUCCESS or FAIL\n"
      + "\n"
      + "To view these options again, please type 'HELP' at anytime.\n"
    );
  }

  private void processUserRequest(CustomerID customer) throws IOException {
    while(true) {
      String request = in.readLine();
      System.out.println("Request from " + customer.getKey());
      String response = bank.processRequest(customer, request);
      out.println(response);
    }
  }

  /**
   * Handles the user interface
   */
  public void run() {
    try {
      CustomerID customer = loginScreen();
      // After the user is authenticated, get requests from the user and process them.
      processUserRequest(customer);
    } catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }

}