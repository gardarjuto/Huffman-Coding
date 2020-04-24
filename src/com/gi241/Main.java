package com.gi241;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: huffman.java <options> <source file> <output file>");
            System.out.println("Options:\n        [-e] Encode\n        [-d] Decode");
            return;
        }
        String option = args[0];
        if (!option.equals("-e") && !option.equals("-d")) {
            System.out.println("Unknown option");
            return;
        }
        File originalFile = new File(args[1]);
        Path pathOfNewFile = Paths.get(args[2]);
        if (!originalFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (!originalFile.canRead()) {
            System.out.println("File is not readable");
            return;
        }
        String fileContents = "";
        if (option.equals("-e")) {
            StringBuilder contents = new StringBuilder();
            String st;
            try {
                BufferedReader br = new BufferedReader(new FileReader(originalFile));
                while ((st = br.readLine()) != null) {
                    contents.append(st);
                    contents.append('\n');
                }
                fileContents = contents.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileContents.isEmpty()) {
                System.out.println("Empty input file");
                return;
            }
            byte[] encodedBytes = HuffmanCoder.encode(fileContents);
            try (FileOutputStream stream = new FileOutputStream(pathOfNewFile.toString())){
                stream.write(encodedBytes);
                System.out.println("Encoded file written to " + pathOfNewFile.toAbsolutePath().toString());
                System.out.println("Resulting file is " + 100.0 * encodedBytes.length / originalFile.length() + "% of the original size");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String decodedString = "";
            try {
                byte[] fileContent = Files.readAllBytes(originalFile.toPath());
                decodedString = HuffmanCoder.decode(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Files.writeString(pathOfNewFile, decodedString, StandardCharsets.UTF_16);
                System.out.println("Decoded file written to " + pathOfNewFile.toAbsolutePath().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
