package auxiliary;

import java.io.IOException;

public class InvalidFileException extends IOException {
  public InvalidFileException() {
    super();
  }

  public InvalidFileException(String message) {
    super(message);
  }
}
