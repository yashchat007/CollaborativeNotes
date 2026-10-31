# Collaborative Notes App

A Java desktop application for creating, organising, searching and sharing notes between users. Built as an Object-Oriented Programming mini project using Java and Swing.

## Features

- **Login and switch user:** simulate multiple people using the same app
- **Personal notes:** only the owner can edit them
- **Shared notes:** the owner can add collaborators who can also edit
- **Categories:** organise notes and filter by category
- **Search:** find notes by keyword in the title or content, optionally within a category
- **Edit history:** every note records who created and edited it, with timestamps
- **Permission checks:** unauthorised edits and deletes are blocked with a custom exception
- **File storage:** notes are saved to `notes.txt` and loaded again on startup

## OOP Concepts Used

### 1. Encapsulation
- All fields in every class are `private` and accessed only through getters.
- A note's content can only be changed through `edit()`, which checks permission first.
- `getEditHistory()` and `getCollaborators()` return copies, so outside code cannot modify the originals.

### 2. Inheritance
- `PersonalNote` and `SharedNote` extend the `Note` class and reuse its fields and methods.
- `PermissionDeniedException` extends Java's `Exception` class.
- `NotesAppGUI` extends Swing's `JFrame`.

### 3. Abstraction
- `Note` is an `abstract` class and cannot be instantiated directly.
- It declares the abstract methods `canEdit(User)` and `getType()`, which each subclass must implement.

### 4. Polymorphism
- **Method overriding (runtime):** `canEdit()` and `getType()` behave differently in `PersonalNote` and `SharedNote`; `toString()` is overridden in `User` and `Note`.
- **Method overloading (compile time):** `NoteManager` has `search(String keyword)` and `search(String keyword, String category)`.
- A single `List<Note>` holds both personal and shared notes.

## Other Java Concepts
- Collections (`ArrayList`, `List`)
- Exception handling with a custom checked exception
- File handling (`BufferedReader`, `BufferedWriter`, try-with-resources)
- Date-Time API (`LocalDateTime`, `DateTimeFormatter`)
- Swing GUI

## Project Structure

| File | Purpose |
|------|---------|
| `User.java` | Represents a user of the app |
| `PermissionDeniedException.java` | Custom exception for unauthorised actions |
| `Note.java` | Abstract base class for all notes |
| `PersonalNote.java` | Note editable only by its owner |
| `SharedNote.java` | Note editable by its owner and collaborators |
| `NoteManager.java` | Stores, searches, deletes, saves and loads notes |
| `NotesAppGUI.java` | Swing user interface |
| `Main.java` | Entry point that loads notes and starts the GUI |

## How to Run

Requires Java (JDK 8 or above).

```bash
javac *.java
java Main
```

## Author
Yash Chaturvedi