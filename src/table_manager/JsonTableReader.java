package table_manager;

import auxiliary.FileFormatException;
import auxiliary.InvalidFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class JsonTableReader implements AbstractTableReader {
  private static final String extension = ".json";
  private int rowCount;
  private int columnCount;

  public JsonTableReader() {}

  @Override
  public boolean isValidFile(@Nullable File file) {
    return file != null
        && file.exists()
        && file.canWrite()
        && file.getName().toLowerCase().endsWith(extension);
  }

  @Override
  @NotNull
  public ConcreteTableModel readTable(@Nullable File file) throws IOException {
    if (!isValidFile(file)) {
      throw new InvalidFileException("Can't read from specified file: " + file);
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(file));
        JsonReader jsonReader = Json.createReader(reader)) {
      JsonObject tableObject = jsonReader.readObject();
      rowCount = tableObject.getInt("Row count");
      columnCount = tableObject.getInt("Column count");
      ArrayList<String> columnNames = getColumnNames(tableObject, columnCount);
      ArrayList<ArrayList<String>> data =
          getTable(tableObject.getJsonObject("Table of values"), rowCount, columnCount);
      ArrayList<ArrayList<String>> expressions =
          getTable(tableObject.getJsonObject("Table of expressions"), rowCount, columnCount);
      ConcreteTableModel tableModel =
          new ConcreteTableModel(rowCount, columnCount, columnNames, data, expressions);
      return tableModel;
    }
  }

  private ArrayList<ArrayList<String>> getTable(
      JsonObject jsonObject, int rowCount, int columnCount) throws FileFormatException {
    ArrayList<ArrayList<String>> result = new ArrayList<>(rowCount);
    for (int i = 0; i < rowCount; i++) {
      result.add(new ArrayList<>(Collections.nCopies(columnCount, null)));
    }
    Set<String> set = jsonObject.keySet();
    for (String i : set) {
      int rowIndex = Integer.parseInt(i);
      JsonArray rowArray = jsonObject.getJsonArray(i);
      for (int j = 0; j < rowArray.size(); j++) {
        JsonObject value = rowArray.getJsonObject(j);
        String strColumnIndex = value.keySet().iterator().next();
        int columnIndex = Integer.parseInt(strColumnIndex);
        validateTablePosition(rowIndex, columnIndex);
        result.get(rowIndex).set(columnIndex, value.getString(strColumnIndex));
      }
    }
    return result;
  }

  private void validateTablePosition(int row, int column) throws FileFormatException {
    if (row < 0 || row >= rowCount || column < 0 || column >= columnCount) {
      throw new FileFormatException("Invalid table position");
    }
  }

  private ArrayList<String> getColumnNames(JsonObject tableObject, int columnCount)
      throws FileFormatException {
    JsonArray names = tableObject.getJsonArray("Column names");
    if (columnCount != names.size()) {
      throw new FileFormatException("Column count isn't equal to column names size");
    }
    ArrayList<String> result = new ArrayList<>(Collections.nCopies(columnCount, null));
    for (int i = 0; i < result.size(); i++) {
      result.set(i, names.getString(i));
    }
    return result;
  }
}
