package com.example.learnenglish;

import java.io.*;
import java.util.*;

class ForWords {

    void createSeparate() {
        File input = new File("C:\\Users\\1\\AndroidStudioProjects\\learnEnglish" +
                "\\app\\src\\main\\res\\raw\\to_sort_it.txt");
        File output = new File("C:\\Users\\1\\AndroidStudioProjects\\learnEnglish" +
                "\\app\\src\\main\\res\\raw\\dictionary2.txt");

        try {
            FileReader fileReader = new FileReader(input);
            FileWriter fileWriter = new FileWriter(output);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String line = bufferedReader.readLine();
            int t = 1;
            while (line != null) {

                List<String> eng_rus = new ArrayList<>(Arrays.asList(line.split("#")));

                System.out.println(t);
                StringBuilder stringBuilder = new StringBuilder();
                List<String> left = new ArrayList<>
                        (Arrays.asList(eng_rus.get(0).split(" |[(.+) *]")));
                left.remove("");
                for (int i = 0; i < left.size(); i++) {
                    stringBuilder.append(left.get(i));
                    if (i < left.size() - 1){
                        stringBuilder.append(" ");
                    }
                }
                stringBuilder.append("#");
                List<String> right = new ArrayList<>
                        (Arrays.asList(eng_rus.get(1).split(" |[(.+) *]")));
                left.remove("");
                for (int i = 0; i < right.size(); i++) {
                    stringBuilder.append(right.get(i));
                    if (i < right.size() - 1){
                        stringBuilder.append(" ");
                    }
                }
                if (stringBuilder.toString().toLowerCase().matches("[a-z]+#.*")) {
                    bufferedWriter.write(stringBuilder + "\n");
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
