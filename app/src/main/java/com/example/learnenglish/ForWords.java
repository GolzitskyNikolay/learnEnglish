package com.example.learnenglish;

import java.io.*;
import java.util.*;

class ForWords {

    void createSeparate() {
        File input = new File("C:\\Users\\1\\AndroidStudioProjects\\learnEnglish" +
                "\\app\\src\\main\\res\\raw\\all_words.txt");
        File output = new File("C:\\Users\\1\\AndroidStudioProjects\\learnEnglish" +
                "\\app\\src\\main\\res\\raw\\dictionary.txt");

        try {
            FileReader fileReader = new FileReader(input);
            FileWriter fileWriter = new FileWriter(output);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String line = bufferedReader.readLine();
            while (line != null) {
                if (!line.equals("")) {
                    StringBuilder newLine = new StringBuilder();
                    List<String> list = new ArrayList<>(
                            Arrays.asList(line.split("\\s+|\\t+|\\n")));
                    list.remove("");
                    boolean hasSeparator = false;
                    for (int i = 1; i <= list.size(); i++) {
                        String partOfString = list.get(i - 1);
                        if (partOfString.matches("[A-Z]?-*([a-z]-*)*")) {
                            if (i != 1) {
                                newLine.append(" ");
                            }
                            newLine.append(partOfString);
                        } else {
                            if (!hasSeparator) {
                                newLine.append("<>");
                                hasSeparator = true;
                            } else {
                                newLine.append(" ");
                            }
                            newLine.append(partOfString);
                        }
                    }
                    newLine.append("\n");
                    bufferedWriter.write(newLine.toString());
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
