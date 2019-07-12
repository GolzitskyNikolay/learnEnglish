package com.example.learnenglish;

import java.io.*;
import java.util.*;

class ForWords {

    void createSeparate() {
        File input = new File("C:\\Users\\1\\AndroidStudioProjects\\learnEnglish" +
                "\\app\\src\\main\\res\\raw\\s.txt");
        File output = new File("C:\\Users\\1\\AndroidStudioProjects\\learnEnglish" +
                "\\app\\src\\main\\res\\raw\\dictionary.txt");

        try {
            FileReader fileReader = new FileReader(input);
            FileWriter fileWriter = new FileWriter(output);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String line = bufferedReader.readLine();
            int t = 1;
            while (line != null) {

                System.out.println(t);
                StringBuilder stringBuilder = new StringBuilder();
                List<String> eng_rus =
                        new ArrayList<>(Arrays.asList(line.split("[\\t]")));

                eng_rus.remove(0);
                eng_rus.remove(0);
                eng_rus.remove(0);
                stringBuilder.append(eng_rus.get(0));
                eng_rus.remove(0);
                eng_rus.remove(0);
                stringBuilder.append("#");

                List<String> right =
                        new ArrayList<>(Arrays.asList(eng_rus.get(0).split(", |; ")));


                for (int i = 0; i < right.size(); i++) {
                    StringBuilder stringBuilder1 = new StringBuilder(stringBuilder);
                    stringBuilder1.append(right.get(i));
                    stringBuilder1.append("\n");
                    if (stringBuilder1.toString().toLowerCase().split("\\)").length == 1) {
                        bufferedWriter.write(stringBuilder1.toString().toLowerCase());
                    }
                }
                line = bufferedReader.readLine();
                t++;
            }
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
