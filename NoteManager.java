import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoteManager {
    private List<Note> notes;
    private String fileName;
    public NoteManager(String fileName) {
        this.notes = new ArrayList<>();
        this.fileName = fileName;
    }
    public void addNote(Note note) {
        notes.add(note);
    }
    public void deleteNote(Note note, User user) throws PermissionDeniedException {
        if (!note.getOwner().getUsername().equals(user.getUsername())) {
            throw new PermissionDeniedException("Only the owner can delete this note");
        }
        notes.remove(note);
    }
    public List<Note> getAllNotes() {
        return new ArrayList<>(notes);
    }
    public List<Note> search(String keyword) {
        List<Note> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Note n : notes) {
            if (n.getTitle().toLowerCase().contains(key)
                    || n.getContent().toLowerCase().contains(key)) {
                result.add(n);
            }
        }
        return result;
    }
     public List<Note> search(String keyword, String category) {
        List<Note> result = new ArrayList<>();
        for (Note n : search(keyword)) {
            if (n.getCategory().equalsIgnoreCase(category)) {
                result.add(n);
            }
        }
        return result;
    }
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        for (Note n : notes) {
            if (!categories.contains(n.getCategory())) {
                categories.add(n.getCategory());
            }
        }
        return categories;
    }
     public void saveToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Note n : notes) {
                String collaborators = "";
                if (n instanceof SharedNote) {
                    SharedNote shared = (SharedNote) n;
                    for (User u : shared.getCollaborators()) {
                        collaborators += u.getUsername() + ",";
                    }
                }
                String content = n.getContent().replace("\n", "\\n");
                writer.write(n.getType() + "|" + n.getTitle() + "|" + n.getCategory() + "|"
                        + n.getOwner().getUsername() + "|" + collaborators + "|" + content);
                writer.newLine();
            }
        }
    }
    public void loadFromFile() throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 6);
                if (parts.length < 6) {
                    continue;
                }
                String type = parts[0];
                String title = parts[1];
                String category = parts[2];
                User owner = new User(parts[3]);
                String content = parts[5].replace("\\n", "\n");
                if (type.equals("Shared")) {
                    SharedNote shared = new SharedNote(title, content, category, owner);
                    for (String name : parts[4].split(",")) {
                        if (!name.isEmpty()) {
                            shared.addCollaborator(new User(name));
                        }
                    }
                    notes.add(shared);
                } else {
                    notes.add(new PersonalNote(title, content, category, owner));
                }
            }
        }
    }    
}