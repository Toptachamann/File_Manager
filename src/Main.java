import TextEditor.TextEditorFrame;
import table_manager.TableEditorFrame;

import javax.swing.JFrame;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

/** Created by Timofey on 9/9/2017. */
public class Main {
  public static void main(String[] argc) throws IOException {
    EventQueue.invokeLater(
        () -> {
          /*GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
          Font[] fonts = e.getAllFonts(); // Get the fonts
          for (Font f : fonts) {
            System.out.println(f.getFontName());
          }*/
          /*FileManagerFrame frame = new FileManagerFrame();
          frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          frame.setVisible(true);*/
          /*try {
            TextEditorFrame textEditor = new TextEditorFrame(new File("C:\\File_Manager_Test\\table.txt"));
            textEditor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            textEditor.setVisible(true);
          } catch (Throwable t) {
            t.printStackTrace();
          }

          */

          try {
            TableEditorFrame frame = new TableEditorFrame(new File("C:\\File_Manager_Test\\new table.json"));
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
          } catch (Throwable t) {
            t.printStackTrace();
          }
        });
  }
}
