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

  /**
   * Transfer money between two accounts
   *
   * @param amount the amount to transfer
   * @param transferAccount the account to transfer money to
   * @return true if the money was transferred and false if there was not enough money
   */
  public boolean transfer(int amount, Account transferAccount){
    // Check the account has enough money to transfer
    if (this.openingBalance >= amount){
      this.openingBalance -= amount;
      transferAccount.openingBalance += amount;
      return true;
    } else {
      return false;
    }
  }

}
