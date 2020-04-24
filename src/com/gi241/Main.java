package com.gi241;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {

    private static boolean validateArguments(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: huffman.java [options] <source file> <output file>");
            System.out.println("Options:\n        [-e] Encode\n        [-d] Decode");
            return false;
        }
        String option = args[0];
        File file_in = new File(args[1]);
        if (!option.equals("-e") && !option.equals("-d")) {
            System.out.println("Unknown option");
            return false;
        }
        if (!file_in.exists()) {
            System.out.println("Input file does not exist");
            return false;
        }
        if (!file_in.canRead()) {
            System.out.println("File is not readable");
            return false;
        }
        if (file_in.length() == 0) {
            System.out.println("Input file is empty");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        if (!validateArguments(args)) return;
        String option = args[0];
        Path pathOfInputFile = Paths.get(args[1]);
        Path pathOfOutputFile = Paths.get(args[2]);

        if (option.equals("-e")) {
            try {
                if (pathOfOutputFile.toFile().exists())
                    Files.delete(pathOfOutputFile);
                Files.createFile(pathOfOutputFile);
                optionEncode(pathOfInputFile, pathOfOutputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String decodedString = "";
            try {
                byte[] fileContent = Files.readAllBytes(pathOfInputFile);
                decodedString = HuffmanCoder.decode(fileContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Files.write(pathOfOutputFile, decodedString.getBytes());
                System.out.println("Decoded file written to " + pathOfOutputFile.toAbsolutePath().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void optionEncode(Path pathOfInputFile, Path pathOfOutputFile) throws IOException {
        HuffmanCoder.encode(pathOfInputFile, pathOfOutputFile);
        System.out.println("Encoded file written to " + pathOfOutputFile.toAbsolutePath().toString());
        System.out.println("Resulting file is " + 100.0 * pathOfOutputFile.toFile().length() / pathOfInputFile.toFile().length() + "% of the original size");
    }
}

// BufferedReader(new FileReader(file))
// while ((st = br.read()) != -1) {
//      contents.append((char)st);
// }
// Files.write(path, string.getBytes())
// FileOutputStream(path.toString())
// stream.write(bytes)