package com.dreebit.i18nextResourceGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by stefanmeschke on 14.01.14.
 */
public class FileContentProvider {


    public String getFileContent(File file) throws IOException {
        String fileContent = readFile(file);
        return fileContent;
    }


    private String readFile(File file) throws IOException {
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner((Readable) new BufferedReader(new FileReader(file)));
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
