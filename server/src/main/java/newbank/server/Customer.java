package newbank.server;

import java.util.HashMap;

public class Customer {
  public static final class AccountAlreadyExists extends Exception {
    public AccountAlreadyExists(String message) {
      super(message);
    }
  }
  private HashMap<String, Account> accounts;

  public Customer() {
    accounts = new HashMap<>();
  }

  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    for(Account a : accounts.values()) {
      s.append(a.toString());
    }
    return s.toString();
  }

  /**
   * Adds a new account to a customer.
   *
   * @param accountName {@code String}
   * @param openingBalance {@code double} starting balance of the account
   *
   * @return {@code Account} newly created account
   * @throws AccountAlreadyExists If the Account doesn't exist
   * */
  public Account addAccount(String accountName, double openingBalance) throws AccountAlreadyExists {
    if(this.accounts.get(accountName) != null) {
      throw new AccountAlreadyExists("There's an existing account with this name");
    }
    accounts.put(accountName, new Account(accountName, openingBalance));
    return this.accounts.get(accountName);
  }

  /**
   * Adds a new account to a customer.
   *
   * @param accountName {@code String}
   *
   * @return {@code Account} newly created account
   * @throws AccountAlreadyExists If the Account doesn't exist
   * */
  public Account addAccount(String accountName) throws AccountAlreadyExists {
    if(this.accounts.get(accountName) != null) {
      throw new AccountAlreadyExists("There's an existing account with this name");
    }
    accounts.put(accountName, new Account(accountName));
    return this.accounts.get(accountName);
  }

  /**
   * Retrieves a customer account by name.
   *
   * @param accountName {@code String}
   *
   * @return {@code Account} newly created account
   * */
  public Account getAccount(String accountName) {
    return this.accounts.get(accountName);
  }

  /**
   * Move money between users accounts if possible
   * @param amount the amount to move
   * @param from the account to move money from
   * @param to the account to move money to
   * @return null if the transfer succeeded, otherwise a string containing the reason for the failure
   */
  public String moveMoneyBetweenAccounts(int amount, String from, String to) {
    Account fromAccount = getAccount(from);
    Account toAccount = getAccount(to);

    // Return an error message if either of the account do not exist
    if (fromAccount == null) {
      return "The account to transfer from does not exist.";
    }
    if (toAccount == null){
      return "The account to transfer to does not exist.";
    }

    // Try to transfer money
    boolean hasTransferredMoney = fromAccount.transfer(amount, toAccount);

    if (hasTransferredMoney) {
      return null;
    } else {
      return "The account does not have enough funds.";
    }
  }
}
