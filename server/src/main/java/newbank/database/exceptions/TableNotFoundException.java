package newbank.database.exceptions;

public class TableNotFoundException extends Exception{
  public TableNotFoundException(String message) {
    super(message);
  }
}
