import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

/**
 * Created by Timofey on 9/9/2017.
 */
public class Main {
    public static void main(String[] argc) throws IOException {
        PrintStream out = System.out;

        File file = new File("D:\\");
        out.println(file.getName());
        out.println(file.toString());
        /*File file = new File("D:\\File_Manager_Test_Folder");
        for(int i = 1; i <= 10; i++){
            File child = new File(file.getAbsolutePath() + "\\" + i + ".txt");
            child.createNewFile();
        }
        PrintStream out = System.out;
        Scanner in = new Scanner(System.in);
        out.println("Enter variable a");
        int a = in.nextInt();
        out.println("Enter variable b");
        int b = in.nextInt();
        out.println("Enter variable c");
        int c = in.nextInt();

        b^= a^(c ^= b^(a^= a^c));

        out.println("a: " + a);
        out.println("b: " + b);
        out.println("c: " + c);*/


        /*UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for(UIManager.LookAndFeelInfo info : infos){
            out.println(info.getName());
            out.println(info.getClassName());
        }

        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(String fontName : fontNames){
            out.println(fontName);
        }
        File file = new File("D:\\");
        File[] roots = file.listRoots();
        for(File root : roots){
            out.println(root);
        }*/

        EventQueue.invokeLater(()->{
            FileManagerFrame frame = new FileManagerFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

}
