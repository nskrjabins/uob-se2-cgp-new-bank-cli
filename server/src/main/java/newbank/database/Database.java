package newbank.database;

import newbank.database.exceptions.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * The database package is designed following MVC paradigm.
 *
 * The Database class represent the view of the database package.
 * It can be used by developers to make Create, Update operations on a CSV file.
 * */
public class Database {
  private final HashMap<String, Table> tables;
  private String path = String.format("%s/.newBankDatabase/", System.getProperty("user.dir"));
  private String configPath = String.format("%s/.newBankDatabase/config/", System.getProperty("user.dir"));
  public enum DATA_TYPES {
    BOOLEAN,
    CHAR,
    STRING,
    FLOAT,
    DOUBLE,
    INTEGER,
    LONG,
  }

  /**
   * It will create a {@code File} instance.
   * By default it will create all the data files (.csv) in the ./.newBankDatabase/ folder.
   * Also it will create a folder named .config to persist config files (.txt)
   * */
  public Database() {
    this.tables = new HashMap<>();
  }

  /**
   * It will create a {@code File} instance and store the files (config and data) on the passed path.
   *
   * @param path {@code String} The desired path where files will be written.
   * */
  public Database(String path) {
    this.tables = new HashMap<>();
    this.path = path;
  }

  /**
   * It will start a Database from an existing config and data set.
   *
   * @throws FileNotFoundException If the database folder doesn't exist.
   * */
  public void init() throws FileNotFoundException {
    System.out.println("Initiating New Bank DB");
    File currentPath = new File(this.path);
    if(!currentPath.exists()) {
      throw new FileNotFoundException("The requested directory doesn't exist");
    }
    System.out.println("Reading Files...");
    File databaseFolder = new File(this.path);
    for (final File file: Objects.requireNonNull(databaseFolder.listFiles())) {
      if(FilenameUtils.getExtension(file.getName()).endsWith("csv")) {
        String tableName = FilenameUtils.getBaseName(file.getName());
        this.tables.put(tableName, new Table(this.path, this.configPath, tableName));
        System.out.printf("Table %s successfully Loaded!\n", tableName);
      }
    }
    System.out.println("New Bank DB Ready to Use");
  }

  /**
   * It will start a Database from a non-existing database
   * */
  public void create() {
    File currentPath = new File(this.path);
    if(!currentPath.exists()) {
      currentPath.mkdir();
    } else {
      try {
        FileUtils.cleanDirectory(currentPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * It will add a table to the database.
   *
   * @param name {@code String} Table name.
   * @param columns {@code ArrayList<String>} Table columns.
   * @param columnTypes {@code DATA_TYPES} Data type of the columns. The index must match the column it is describing in the columns ArrayList.
   *
   * @throws TableAlreadyExistsException If the table already exists. Name is repeated.
   * @throws InvalidColumnNumberException If no column is passed or if the column types number and the columns number don't match.
   * */
  public void addTable(String name, ArrayList<String> columns, ArrayList<Database.DATA_TYPES> columnTypes) throws TableAlreadyExistsException, InvalidColumnNumberException {
    if(this.tables.get(name) != null) { throw new TableAlreadyExistsException("Couldn't create table: Table already exists."); }
    if (columns.size() == 0) { throw new InvalidColumnNumberException("The number of columns must be greater than 0"); }
    if (columns.size() != columnTypes.size()) { throw new InvalidColumnNumberException("Miss match between schema and columns"); }
    this.tables.put(name, new Table(this.path, name, columns, columnTypes));
    this.buildTableConfig(name, columns, columnTypes);
  }

  /**
   * It will insert a row in a table.
   *
   * @param table {@code String} Table name.
   * @param fields {@code ArrayList<String>} Fields to insert.
   *
   * @throws TableNotFoundException If the table doesn't exist.
   * @throws InvalidColumnNumberException If the amount of fields and the columns number don't match.
   * */
  public void insert(String table, ArrayList<String> fields) throws TableNotFoundException, InvalidColumnNumberException {
    if(this.checkTable(table)) { throw new TableNotFoundException(String.format("The table '%s' doesn't exist", table)); }
    this.tables.get(table).insert(fields);
  }

  /**
   * It search for all occurrence on the requested table based on the criteria passed.
   *
   * @param table {@code String} Name of the Table to search.
   * @param column {@code String} Name of the column that will be used to filter.
   * @param param {@code Object} Parameter that will be used to match the fields in the selected column.
   *
   * @throws TableNotFoundException If the table doesn't exist.
   * @throws ColumnNotFound If the column doesn't exist.
   * @throws ColumnDataTypeAffinityException if the data type of param doesn't match the type on the table.
   * */
  public ArrayList<ArrayList<Object>> find(String table, String column, Object param) throws TableNotFoundException, ColumnNotFound, ColumnDataTypeAffinityException {
    if(this.checkTable(table)) { throw new TableNotFoundException(String.format("The table '%s' doesn't exist", table)); }
    return this.tables.get(table).find(column, param, false);
  }

  /**
   * It search for the first occurrence on the requested table based on the criteria passed.
   *
   * @param table {@code String} Name of the Table to search.
   * @param column {@code String} Name of the column that will be used to filter.
   * @param param {@code Object} Parameter that will be used to match the fields in the selected column.
   *
   * @throws TableNotFoundException If the table doesn't exist.
   * @throws ColumnNotFound If the column doesn't exist.
   * @throws ColumnDataTypeAffinityException if the data type of param doesn't match the type on the table.
   * */
  public ArrayList<Object> findOne(String table, String column, Object param) throws TableNotFoundException, ColumnNotFound, ColumnDataTypeAffinityException {
    if(this.checkTable(table)) { throw new TableNotFoundException(String.format("The table '%s' doesn't exist", table)); }
    return this.tables.get(table).find(column, param, true).get(0);
  }

  /**
   * It will search and update the first occurrence on the requested table based on the criteria passed.
   *
   * @param table {@code String} Name of the Table to search.
   * @param findColumn {@code String} Name of the column that will be used to filter.
   * @param findParam {@code Object} Parameter that will be used to match the fields in the selected column.
   * @param updateColumn {@code String} Name of the column where the field that will be updated resides.
   * @param updateParam {@code Object} Parameter that will be replace the older parameter.
   *
   * @return {@code boolean} if the record was updated successfully.
   *
   * @throws TableNotFoundException If the table doesn't exist.
   * @throws ColumnNotFound If the column doesn't exist.
   * @throws ColumnDataTypeAffinityException if the data type of param doesn't match the type on the table.
   * */
  public boolean update(String table, String findColumn, Object findParam, String updateColumn, Object updateParam) throws TableNotFoundException, ColumnNotFound, ColumnDataTypeAffinityException {
    if(this.checkTable(table)) { throw new TableNotFoundException(String.format("The table '%s' doesn't exist", table)); }
    return this.tables.get(table).update(findColumn, findParam, updateColumn, updateParam);
  }

  /**
   * Checks if a table is present in the database.
   *
   * @return {@code boolean} if the table is present.
   * */
  private boolean checkTable(String tableName) {
    return this.tables.get(tableName) == null;
  }

  /**
   * Checks if an object corresponds to the DATA_TYPE value of the enum.
   *
   * @param type {@code DATA_TYPES} Data type to match.
   * @param param {@code Object} parameter to assess.
   *
   * @return {@code boolean} true if the values match, else false.
   * */
  public static boolean checkTypeAffinity(DATA_TYPES type, Object param) {
    switch (type) {
      case STRING:
        return param.getClass() == String.class;
      case CHAR:
        return param.getClass() == Character.class;
      case DOUBLE:
        return param.getClass() == Double.class;
      case FLOAT:
        return param.getClass() == Float.class;
      case INTEGER:
        return param.getClass() == Integer.class;
      case LONG:
        return param.getClass() == Long.class;
      default: // BOOLEAN
        return param.getClass() == Boolean.class;
    }
  }

  /**
   * Transforms a String into the desired Object.
   *
   * @param type {@code DATA_TYPES} Data type to transform.
   * @param param {@code Object} parameter to transform.
   *
   * @return {@code Object} true if the values match, else false.
   * */
  public static Object string2Object(DATA_TYPES type, String param) {
    switch (type) {
      case STRING:
        return param;
      case CHAR:
        return param.charAt(0);
      case DOUBLE:
        return Double.parseDouble(param);
      case FLOAT:
        return Float.parseFloat(param);
      case INTEGER:
        return Integer.parseInt(param);
      case LONG:
        return Long.parseLong(param);
      default: // BOOLEAN
        return Boolean.parseBoolean(param);
    }
  }

  /**
   * Will create a config file of the created table.
   *
   * @param tableName {@code String} Table name.
   * @param fields {@code ArrayList<String>} Table columns.
   * @param columnTypes {@code DATA_TYPES} Data type of the columns.
   * */
  private void buildTableConfig(String tableName, ArrayList<String> fields, ArrayList<Database.DATA_TYPES> columnTypes) {
    String config = "";
    int columnNumber = fields.size();
    for (int i = 0; i < columnNumber; i++) {
      String name = fields.get(i);
      DATA_TYPES type = columnTypes.get(i);
      config += String.format("%s=%s\n", name, type);
    }
    try {
      File currentConfigPath = new File(this.configPath);
      currentConfigPath.mkdir();
      Files.createFile(Paths.get(String.format("%s%s.txt", this.configPath, tableName)));
      Files.write(Paths.get(String.format("%s%s.txt", this.configPath, tableName)), config.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
