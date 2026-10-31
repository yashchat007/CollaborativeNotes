import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        NoteManager manager = new NoteManager("notes.txt");
        try {
            manager.loadFromFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not load saved notes: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            NotesAppGUI app = new NotesAppGUI(manager);
            app.setVisible(true);
        });
    }
}