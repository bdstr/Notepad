package notepad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Note {

    private final String directoryPath;
    private String title;
    private String content;
    private String creationDate;
    private boolean isEncrypted;
    private String password;

    public Note(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(String password) {
        isEncrypted = true;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }


    public void createNew(String directoryPath) throws Exception {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = date.format(formatter);

        String filename = "Note_" + timeStamp;
        File file = new File(directoryPath + filename);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Exception("Directory cannot be created");
            }
        }
        if (!file.createNewFile()) {
            throw new Exception("Cannot create file - already exists");
        }

        title = filename;
        content = "";
        isEncrypted = false;
        password = null;
    }

    public void open(String fileName) throws IOException {
        File file = new File(directoryPath + fileName);
        StringBuilder note = new StringBuilder();

        BasicFileAttributes creationTimeUnix = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        LocalDateTime creationDate = LocalDateTime.ofInstant(creationTimeUnix.creationTime().toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate = creationDate.format(formatter);

        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            note.append(scanner.nextLine()).append("\n");
        }
        title = fileName;
        content = note.toString();
    }

    public void save(String title, String content) throws IOException {
        if (title == null) {
            throw new IOException("First you need to open a note");
        }

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

    public void delete() throws Exception {
        File file = new File(directoryPath + title);
        if (!file.delete()) {
            throw new Exception("Cannot delete file on path " + directoryPath + title);
        }
    }
}
