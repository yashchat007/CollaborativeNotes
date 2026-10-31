import java.util.ArrayList;
import java.util.List;
public class SharedNote extends Note{
    private List<User> collaborators;

    public SharedNote(String title, String content, String category, User owner){
        super(title,content,category,owner);
        this.collaborators = new ArrayList<>();
    }
    public void addCollaborator(User user){
        collaborators.add(user);
    }
    public List<User> getCollaborators() {
        return new ArrayList<>(collaborators);
    }
    @Override
    public boolean canEdit(User user) {
        if (getOwner().getUsername().equals(user.getUsername())) {
            return true;
        }
        for (User u : collaborators) {
            if (u.getUsername().equals(user.getUsername())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public String getType() {
        return "Shared";
    }
}