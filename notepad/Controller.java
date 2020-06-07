package notepad;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.crypto.AEADBadTagException;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class Controller {
    private final String directoryPath = ".saved_notes/";

    @FXML
    private ListView<String> notesList;

    @FXML
    private Button createButton;

    @FXML
    private TextField noteTitle;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextArea noteTextarea;

    @FXML
    private Label creationStatsLabel;

    @FXML
    private Label wordsStatsLabel;

    @FXML
    private Label charactersStatsLabel;

    @FXML
    private Label paragraphsStatsLabel;

    @FXML
    private Button encryptButton;


    private Note note;
    private String selectedNote;

    @FXML
    private void initialize() {
        noteTextarea.textProperty().addListener((observableValue, s, s2) -> refreshStats());
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
                    if (EncryptedText.isTextEncrypted(note.getContent())) {
                        String password = passwordPrompt();

                        String decryptedText = EncryptedText.decrypt(note.getContent(), password);
                        note.setContent(decryptedText);
                        note.setEncrypted(password);
                        encryptButton.setText("Change password");
                    } else {
                        encryptButton.setText("Encrypt note");
                    }
                    selectedNote = title;
                    displayNote(note);
                    refreshStats();
                    creationStatsLabel.setText(note.getCreationDate());
                } catch (NullPointerException e) {
                    selectedNote = "";
                    notesList.getSelectionModel().select(null);
                    creationStatsLabel.setText("");
                    blockUserInput();
                } catch (AEADBadTagException e) {
                    showError("Error while opening note", new Exception("Wrong password"));
                } catch (Exception e) {
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
        } catch (Exception e) {
            showError("Error while creating note", e);
            listAllNotes();
            blockUserInput();
        }
    }

    @FXML
    private void saveNote() {
        String title = noteTitle.getText();
        String content = noteTextarea.getText();

        try {
            if (note.isEncrypted()) {
                content = EncryptedText.encrypt(content, note.getPassword());
            }
            note.save(title, content);
            note.open(title);
            listAllNotes();
            notesList.getSelectionModel().select(title);
        } catch (Exception e) {
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
        try {
            String password = passwordPrompt();
            note.setEncrypted(password);
            saveNote();
        } catch (Exception e) {
            showError("Error while encrypting note", e);
        }
    }

    @FXML
    public void refreshStats() {
        TextStats stats = new TextStats(noteTextarea.getText());
        wordsStatsLabel.setText(String.valueOf(stats.getWordNumber()));
        charactersStatsLabel.setText(String.valueOf(stats.getCharacterNumber()));
        paragraphsStatsLabel.setText(String.valueOf(stats.getParagraphsNumber()));
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

    public String passwordPrompt() throws NullPointerException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Password prompt");
        dialog.setHeaderText("Enter password");
        dialog.setContentText("Please, enter password:");
        Optional<String> result = dialog.showAndWait();
        String password;

        if (result.isPresent()) {
            password = result.get();
        } else {
            throw new NullPointerException("Password not provided");
        }
        return password;
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
        refreshStats();
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
