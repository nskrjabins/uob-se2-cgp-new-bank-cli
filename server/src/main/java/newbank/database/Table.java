package newbank.database;

import newbank.database.csv.DataFrame;
import newbank.database.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Table class encapsulates the logic of the controller the database package.
 *
 * It is used to update parameters in tables, parse and filter the CSV data.
 * */
class Table {
  private int columnNumber;
  private int lastIndex = 0;
  private HashMap<String, Column> schema;
  private ArrayList<Database.DATA_TYPES> typeList;
  private final DataFrame dataFrame;

  /**
   * It instantiates a Table class.
   *
   * @param path {@code String} Path where .csv files would be created.
   * @param name {@code String} Table name.
   * @param columns {@code ArrayList<String>} Table columns.
   * @param columnTypes {@code DATA_TYPES} Data type of the columns. The index must match the column it is describing in the columns ArrayList.
   * */
  public Table(String path, String name, ArrayList<String> columns, ArrayList<Database.DATA_TYPES> columnTypes) {
    String filePath = String.format("%s%s.csv", path, name);
    File csv = new File(filePath);
    if(!csv.exists()) {
      this.dataFrame = new DataFrame(filePath, columns);
    } else {
      this.dataFrame = new DataFrame(filePath);
    }
    this.columnNumber = columns.size();
    this.schema = new HashMap<>();
    this.typeList = columnTypes;
    for (int i = 0; i < this.columnNumber; i++) {
      this.schema.put(columns.get(i), new Column(columnTypes.get(i), i));
    }
  }

  /**
   * It instantiates a Table class.
   *
   * @param path {@code String} Path where .csv files would reside.
   * @param configPath {@code String} Path where .config files would reside.
   * @param name {@code String} Table name.
   * */
  public Table(String path, String configPath, String name) {
    String filePath = String.format("%s%s.csv", path, name);
    this.loadConfig(String.format("%s%s.txt", configPath, name));
    this.dataFrame = new DataFrame(filePath);
  }

  /**
   * It inserts a record in the table.
   *
   * @param row {@code ArrayList<String>} fields to insert in the table
   *
   * @throws InvalidColumnNumberException if the amount of fields doesn't correspond with the amount of columns.
   * */
  public void insert(ArrayList<String> row) throws InvalidColumnNumberException {
    if(this.columnNumber != row.size()) {
      throw new InvalidColumnNumberException("The number fields doesn't match the number of columns of the table");
    }
    this.lastIndex++;
    row.add(0, Integer.toString(this.lastIndex));
    this.dataFrame.addRow(row);
  }

  /**
   * It searches and retrieves a set of rows based on the passed criteria.
   *
   * @param column {@code String} Name of the column that will be used to filter.
   * @param param {@code Object} Parameter that will be used to match the fields in the selected column.
   * @param stopAtFirst {@code boolean} Whether to stop at first occurrence.
   *
   * @throws ColumnNotFound If the column doesn't exist.
   * @throws ColumnDataTypeAffinityException if the data type of param doesn't match the type on the table.
   *
   * @return {@code ArrayList<ArrayList<Object>>} the set of records that match the criteria
   * */
  public ArrayList<ArrayList<Object>> find(String column, Object param, boolean stopAtFirst) throws ColumnNotFound, ColumnDataTypeAffinityException {
    Column col = this.schema.get(column);
    if(col == null) { throw new ColumnNotFound("The column you're querying doesn't exist"); }
    if(!Database.checkTypeAffinity(col.getType(), param)) { throw new ColumnDataTypeAffinityException("The parameter you're passing doesn't match the column type");}
    int columnIndex = col.getNumber();
    return this.filterResults(columnIndex, param, stopAtFirst, false);
  }

  /**
   * It searches and updates the first occurrence based on the passed criteria.
   *
   * @param findColumn {@code String} Name of the column that will be used to filter.
   * @param findParam {@code Object} Parameter that will be used to match the fields in the selected column.
   * @param updateColumn {@code String} Name of the column where the field that will be updated resides.
   * @param updateParam {@code Object} Parameter that will be replace the older parameter.
   *
   * @return {@code boolean} if the record was updated successfully.
   *
   * @throws ColumnNotFound If the column doesn't exist.
   * @throws ColumnDataTypeAffinityException if the data type of param doesn't match the type on the table.
   * */
  public boolean update(String findColumn, Object findParam, String updateColumn, Object updateParam) throws ColumnNotFound, ColumnDataTypeAffinityException {
    Column findCol = this.schema.get(findColumn);
    Column updateCol = this.schema.get(updateColumn);
    if(findCol == null || updateCol == null) { throw new ColumnNotFound("The column you're querying doesn't exist"); }
    if( !Database.checkTypeAffinity(findCol.getType(), findParam) || !Database.checkTypeAffinity(updateCol.getType(), updateParam) ) { throw new ColumnDataTypeAffinityException("The parameter you're passing doesn't match the column type"); }
    int columnIndex = findCol.getNumber();
    ArrayList<ArrayList<Object>> result = this.filterResults(columnIndex, findParam, true, true);
    if (result.size() == 0) { return false; }

    ArrayList<Object> rowToUpdate = result.get(0);
    int updateColIndex = updateCol.getNumber();
    rowToUpdate.set(updateColIndex + 1, updateParam);
    ArrayList<String> row = new ArrayList<>();
    for (Object field: rowToUpdate) {
      row.add(field.toString());
    }
    this.dataFrame.replaceLine((int) rowToUpdate.get(0), row);
    return true;
  }

  /**
   * It filters a DataFrame based on the criteria passed.
   *
   * @param columnIndex {@code int} Index of the column used to filter results.
   * @param param {@code Object} Parameter that will be used to match the fields in the selected column.
   * @param stopAtFirst {@code boolean} Whether to stop at first occurrence.
   * @param addIndex {@code boolean} Whether to add the index of the row.
   *
   * @return {@code ArrayList<ArrayList<Object>>} Records that match the criteria.
   * */
  private ArrayList<ArrayList<Object>> filterResults(int columnIndex, Object param, boolean stopAtFirst, boolean addIndex) {
    ArrayList<ArrayList<Object>> result = new ArrayList<>();
    try {
      ArrayList<String[]> dataFrameData = this.dataFrame.getData();
      for (String[] row: dataFrameData) {
        if(row[columnIndex + 1].equals(param.toString())) {
          ArrayList<Object> parsedRow = parseResult(row);
          if (addIndex) {
            parsedRow.add(0, Integer.parseInt(row[0]));
          }
          result.add(parsedRow);
          if (stopAtFirst) { break; }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * It will parse a row of Strings into the corresponding Objects.
   *
   * @param row {@code String[]} row of fields.
   *
   * @return {@code ArrayList<Object>} List of parsed fields.
   * */
  private ArrayList<Object> parseResult(String[] row){
    ArrayList<Object> result = new ArrayList<>();
    for (int i = 0; i < this.typeList.size(); i++) {
      result.add(Database.string2Object(this.typeList.get(i), row[i+1]));
    }
    return result;
  }

  /**
   * It loads the config on the instance.
   *
   * @param configPath {@code String} Path to config file for this table.
   * */
  private void loadConfig(String configPath) {
    Path configFilePath = Paths.get(configPath);
    AtomicInteger i = new AtomicInteger();
    i.set(0);
    try {
      this.schema = new HashMap<>();
      this.typeList = new ArrayList<>();
      Files.lines(configFilePath).forEach(config -> {
        String[] configArray = config.split("=");
        String columnName = configArray[0];
        Database.DATA_TYPES type = Database.DATA_TYPES.valueOf(configArray[1]);
        this.schema.put(columnName, new Column(type, i.get()));
        this.typeList.add(type);
        i.getAndIncrement();
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
