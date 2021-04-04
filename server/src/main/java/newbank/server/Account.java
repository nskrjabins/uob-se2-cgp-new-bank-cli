package newbank.server;
import java.util.*;

/**
 * Customer Account Controller Class
 *
 * This class contains the logic for a New Bank Account.
 */
public class Account {

  private String accountName;
  private double openingBalance;
  private Customer owner = null;
  private int accountNumber;
  private int sortCode;
  private Date openDate;

  private Date todaysDate() {
    Date today = Calendar.getInstance().getTime();
    System.out.print(today);
    return today;
  }

  private int accountNumberGenerator() {
    Random rnd = new Random();
    int n = 1000000 + rnd.nextInt(9000000);
    return n;
  }

  private int sortCodeGenerator() {
    Random rnd = new Random();
    int n = 100000 + rnd.nextInt(900000);
    return n;
  }

  /**
   * Creates an instance
   *
   * @param accountName {@code String}
   * @param openingBalance {@code double} starting balance of the account
   * */
  public Account(String accountName, double openingBalance) {
    this.accountName = accountName;
    this.openingBalance = 0.0;
    this.accountNumber = accountNumberGenerator();
    this.sortCode = sortCodeGenerator();
    this.openDate = todaysDate();
  }

  public String toString() {
    return ("Account Name: " + accountName +
            "\nAccount No: " + accountNumber +
            "\n" + "Sort Code: " + sortCode +
            "\nBalance: " + openingBalance +
            "\nAccount Opened: " + openDate +
            "\n");
  }

}
