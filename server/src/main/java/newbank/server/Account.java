package newbank.server;
/**
 * Customer Account Controller Class
 *
 * This class contains the logic for a New Bank Account.
 */
public class Account {

  private String accountName;
  private double openingBalance;
  private Customer owner = null;

  /**
   * Creates an instance
   *
   * @param accountName {@code String}
   * @param openingBalance {@code double} starting balance of the account
   * */
  public Account(String accountName, double openingBalance) {
    this.accountName = accountName;
    this.openingBalance = openingBalance;
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
    return (accountName + ": " + openingBalance);
  }

}
