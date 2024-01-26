package data;

import java.io.File;

public class WritableFile {
    public File file;
    public StringBuilder content;

    public WritableFile(File file) {
        this.file = file;

        content = new StringBuilder();
    }
}
