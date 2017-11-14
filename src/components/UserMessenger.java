package components;

import javax.swing.JOptionPane;
import java.awt.Frame;
import java.io.File;

public class UserMessenger {
  private Frame owner;

  public UserMessenger(Frame owner) {
    this.owner = owner;
  }

  public void textEditorFailed(File file) {
    JOptionPane.showMessageDialog(
        owner,
        "Can't open " + file.getName() + " in Text Editor",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void tableEditorFailed(File file) {
    JOptionPane.showMessageDialog(
        owner,
        "Can't open " + file.getName() + " in Table Editor",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public int wantsMoveFileToTransh() {
    return JOptionPane.showConfirmDialog(
        owner,
        "Do you want to move this folder to recycle bin?",
        "Confirm dialog",
        JOptionPane.YES_NO_OPTION);
  }
  // need to implement
  public void moveToTrashFailed(File file) {
    JOptionPane.showMessageDialog(
        owner,
        "Can't move " + file.getAbsolutePath() + " to trash",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void selectDirectory() {
    JOptionPane.showMessageDialog(
        owner,
        "Select directory to perform this operation",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public int wantsToClear() {
    return JOptionPane.showConfirmDialog(
        owner,
        "Do you want to delete the content of this file forever?",
        "Confirm dialog",
        JOptionPane.YES_NO_OPTION);
  }

  public void clearContentFailed() {
    JOptionPane.showMessageDialog(
        owner, "Clear content failed", "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  public void moveFileFailed(File source, File destinationFolder) {
    JOptionPane.showMessageDialog(
        owner,
        "Failed to move "
            + source.getAbsolutePath()
            + " to folder "
            + destinationFolder.getAbsolutePath(),
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void copyFileFailed(File source, File destinationFilder) {
    JOptionPane.showMessageDialog(
        owner,
        "Failed to copy "
            + source.getAbsolutePath()
            + " to folder "
            + destinationFilder.getAbsolutePath(),
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void canClearOnlyTxt() {
    JOptionPane.showMessageDialog(
        owner,
        "You can delete only the content of .txt files",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public int wantsToOverwriteFile(File file, File parentDirectory) {
    return JOptionPane.showConfirmDialog(
        owner,
        "File "
            + file.getName()
            + " already exists in "
            + parentDirectory.getAbsolutePath()
            + "\n Do you want to overwrite its content?",
        "Confirm message",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  public int wantsToOverwriteDirectory(File sourceDirectory, File directory, File parentDirectory) {
    return JOptionPane.showConfirmDialog(
        owner,
        "Directory "
            + directory.getName()
            + " already exists in "
            + parentDirectory.getAbsolutePath()
            + "\n Do you want to to replace its content with the content of "
            + sourceDirectory
            + "?",
        "Confirm message",
        JOptionPane.YES_NO_CANCEL_OPTION);
  }

  public void fileIsAncestor(File ancestor, File descendant) {
    JOptionPane.showMessageDialog(
        owner,
        "Folder "
            + ancestor.getAbsolutePath()
            + " is ancestor of "
            + descendant.getAbsolutePath()
            + "\nCan't perform paste operation",
        "Message",
        JOptionPane.INFORMATION_MESSAGE);
  }
}
