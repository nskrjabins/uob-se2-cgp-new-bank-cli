package newbank.database;

public class Column {
  private Database.DATA_TYPES type;
  private int number;

  public Column(Database.DATA_TYPES type, int number) {
    this.type = type;
    this.number = number;
  }
  public Database.DATA_TYPES getType() {
    return type;
  }
  public int getNumber() {
    return number;
  }
}
