package newbank.database.exceptions;

public class TableAlreadyExistsException extends Exception{
  public TableAlreadyExistsException(String message) {
    super(message);
  }
}
