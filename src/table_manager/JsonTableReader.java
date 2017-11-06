package table_manager;

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
import java.util.Set;

public class JsonTableReader implements AbstractTableReader {
  private static final String extension = ".json";

  public JsonTableReader() {}

  @Override
  public boolean isValidFile(@Nullable File file) {
    return file != null && file.exists() && file.canWrite() && file.getName().toLowerCase().endsWith(extension);
  }

  @Override
  @NotNull
  public ConcreteTableModel readTable(@Nullable File file) throws IOException {
    if(!isValidFile(file)){
      throw new InvalidFileException("Can't read from specified file: " + file);
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(file));
        JsonReader jsonReader = Json.createReader(reader)) {
      JsonObject tableObject = jsonReader.readObject();
      int rowCount = tableObject.getInt("Row count");
      int columnCount = tableObject.getInt("Column count");
      String[] columnNames = getColumnNames(tableObject);
      String[][] data = getTable(tableObject.getJsonObject("Table of values"), rowCount, columnCount);
      String[][] expressions = getTable(tableObject.getJsonObject("Table of expressions"), rowCount, columnCount);
      ConcreteTableModel tableModel = new ConcreteTableModel(rowCount, columnCount, columnNames, data, expressions);
      return tableModel;
    }
  }

  private String[][] getTable(JsonObject jsonObject, int rowCount, int columnCount){
    String[][] result = new String[rowCount][columnCount];
    if(!jsonObject.isEmpty()){
      Set<String> set = jsonObject.keySet();
      for(String i : set){
        int rowIndex = Integer.parseInt(i);
        JsonArray rowArray = jsonObject.getJsonArray(i);
        for(int j = 0; j < rowArray.size(); j++){
          JsonObject value = rowArray.getJsonObject(j);
          String strRowIndex = value.keySet().iterator().next();
          int columnIndex = Integer.parseInt(strRowIndex);
          result[rowIndex][columnIndex] = value.getString(strRowIndex);
        }
      }
    }
    return result;
  }

  private String[] getColumnNames(JsonObject tableObject){
    JsonArray names = tableObject.getJsonArray("Column names");
    String[] result = new String[names.size()];
    for(int i = 0; i < result.length; i++){
      result[i] = names.getString(i);
    }
    return result;
  }
}
