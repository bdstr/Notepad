package notepad;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Controller {
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
    private void initialize() {
        notesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                openSelectedNote(newValue);
            } catch (FileNotFoundException e) {
                System.out.println("Error: cannot read note file at path \"" + ".saved_notes/" + newValue + "\"");
            }
        });
        listAllNotes();
    }

    @FXML
    void createNoteButtonClick(ActionEvent event) {
        createNote();
        listAllNotes();
    }

    private void listAllNotes() {
        String[] filesInDirectory = getFilesInDirectory(".saved_notes/");

        if (filesInDirectory != null) {
            notesList.getItems().setAll(filesInDirectory);
        }
    }

    private String[] getFilesInDirectory(String path) {
        File file = new File(path);
        return file.list();
    }

    private void createNote() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_H-m-s");
        String timeStamp = date.format(formatter);

        String filename = "Note_" + timeStamp;
        File file = new File(".saved_notes/" + filename);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                System.out.println("Error: directory cannot be created!");
            }
        }
        try {
            if (file.createNewFile()) {
                System.out.println("Created file in path: " + file.getPath());
            } else {
                System.out.println("Error: file already exists!");
            }
        } catch (IOException e) {
            System.out.println("Error: cannot create file!");
        }
    }

    private void openSelectedNote(String name) throws FileNotFoundException {
        File file = new File(".saved_notes/" + name);
        StringBuilder note = new StringBuilder();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            note.append(scanner.nextLine()).append("\n");
        }
        noteTitle.setText(name);
        noteTextarea.setText(note.toString());
    }

    private void saveNote() {

    }
}
