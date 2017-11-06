package table_manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public interface AbstractTableReader {
  boolean isValidFile(@Nullable File file);

  @NotNull
  ConcreteTableModel readTable(@Nullable File file) throws IOException;
}
