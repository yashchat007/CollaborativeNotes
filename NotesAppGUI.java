import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class NotesAppGUI extends JFrame {
    private NoteManager manager;
    private User currentUser;

    private DefaultListModel<Note> listModel = new DefaultListModel<>();
    private JList<Note> noteList = new JList<>(listModel);
    private JTextField searchField = new JTextField(12);
    private JComboBox<String> categoryFilter = new JComboBox<>();
    private JTextField titleField = new JTextField(20);
    private JTextField categoryField = new JTextField(20);
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{"Personal", "Shared"});
    private JTextArea contentArea = new JTextArea(12, 30);
    private JLabel infoLabel = new JLabel(" ");

    public NotesAppGUI(NoteManager manager) {
        this.manager = manager;
        login();

        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // ----- Left side: search + list of notes -----
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(categoryFilter);
        searchPanel.add(searchBtn);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(noteList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(380, 0));

        // ----- Right side: note details -----
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeBox);

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(formPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        rightPanel.add(infoLabel, BorderLayout.SOUTH);

        // ----- Bottom: buttons -----
        JButton newBtn = new JButton("New");
        JButton addBtn = new JButton("Add Note");
        JButton editBtn = new JButton("Save Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton shareBtn = new JButton("Share");
        JButton historyBtn = new JButton("History");
        JButton switchBtn = new JButton("Switch User");
        JButton saveBtn = new JButton("Save to File");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(newBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(shareBtn);
        buttonPanel.add(historyBtn);
        buttonPanel.add(switchBtn);
        buttonPanel.add(saveBtn);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ----- Button actions -----
        searchBtn.addActionListener(e -> doSearch());
        newBtn.addActionListener(e -> clearForm());
        addBtn.addActionListener(e -> addNote());
        editBtn.addActionListener(e -> editNote());
        deleteBtn.addActionListener(e -> deleteNote());
        shareBtn.addActionListener(e -> shareNote());
        historyBtn.addActionListener(e -> showHistory());
        switchBtn.addActionListener(e -> { login(); refreshList(); });
        saveBtn.addActionListener(e -> saveNotes());
        noteList.addListSelectionListener(e -> showSelectedNote());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveNotes();
                System.exit(0);
            }
        });

        refreshList();
    }

    private void login() {
        String name = null;
        while (name == null || name.trim().isEmpty()) {
            name = JOptionPane.showInputDialog(this, "Enter your username:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (name == null) {
                System.exit(0);
            }
        }
        currentUser = new User(name.trim());
        setTitle("Collaborative Notes - logged in as " + currentUser);
    }

    private void refreshList() {
        listModel.clear();
        for (Note n : manager.getAllNotes()) {
            listModel.addElement(n);
        }
        categoryFilter.removeAllItems();
        categoryFilter.addItem("All");
        for (String c : manager.getCategories()) {
            categoryFilter.addItem(c);
        }
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        String category = (String) categoryFilter.getSelectedItem();
        List<Note> results;
        if (category == null || category.equals("All")) {
            results = manager.search(keyword);
        } else {
            results = manager.search(keyword, category);
        }
        listModel.clear();
        for (Note n : results) {
            listModel.addElement(n);
        }
    }

    private void clearForm() {
        noteList.clearSelection();
        titleField.setText("");
        categoryField.setText("");
        contentArea.setText("");
        typeBox.setEnabled(true);
        infoLabel.setText(" ");
    }

    private void showSelectedNote() {
        Note n = noteList.getSelectedValue();
        if (n == null) {
            return;
        }
        titleField.setText(n.getTitle());
        categoryField.setText(n.getCategory());
        contentArea.setText(n.getContent());
        typeBox.setSelectedItem(n.getType());
        typeBox.setEnabled(false);

        String info = "Owner: " + n.getOwner();
        if (n instanceof SharedNote) {
            info += "   |   Shared with: " + ((SharedNote) n).getCollaborators();
        }
        info += "   |   You can edit: " + (n.canEdit(currentUser) ? "Yes" : "No");
        infoLabel.setText(info);
    }

    private void addNote() {
        String title = titleField.getText().trim();
        String category = categoryField.getText().trim();
        String content = contentArea.getText();
        if (title.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and category cannot be empty.");
            return;
        }
        if (title.contains("|") || category.contains("|")) {
            JOptionPane.showMessageDialog(this, "Please don't use the | character in title or category.");
            return;
        }
        Note note;
        if (typeBox.getSelectedItem().equals("Shared")) {
            note = new SharedNote(title, content, category, currentUser);
        } else {
            note = new PersonalNote(title, content, category, currentUser);
        }
        manager.addNote(note);
        refreshList();
        clearForm();
        JOptionPane.showMessageDialog(this, "Note added.");
    }

    private void editNote() {
        Note n = noteList.getSelectedValue();
        if (n == null) {
            JOptionPane.showMessageDialog(this, "Select a note first.");
            return;
        }
        try {
            n.edit(contentArea.getText(), currentUser);
            JOptionPane.showMessageDialog(this, "Note updated.");
        } catch (PermissionDeniedException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Permission Denied", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNote() {
        Note n = noteList.getSelectedValue();
        if (n == null) {
            JOptionPane.showMessageDialog(this, "Select a note first.");
            return;
        }
        try {
            manager.deleteNote(n, currentUser);
            refreshList();
            clearForm();
        } catch (PermissionDeniedException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Permission Denied", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void shareNote() {
        Note n = noteList.getSelectedValue();
        if (!(n instanceof SharedNote)) {
            JOptionPane.showMessageDialog(this, "Select a Shared note to share it.");
            return;
        }
        if (!n.getOwner().getUsername().equals(currentUser.getUsername())) {
            JOptionPane.showMessageDialog(this, "Only the owner can share this note.");
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Share with (username):");
        if (name != null && !name.trim().isEmpty()) {
            ((SharedNote) n).addCollaborator(new User(name.trim()));
            showSelectedNote();
            JOptionPane.showMessageDialog(this, "Shared with " + name.trim());
        }
    }

    private void showHistory() {
        Note n = noteList.getSelectedValue();
        if (n == null) {
            JOptionPane.showMessageDialog(this, "Select a note first.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String h : n.getEditHistory()) {
            sb.append(h).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Edit History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveNotes() {
        try {
            manager.saveToFile();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save notes: " + ex.getMessage());
        }
    }
}