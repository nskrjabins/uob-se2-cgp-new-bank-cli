package newbank.server;

public class Account {

  private String accountName;
  private double openingBalance;
  private int accountNumber;

  public Account(String accountName, double openingBalance, int accountNumber) {
    this.accountName = accountName;
    this.openingBalance = openingBalance;
    this.accountNumber = accountNumber;
    this.sortCode = sortCode;

  }

  public String toString() {
    return (accountName + " (Account No:" + accountNumber + ", " + "Sort Code: " + sortCode + "): " + openingBalance);
  }

}
