package com.dreebit.i18nextResourceGenerator;

import java.io.*;
import java.util.Scanner;

/**
 * Created by stefanmeschke on 14.01.14.
 */
public class FileReadWriteProvider {


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

    public void writeFileWithContent(File file, String content) throws IOException {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
