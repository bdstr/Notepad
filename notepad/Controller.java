package notepad;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.crypto.AEADBadTagException;
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
    public Button encryptButton;

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
                    if (EncryptedText.isTextEncrypted(note.getContent())) {


                        EncryptedText encryptedText = new EncryptedText(note.getContent());
                        encryptedText.decrypt(password);
                    }
                    displayNote(note);
                } catch (FileNotFoundException e) {
                    showError("Error while opening note", e);
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
            showError("Error while opening note", e);
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
            showError("Error while saving note", e);
        }
    }

    @FXML
    public void deleteNote() {
        try {
            note.delete();
            listAllNotes();
            blockUserInput();
        } catch (Exception e) {
            showError("Error while deleting note", e);
        }
    }

    @FXML
    public void encryptNote() {
        EncryptedText encryptedNote;
        try {
            encryptedNote = new EncryptedText(note.getContent(), "123");
            System.out.println(encryptedNote.getEncryptedText());
        } catch (Exception e) {
            showError("Error while encrypting note", e);
        }



/*
        try {
            System.out.println(encryptedNote.decrypt("122"));
            System.out.println(encryptedNote.decrypt("123"));
        } catch (AEADBadTagException e) {
            showError("Error while decrypting note", new Exception("Password doesn't match!"));
        } catch (Exception e) {
            showError("Error while decrypting note", e);
        }
        */
    }

    private void showError(String header, Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage() + "\n");

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
        encryptButton.setDisable(true);
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
        encryptButton.setDisable(false);
        noteTitle.setEditable(true);
        noteTitle.setDisable(false);
        noteTextarea.setEditable(true);
        noteTextarea.setDisable(false);
    }
}
