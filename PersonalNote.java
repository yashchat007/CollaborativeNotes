public class PersonalNote extends Note{
    public PersonalNote(String title, String content, String category, User owner){
        super(title, content, category, owner);
    }
    @Override 
    public boolean canEdit(User user){
        return getOwner().getUsername().equals(user.getUsername());
    }
    @Override
    public String getType() {
        return "Personal";
    }
}