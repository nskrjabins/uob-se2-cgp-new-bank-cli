package newbank.database.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/**
 * The DataFrame class encapsulates the logic of the model of the database package.
 *
 * It serves as interface of the CSV files and offers functionality for the table class to interact with.
 * It does all the direct writing and reading from the file system.
 * */
public class DataFrame {
  private String path;

  /**
   * Instantiates a DataFrame class from an existing CSV
   *
   * @param path {@code String} Path where the csv resides
   * */
  public DataFrame(String path) { this.path = path; }

  /**
   * Instantiates a DataFrame class and creates a CSV.
   *
   * @param path {@code String} Path where the csv should be created
   * @param fields {@code ArrayList<String>} List column names
   * */
  public DataFrame(String path, ArrayList<String> fields) {
    this.path = path;
    this.init(fields);
  }

  /**
   * Creates a CSV file and insert the header column
   *
   * @param fields {@code ArrayList<String>} List column names
   *
   * @return {@code boolean} true if the file was initiated successfully, else false.
   * */
  private boolean init(ArrayList<String> fields) {
    String header = "index, ";
    header += this.buildLine(fields, true);
    try {
      Files.createFile(Paths.get(this.path));
      Files.write(Paths.get(this.path), header.getBytes());
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Adds a row into the CSV.
   *
   * @param fields {@code ArrayList<String>} List column names
   *
   * @return {@code boolean} true if the row was added successfully, else false.
   * */
  public boolean addRow(ArrayList<String> fields) {
    String row = this.buildLine(fields, true);
    try {
      Files.write(Paths.get(this.path), row.getBytes(), StandardOpenOption.APPEND);
      return true;
    }catch (IOException e) {
      return false;
    }
  }

  /**
   * Updates a row on a CSV file.
   *
   * @param rowNumber {@code int} row to update
   * @param fields {@code ArrayList<String>} List column names
   *
   * @return {@code boolean} true if the row was updated successfully, else false.
   * */
  public boolean replaceLine(int rowNumber, ArrayList<String> fields) {
    String row = this.buildLine(fields, false);
    try {
      ArrayList<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(this.path)));
      fileContent.set(rowNumber, row);
      Files.write(Paths.get(this.path), fileContent);
      return true;
    }catch (IOException e) {
      return false;
    }
  }

  /**
   * Returns the Data Frame from the CSV file.
   *
   * @return {@code ArrayList<String[]>}.
   * */
  public ArrayList<String[]> getData() throws IOException {
    ArrayList<String[]> dataFrame = new ArrayList<>();
    Path CSVPath = Paths.get(this.path);
    Files.lines(CSVPath).forEach(row -> {
      dataFrame.add(row.split(", "));
    });
    return dataFrame;
  }

  /**
   * Returns the Data Frame from the CSV file.
   *
   * @param addCR {@code boolean} if a carriage return should be added at the end of the line
   * @param fields {@code ArrayList<String>} List column names
   *
   * @return {@code String} coma-separated line of values.
   * */
  private String buildLine(ArrayList<String> fields, boolean addCR) {
    String row = "";
    int columnNumber = fields.size();
    for (int i = 0; i < columnNumber; i++) {
      String name = fields.get(i);
      row += String.format("%s", name);
      if (i < columnNumber - 1) {
        row += ", ";
      } else {
        if (addCR) { row += "\n"; }
      }
    }
    return row;
  }
}
