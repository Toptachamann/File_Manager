package table_manager;

import auxiliary.FileFormatException;
import auxiliary.InvalidFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
      ArrayList<String> columnNames = arrayFromObject(tableObject.getJsonArray("Column names"), columnCount);
      ArrayList<String> rowNames = arrayFromObject(tableObject.getJsonArray("Row names"), columnCount);
      HashMap<String, Integer> columnMap = mapFromObejct(tableObject.getJsonObject("Column map"));
      HashMap<String, Integer> rowMap = mapFromObejct(tableObject.getJsonObject("Row map"));
      ArrayList<ArrayList<String>> data =
          tableFromObject(tableObject.getJsonObject("Table of values"), rowCount, columnCount);
      ArrayList<ArrayList<String>> expressions =
          tableFromObject(tableObject.getJsonObject("Table of expressions"), rowCount, columnCount);
      ConcreteTableModel tableModel =
          new ConcreteTableModel(rowCount, columnCount, columnNames, rowNames, columnMap, rowMap, data, expressions);
      return tableModel;
    }
  }

  private ArrayList<ArrayList<String>> tableFromObject(
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

  private ArrayList<String> arrayFromObject(JsonArray array, int columnCount)
      throws FileFormatException {
    if (columnCount != array.size()) {
      throw new FileFormatException("Column count isn't equal to column names size");
    }
    ArrayList<String> result = new ArrayList<>(Collections.nCopies(columnCount, null));
    for (int i = 0; i < result.size(); i++) {
      result.set(i, array.getString(i));
    }
    return result;
  }

  private HashMap<String, Integer> mapFromObejct(JsonObject map) throws FileFormatException {
    HashMap<String, Integer> result = new HashMap<>();
    for(Map.Entry<String, JsonValue> entry : map.entrySet()){
      JsonValue.ValueType type = entry.getValue().getValueType();
      if(type.equals(JsonValue.ValueType.NUMBER)){
        result.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
      }else{
        throw new FileFormatException("Not number");
      }
    }
    return result;
  }
}
