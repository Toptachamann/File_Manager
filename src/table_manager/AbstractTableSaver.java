package table_manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public interface AbstractTableSaver {
  boolean isValidFile(@Nullable File file);

  void persist(@Nullable File file, @NotNull ConcreteTableModel tableModel) throws IOException;
}
