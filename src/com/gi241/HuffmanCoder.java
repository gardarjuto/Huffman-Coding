package com.gi241;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

public class HuffmanCoder {

  private static final int ALP_LEN = 65536;

  private HuffmanCoder() {}

  /**
   * Encodes the string using Huffman Codes based on its character frequencies.
   * @param pathIn Path to the input file
   * @param pathOut Path to the output file
   */
  public static void encode(Path pathIn, Path pathOut) throws IOException {
    File inputFile = pathIn.toFile();
    Node root = buildCodeTrie(inputFile);

    String[] codes = new String[ALP_LEN];
    buildCode(codes, root, "");
    StringBuilder bitsToWrite = new StringBuilder();
    writeTrie(bitsToWrite, root);
    bitsToWrite.append(String.format("%32s", Integer.toBinaryString((int)inputFile.length())).replace(' ', '0'));
    BufferedReader br = new BufferedReader(new FileReader(inputFile));
    int charRead;
    while ((charRead = br.read()) != -1) {
      bitsToWrite.append(codes[(char)charRead]);
      if (bitsToWrite.length() > 67108864) {
        Files.write(pathOut, bitStringToByteArray(bitsToWrite.substring(0, (bitsToWrite.length() / 8) * 8)), StandardOpenOption.APPEND);
        bitsToWrite = new StringBuilder(bitsToWrite.substring((bitsToWrite.length() / 8) * 8, bitsToWrite.length()));
      }
    }
    while (bitsToWrite.length() % 8 != 0) bitsToWrite.append('0');
    Files.write(pathOut, bitStringToByteArray(bitsToWrite.toString()), StandardOpenOption.APPEND);
  }

  private static byte[] bitStringToByteArray(String bitString) {
    byte[] byteArray = new byte[bitString.length() / 8];
    for (int i = 0; i < byteArray.length; i++) {
      byte b = 0;
      int n = 7;
      for (int j = 8*i; j < 8*(i+1); j++) {
        if (bitString.charAt(j) == '1') b += (1<<n);
        n--;
      }
      byteArray[i] = b;
    }
    return byteArray;
  }


  public static String decode(byte[] input) {
    StringBuilder binary = byteArrayToBitString(input);
    CharacterIterator it = new StringCharacterIterator(binary.toString());
    Node root = readTrie(it);
    int length = 0;
    for (int i = 31; i >= 0; i--) {
      if (it.current() == '1') {
        length += (1<<i);
      }
      it.next();
    }
    return traverseTrie(root, it, length);
  }

  private static StringBuilder byteArrayToBitString(byte[] input) {
    StringBuilder sb = new StringBuilder();
    for (byte b : input) {
      for (int i = 7; i >= 0; i--) {
        sb.append((b & (1<<i)) >> i);
      }
    }
    return sb;
  }

  private static String traverseTrie(Node root, CharacterIterator it, int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      Node x = root;
      while (!x.isLeaf()) {
        char dir = it.current();
        it.next();
        if (dir == '1') x = x.right;
        else x = x.left;
      }
      sb.append(x.ch);
    }
    return sb.toString();
  }

  public static Node readTrie(CharacterIterator it) {
    if (it.current() == '1') {
      it.next();
      char ch = 0;
      for (int i = 15; i >= 0; i--) {
        if (it.current()=='1') {
          ch += (1<<i);
        }
        it.next();
      }
      return new Node(ch,0,null,null);
    }
    it.next();
    return new Node('\0',0, readTrie(it), readTrie(it));
  }

  public static void writeTrie(StringBuilder sb, Node x) {
    if (x.isLeaf()) {
      sb.append('1');
      sb.append(String.format("%16s", Integer.toBinaryString(x.ch)).replace(' ', '0'));
      return;
    }
    sb.append('0');
    writeTrie(sb, x.left);
    writeTrie(sb, x.right);
  }

  private static void buildCode(String[] codes, Node curr, String s) {
    if (!curr.isLeaf()) {
      buildCode(codes, curr.left, s + '0');
      buildCode(codes, curr.right, s + '1');
    }
    else {
      codes[curr.ch] = s;
    }
  }

  /**
   * Builds the corresponding Huffman Code tree from character frequencies.
   * @param file Input file
   * @return Root of Huffman Code tree
   */
  public static Node buildCodeTrie(File file) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    int readChar;
    Map<Character, Long> frequencies = new HashMap<>();
    while ((readChar = br.read()) != -1) {
      frequencies.put((char)readChar, frequencies.getOrDefault((char)readChar, 0L) + 1);
    }
    br.close();
    PriorityQueue<Node> pq = new PriorityQueue<>();
    for (Character ch: frequencies.keySet()) {
      pq.add(new Node(ch, frequencies.get(ch), null,null));
    }

    while (pq.size() > 1) {
      Node node1 = pq.poll();
      Node node2 = pq.poll();
      Node parent = new Node('\0', node1.freq+node2.freq, node1, node2);
      pq.add(parent);
    }
    return pq.poll();
  }


  private static class Node implements Comparable<Node> {
    private final char ch;
    private final long freq;
    private final Node left, right;

    Node(char ch, long freq, Node left, Node right) {
      this.ch    = ch;
      this.freq  = freq;
      this.left  = left;
      this.right = right;
    }

    public boolean isLeaf() {
      return (left == null) && (right == null);
    }

    @Override
    public int compareTo(Node node) {
      return (int)(this.freq - node.freq);
    }
  }


}
