package com.graemsheppard.clang;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: clang <filename>");
            return;
        }

        String filename = args[0];
        String input = null;
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            input = sb.toString();


        } catch (Exception e) {
            var test = e;
        } finally {
             System.out.println("Closing file: " + filename);
             br.close();
        }
        var tokenizer = new Tokenizer(input);
        var tokens = tokenizer.tokenize();
        var parser = new Parser(tokens);
        var tree = parser.parse();
        var generator = new Generator(tree);
        String asm = generator.generate();
        Writer fileWriter = new FileWriter("src/main/resources/main.asm", false);
        fileWriter.write(asm);
        fileWriter.close();
    }
}