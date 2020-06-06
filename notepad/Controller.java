package notepad;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Controller {
    private final String directoryPath = ".saved_notes/";

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    private ListView<String> notesList;

    @FXML
    private Button createButton;

    @FXML
    private TextArea noteTextarea;

    @FXML
    public TextField noteTitle;

    @FXML
    public Button saveTitleButton;

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
    public void openNote(MouseEvent mouseEvent) {
        String title = notesList.getSelectionModel().getSelectedItem();
        if (!selectedNote.equals(title)) {
            if (title != null) {
                try {
                    note.openNote(title);
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
            note.createNewNote(directoryPath);
            listAllNotes();
            notesList.getSelectionModel().select(note.getTitle());
            displayNote(note);

        } catch (IOException e) {
            showError("Error while opening note", "Cannot create note file!", e);
        }
        listAllNotes();
    }

    @FXML
    private void saveNote() {
        String title = noteTitle.getText();
        String content = noteTextarea.getText();

        try {
            note.saveNote(title, content);
            note.openNote(title);
            listAllNotes();
            notesList.getSelectionModel().select(title);
        } catch (IOException e) {
            showError("Error while saving note", "Cannot save note file!", e);
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
        saveTitleButton.setDisable(false);
        noteTitle.setEditable(true);
        noteTextarea.setEditable(true);
    }

    private void blockUserInput() {
        saveTitleButton.setDisable(true);
        noteTitle.setText(null);
        noteTitle.setEditable(false);
        noteTextarea.setText(null);
        noteTextarea.setEditable(false);
    }

}
