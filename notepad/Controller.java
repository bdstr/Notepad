package notepad;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Controller {
    private final String directoryPath = ".saved_notes/";

    @FXML
    private ListView<String> notesList;

    @FXML
    private Button createButton;

    @FXML
    public Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextArea noteTextarea;

    @FXML
    public TextField noteTitle;


    private Note note;
    private String selectedNote;

    @FXML
    private void initialize() {
        note = new Note(directoryPath);
        selectedNote = "";
        blockUserInput();
        listAllNotes();
    }

    @FXML
    public void openNote() {
        String title = notesList.getSelectionModel().getSelectedItem();
        if (!selectedNote.equals(title)) {
            if (title != null) {
                try {
                    note.open(title);
                    selectedNote = title;
                    displayNote(note);
                } catch (FileNotFoundException e) {
                    showError("Error while opening note", "Cannot read note file!", e);
                }
            } else {
                blockUserInput();
            }
        }
    }

    @FXML
    void createNote() {
        try {
            note.createNew(directoryPath);
            String title = note.getTitle();
            listAllNotes();
            notesList.getSelectionModel().select(title);
            selectedNote = title;
            displayNote(note);
        } catch (IOException e) {
            showError("Error while opening note", "Cannot create note file!", e);
            listAllNotes();
            blockUserInput();
        }
    }

    @FXML
    private void saveNote() {
        String title = noteTitle.getText();
        String content = noteTextarea.getText();

        try {
            note.save(title, content);
            note.open(title);
            listAllNotes();
            notesList.getSelectionModel().select(title);
        } catch (IOException e) {
            showError("Error while saving note", "Cannot save note file!", e);
        }
    }

    @FXML
    public void deleteNote() {
        try {
            note.delete();
            listAllNotes();
            blockUserInput();
        } catch (Exception e) {
            showError("Error while deleting note", "Cannot delete note file!", e);
        }
    }

    private void showError(String header, String content, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content + "\n\n" + e.getMessage() + "\n\n");

        blockUserInput();
        listAllNotes();

        alert.showAndWait();
    }

    private void listAllNotes() {
        String[] filesInDirectory = getFilesInDirectory();
        if (filesInDirectory != null) {
            Arrays.sort(filesInDirectory);
            notesList.getItems().setAll(filesInDirectory);
        }
    }

    private String[] getFilesInDirectory() {
        File file = new File(directoryPath);
        return file.list();
    }

    private void displayNote(Note note) {
        String title = note.getTitle();
        String content = note.getContent();

        noteTitle.setText(title);
        noteTextarea.setText(content);
        allowUserInput();
    }

    private void blockUserInput() {
        saveButton.setDisable(true);
        deleteButton.setDisable(true);
        noteTitle.setText(null);
        noteTitle.setEditable(false);
        noteTitle.setDisable(true);
        noteTextarea.setText(null);
        noteTextarea.setEditable(false);
        noteTextarea.setDisable(true);
    }

    private void allowUserInput() {
        saveButton.setDisable(false);
        deleteButton.setDisable(false);
        noteTitle.setEditable(true);
        noteTitle.setDisable(false);
        noteTextarea.setEditable(true);
        noteTextarea.setDisable(false);
    }

}
