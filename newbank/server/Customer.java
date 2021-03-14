package newbank.server;

import java.util.ArrayList;

/**
 * The Customer class stores user's login details, as well as all accounts which belong to a
 * customer.
 */
public class Customer {

  /** Customer's user name, which is necessary for login purposes. */
  private String userName;
  /** Customer's password, which is necessary for login purposes. */
  private String password;
  /** A list of accounts which belong to the customer. */
  private ArrayList<Account> accounts;

  /**
   * @param userName The user name, which customer will need for login purposes.
   * @param password The password, which customer will need for login purposes.
   */
  public Customer(String userName, String password) {
    this.userName = userName;
    this.password = password;
    accounts = new ArrayList<>();
  }

  /**
   * @return A string containing all accounts which belong to the customer.
   */
  public String accountsToString() {
    String s = "";
    for(Account a : accounts) {
      s += a.toString();
    }
    return s;
  }

  /** Adds an account to the customer. */
  public void addAccount(Account account) {
    accounts.add(account);
  }

  /** @return Customer's user name.*/
  public String getUserName() {
    return userName;
  }

  /** @return Customer's password.*/
  public String getPassword() {
    return password;
  }
}
