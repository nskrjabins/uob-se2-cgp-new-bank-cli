package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
   * Logs in a customer
   * @return a logged in customer or null
   * @throws IOException
   */
  private CustomerID loginUser() throws IOException {
    // ask for user name
    out.println("Enter Username");
    String userName = in.readLine();
    // ask for password
    out.println("Enter Password");
    String password = in.readLine();
    out.println("Checking Details...");
    // authenticate user and get customer ID token from bank for use in subsequent requests

    return bank.checkLogInDetails(userName, password);
  }

  /**
   * Handles the user interface
   */
  public void run() {
    // keep getting requests from the client and processing them
    try {
      // try to log in a user
      CustomerID customer = loginUser();
      // if the login failed retry
      while(customer == null) {
        out.println("Log In Failed. Please try again.");
        customer = loginUser();
      }
      // if the user is authenticated then get requests from the user and process them
      out.println("Login successful, welcome to NewBank!");
      out.println(printHelp());
      while(true) {
        String request = in.readLine();
        System.out.println("Request from " + customer.getKey());
        String response = bank.processRequest(customer, request);
        out.println(response);
      }
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

  /**
   * Help function provides user with list of available command options.
   * @return The available options will be listed.
   */
  public static String printHelp(){
    return "\nHere are your available options:\n"+
            "\n"+
            "SHOWMYACCOUNTS\n"+
            "Returns a list of all the customers accounts along with their current balance\n"+
            "e.g. Main: 1000.0\n"+
            "\n"+
            "NEWACCOUNT <Name>\n"+
            "e.g. NEWACCOUNT Savings\n"+
            "Returns SUCCESS or FAIL\n"+
            "\n"+
            "MOVE <Amount> <From> <To>\n"+
            "e.g. MOVE 100 Main Savings\n"+
            "Returns SUCCESS or FAIL\n"+
            "\n"+
            "PAY <Person/Company> <Ammount>\n"+
            "e.g. PAY John 100\n"+
            "Returns SUCCESS or FAIL\n"+
            "\n"+
            "\n"+
            "To view these options again, please type 'HELP' at anytime.\n";
  }
}