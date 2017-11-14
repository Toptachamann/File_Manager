package table_manager;

import auxiliary.InvalidFileException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonTableSaver implements AbstractTableSaver {
  private static final String extension = ".json";

  public JsonTableSaver() {}

  @Override
  public boolean isValidFile(@Nullable File file) {
    return file != null
        && file.exists()
        && file.isFile()
        && file.canWrite()
        && file.getName().toLowerCase().endsWith(extension);
  }

  @Override
  public void persist(@Nullable File file, @NotNull ConcreteTableModel tableModel)
      throws IOException {
    if (!isValidFile(file)) {
      throw new InvalidFileException("Can't write to specified file: " + file);
    } else {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
          JsonWriter jsonWriter = Json.createWriter(writer)) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("Column count", tableModel.getColumnCount());
        objectBuilder.add("Row count", tableModel.getRowCount());
        objectBuilder.add("Column created", tableModel.getColumnCreated());
        objectBuilder.add("Row created", tableModel.getRowCreated());
        objectBuilder.add("Column names", persistArray(tableModel.getColumnNames()));
        objectBuilder.add("Row names", persistArray(tableModel.getRowNames()));
        objectBuilder.add("Column map", persistMap(tableModel.getColumnMap()));
        objectBuilder.add("Row map", persistMap(tableModel.getRowMap()));
        objectBuilder.add("Table of values", persistTable(tableModel.getValues()));
        objectBuilder.add("Table of expressions", persistTable(tableModel.getExpressions()));
        JsonObject jsonObject = objectBuilder.build();
        jsonWriter.writeObject(jsonObject);
      }
    }
  }

  private JsonObject persistMap(Map<String, Integer> map) {
    JsonObjectBuilder result = Json.createObjectBuilder();
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      result.add(entry.getKey(), entry.getValue());
    }
    return result.build();
  }

  @NotNull
  private JsonArray persistArray(List<String> array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (String str : array) {
      arrayBuilder.add(str);
    }
    return arrayBuilder.build();
  }

  @NotNull
  private JsonObject persistTable(ArrayList<ArrayList<String>> table) {
    int rowCount = table.size();
    if (rowCount > 0) {
      int columnCount = table.get(0).size();
      JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
      for (int row = 0; row < rowCount; row++) {
        JsonArrayBuilder rowArrayBuilder = Json.createArrayBuilder();
        for (int column = 0; column < columnCount; column++) {
          String entry = table.get(row).get(column);
          if (entry != null) {
            JsonObject object =
                Json.createObjectBuilder().add(String.valueOf(column), entry).build();
            rowArrayBuilder.add(object);
          }
        }
        JsonArray array = rowArrayBuilder.build();
        if (array.size() > 0) {
          objectBuilder.add(String.valueOf(row), array);
        }
      }
      return objectBuilder.build();
    } else {
      return Json.createObjectBuilder().build();
    }
  }
}
