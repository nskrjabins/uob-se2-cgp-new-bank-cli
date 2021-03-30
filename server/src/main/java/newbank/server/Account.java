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

  public int accountNumberGenerator() {
    Random rnd = new Random();
    int n = 1000000 + rnd.nextInt(9000000);
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
    this.openingBalance = openingBalance;
    this.accountNumber = accountNumberGenerator();
  }

  /**
   * Creates an instance
   *
   * @param accountName {@code String}
   * */
  public Account(String accountName) {
    this.accountName = accountName;
    this.openingBalance = 0.0;
  }

  public String toString() {
    return ("Account Name: " + accountName +
            "\nAccount No: " + accountNumber +
            "\nBalance: " + openingBalance +
            "\n");
  }

}
