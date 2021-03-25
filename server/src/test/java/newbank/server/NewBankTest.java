package newbank.server;

import org.junit.Test;
import static org.junit.Assert.*;

public class NewBankTest {
  protected NewBank newBank = new NewBank();
  protected CustomerID validCustomerID = new CustomerID("test");
  protected CustomerID wrongCustomerID = new CustomerID("NotAUser");

  @Test
  public void testShowAccount() {
    String response = newBank.processRequest(validCustomerID, "SHOWMYACCOUNTS");
    assertEquals("Main: 1000.0", response);
    response = newBank.processRequest(wrongCustomerID, "SHOWMYACCOUNTS");
    assertEquals("FAIL", response);
  }
  @Test
  public void testNewAccountNoName() {
    String response = newBank.processRequest(validCustomerID, "NEWACCOUNT");
    assertEquals(NewBank.NOT_ACCOUNT_NAME_MSG, response);
  }
  @Test
  public void testNewAccountAlreadyExists() {
    String response = newBank.processRequest(validCustomerID, "NEWACCOUNT Main");
    assertEquals(NewBank.ACCOUNT_ALREADY_EXISTS_MSG, response);
  }
  @Test
  public void testNewAccount() {
    String newAccountName = "TestAccount";
    String response = newBank.processRequest(validCustomerID, String.format("NEWACCOUNT %s", newAccountName));
    assertEquals(String.format("'%s' %s. %s",
            newAccountName, NewBank.ACCOUNT_CREATED_SUCCESSFULLY_MSG, NewBank.ANYTHING_ELSE_MSG), response);
  }
}
