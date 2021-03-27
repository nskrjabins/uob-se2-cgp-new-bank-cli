package newbank.server;

public class Account {

  private String accountName;
  private double openingBalance;
  private int accountNumber;
  private int sortCode;

  public Account(String accountName, double openingBalance, int accountNumber, int sortCode) {
    this.accountName = accountName;
    this.openingBalance = openingBalance;
    this.accountNumber = accountNumber;
    this.sortCode = sortCode;

  }

  public String toString() {
    return ("Account Name: " + accountName +
            "\nAccount No: " + accountNumber +
            "\n" + "Sort Code: " + sortCode +
            "\nBalance: " + openingBalance +
            "\n");
  }

}
