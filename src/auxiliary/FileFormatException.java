package auxiliary;

import java.io.IOException;

public class FileFormatException extends IOException{
  public FileFormatException() {
    super();
  }

  public FileFormatException(String message) {
    super(message);
  }
}
