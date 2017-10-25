import components.FileManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by Timofey on 9/9/2017.
 */
public class Main {
    public static void main(String[] argc) throws IOException {
        EventQueue.invokeLater(() -> {
            FileManagerFrame frame = new FileManagerFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
