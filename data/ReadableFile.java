package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadableFile {
    public File file;
    public Scanner scanner;

    public ReadableFile(File file) throws FileNotFoundException {
        this.file = file;
        scanner = new Scanner(file);
    }
}
