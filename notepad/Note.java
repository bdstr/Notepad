package notepad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Note {

    private String title;
    private String content;
    private final String directoryPath;

    public Note(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void createNewNote(String directoryPath) throws IOException {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = date.format(formatter);

        String filename = "Note_" + timeStamp;
        File file = new File(directoryPath + filename);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                System.out.println("Error: directory cannot be created!");
            }
        }
        if (file.createNewFile()) {
            System.out.println("Created file in path: " + file.getPath());
        } else {
            System.out.println("Error: file already exists!");
        }
        title = filename;
        content = "";
    }

    public void openNote(String fileName) throws FileNotFoundException {
        File file = new File(directoryPath + fileName);
        StringBuilder note = new StringBuilder();

        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            note.append(scanner.nextLine()).append("\n");
        }
        title = fileName;
        content = note.toString();
    }

    public void saveNote(String title, String content) throws IOException {
        if (!this.title.equals(title)) {
            File file = new File(directoryPath + this.title);
            File destinationFile = new File(directoryPath + title);
            if (!file.renameTo(destinationFile)) {
                throw new IOException("Cannot rename file " + this.title + " to " + title);
            }
        }

        if (!this.content.equals(content)) {
            FileWriter fileWriter = new FileWriter(directoryPath + title);
            fileWriter.write(content);
            fileWriter.close();
        }
    }
}
