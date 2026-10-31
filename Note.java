import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class Note{
    private static int nextId = 1;
    private static final DateTimeFormatter FMT =DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private int id;
    private String title;
    private String content;
    private String category;
    private User owner;
    private List<String> editHistory;
    public Note(String title,String content,String category,User owner){
        this.id = nextId++;
        this.title = title;
        this.content = content;
        this.category = category;
        this.owner = owner;
        this.editHistory = new ArrayList<>();
        addHistory(owner.getUsername() + " created the note");
    }
    public abstract boolean canEdit(User user);
    public abstract String getType();
    public void edit (String newcontent,User editor)throws PermissionDeniedException {
        if(!canEdit(editor)){
            throw new PermissionDeniedException(editor.getUsername() + " is not allowed to edit this note");

        }
        this.content = newcontent;
        addHistory(editor.getUsername() + " edited the note");

    }
    private void addHistory(String action){
        editHistory.add(LocalDateTime.now().format(FMT) + " - " + action);
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return category; }
    public User getOwner() { return owner; }
    public List<String> getEditHistory() {
        return new ArrayList<>(editHistory);
    }
    @Override
    public String toString() {
        return "[" + getType() + "] " + title + " (" + category + ")";
    }
}