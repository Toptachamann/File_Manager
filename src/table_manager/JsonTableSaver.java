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

public class JsonTableSaver implements AbstractTableSaver {
  private static final String extension = ".json";

  public JsonTableSaver() {}

  @Override
  public boolean isValidFile(@Nullable File file) {
    return file != null && file.exists() && file.isFile() && file.canWrite() && file.getName().toLowerCase().endsWith(extension);
  }

  @Override
  public void persist(@Nullable File file, @NotNull ConcreteTableModel tableModel) throws IOException {
    if (!isValidFile(file)) {
      throw new InvalidFileException("Can't write to specified file: " + file);
    } else {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
          JsonWriter jsonWriter = Json.createWriter(writer)) {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("Column count", tableModel.getColumnCount());
        objectBuilder.add("Row count", tableModel.getRowCount());
        objectBuilder.add("Column names", toArray(tableModel.getColumnNames()));
        objectBuilder.add("Table of values", getTable(tableModel.getValues()));
        objectBuilder.add("Table of expressions", getTable(tableModel.getExpressions()));

        JsonObject jsonObject = objectBuilder.build();
        jsonWriter.writeObject(jsonObject);
      }
    }
  }

  @NotNull
  private JsonArray toArray(Object[] array) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (Object obj : array) {
      arrayBuilder.add(obj.toString());
    }
    return arrayBuilder.build();
  }

  @NotNull
  private JsonObject getTable(Object[][] table) {
    int rowCount = table.length;
    if (rowCount > 0) {
      int columnCount = table[0].length;
      JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
      for (int row = 0; row < rowCount; row++) {
        JsonArrayBuilder rowArrayBuilder = Json.createArrayBuilder();
        for (int column = 0; column < columnCount; column++) {
          Object entry = table[row][column];
          if (entry != null) {
            JsonObject object =
                Json.createObjectBuilder().add(String.valueOf(column), entry.toString()).build();
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
