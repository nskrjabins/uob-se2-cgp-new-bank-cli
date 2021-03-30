package newbank.server;
import java.util.*;


public class Account {

  private String accountName;
  private double openingBalance;
  private int accountNumber;
  private int sortCode;
  private Date openDate;



  public Date todaysDate() {
    Date today = Calendar.getInstance().getTime();
    System.out.print(today);
    return today;
  }

  public int accountNumberGenerator() {
    Random rnd = new Random();
    int n = 1000000 + rnd.nextInt(9000000);
    return n;
  }

  public Account(String accountName, double openingBalance, int sortCode) {
    this.accountName = accountName;
    this.openingBalance = openingBalance;
    this.accountNumber = accountNumberGenerator();
    this.sortCode = sortCode;
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
