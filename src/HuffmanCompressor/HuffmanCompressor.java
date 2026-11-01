// Concepts: Huffman greedy coding, binary trees, PriorityQueue, HashMap, recursion, bit I/O, and UTF-8 file handling.

package HuffmanCompressor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class HuffmanCompressor {

    static final int MAGIC = 0x48554631;
    static Map<Character, String> codes = new HashMap<>();

    static class Node {
        char ch;
        int frequency;
        char minChar;
        Node left, right;

        Node(char ch, int frequency) {
            this.ch = ch;
            this.frequency = frequency;
            this.minChar = ch;
        }

        Node(Node left, Node right) {
            this.ch = '\0';
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
            this.minChar = (char) Math.min(
                left.minChar, right.minChar
            );
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    static Map<Character, Integer> getFrequencies(String text) {
        Map<Character, Integer> frequency = new HashMap<>();

        for (char ch : text.toCharArray()) {
            frequency.put(
                ch, frequency.getOrDefault(ch, 0) + 1
            );
        }

        return frequency;
    }

    static Node buildTree(Map<Character, Integer> frequency) {
        PriorityQueue<Node> pq = new PriorityQueue<>(
            Comparator.comparingInt((Node n) -> n.frequency)
                .thenComparingInt(n -> n.minChar)
        );

        for (Map.Entry<Character, Integer> entry
                : frequency.entrySet()) {
            pq.offer(new Node(
                entry.getKey(), entry.getValue()
            ));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.offer(new Node(left, right));
        }

        return pq.poll();
    }

    static void generateCodes(Node root, String code) {
        if (root == null) {
            return;
        }

        if (root.isLeaf()) {
            codes.put(
                root.ch, code.isEmpty() ? "0" : code
            );
            return;
        }

        generateCodes(root.left, code + "0");
        generateCodes(root.right, code + "1");
    }

    static String encode(String text) {
        StringBuilder result = new StringBuilder();

        for (char ch : text.toCharArray()) {
            result.append(codes.get(ch));
        }

        return result.toString();
    }

    static String decode(String bits, Node root, int length) {
        if (root == null) {
            return "";
        }

        if (root.isLeaf()) {
            char[] result = new char[length];
            Arrays.fill(result, root.ch);
            return new String(result);
        }

        StringBuilder result = new StringBuilder();
        Node current = root;

        for (char bit : bits.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else if (bit == '1') {
                current = current.right;
            } else {
                throw new IllegalArgumentException(
                    "Only 0 and 1 are allowed."
                );
            }

            if (current == null) {
                throw new IllegalArgumentException(
                    "Invalid encoded text."
                );
            }

            if (current.isLeaf()) {
                result.append(current.ch);
                current = root;
            }
        }

        if (current != root || result.length() != length) {
            throw new IllegalArgumentException(
                "Incomplete or invalid encoded text."
            );
        }

        return result.toString();
    }

    static String displayChar(char ch) {
        if (ch == ' ') return "[space]";
        if (ch == '\n') return "[newline]";
        if (ch == '\t') return "[tab]";
        return "'" + ch + "'";
    }

    static void showFrequencies(
            Map<Character, Integer> frequency) {
        System.out.println("\nCharacter Frequencies:");

        for (Map.Entry<Character, Integer> entry
                : frequency.entrySet()) {
            System.out.println(
                displayChar(entry.getKey()) + " = "
                + entry.getValue()
            );
        }
    }

    static void showCodes() {
        System.out.println("\nHuffman Codes:");

        for (Map.Entry<Character, String> entry
                : codes.entrySet()) {
            System.out.println(
                displayChar(entry.getKey()) + " = "
                + entry.getValue()
            );
        }
    }

    static void compressText(Scanner sc) {
        System.out.print("Enter text: ");
        String text = sc.nextLine();

        if (text.isEmpty()) {
            System.out.println("Text cannot be empty.");
            return;
        }

        Map<Character, Integer> frequency =
            getFrequencies(text);

        Node root = buildTree(frequency);
        codes.clear();
        generateCodes(root, "");

        showFrequencies(frequency);
        showCodes();

        String encoded = encode(text);
        String decoded = decode(encoded, root, text.length());

        System.out.println("\nOriginal Text: " + text);
        System.out.println("Encoded Bits: " + encoded);
        System.out.println("Decoded Text: " + decoded);

        System.out.println(
            "Decoding Successful: " + text.equals(decoded)
        );

        int originalBits = text.length() * 16;
        int encodedBits = encoded.length();

        System.out.println(
            "Original UTF-16 Size: " + originalBits + " bits"
        );
        System.out.println(
            "Encoded Size: " + encodedBits + " bits"
        );

        double saved = 100.0
            * (originalBits - encodedBits) / originalBits;

        System.out.printf(
            "Estimated Size Reduction: %.2f%%%n", saved
        );

        System.out.println(
            "Note: Encoded Bits do not include the code table."
        );
    }

    static class BitWriter {
        DataOutputStream out;
        int currentByte = 0;
        int bitCount = 0;

        BitWriter(DataOutputStream out) {
            this.out = out;
        }

        void writeBit(int bit) throws IOException {
            currentByte = (currentByte << 1) | bit;
            bitCount++;

            if (bitCount == 8) {
                out.writeByte(currentByte);
                currentByte = 0;
                bitCount = 0;
            }
        }

        void finish() throws IOException {
            if (bitCount > 0) {
                currentByte <<= (8 - bitCount);
                out.writeByte(currentByte);
                currentByte = 0;
                bitCount = 0;
            }
        }
    }

    static class BitReader {
        DataInputStream in;
        int currentByte;
        int bitsLeft;

        BitReader(DataInputStream in) {
            this.in = in;
        }

        int readBit() throws IOException {
            if (bitsLeft == 0) {
                currentByte = in.read();
                if (currentByte == -1) {
                    throw new EOFException(
                        "Compressed file ended unexpectedly."
                    );
                }
                bitsLeft = 8;
            }

            int bit = (currentByte >> (bitsLeft - 1)) & 1;
            bitsLeft--;
            return bit;
        }
    }

    static void compressFile(Scanner sc) throws IOException {
        System.out.print("Enter input text file path: ");
        Path input = Path.of(sc.nextLine().trim());

        if (!Files.isRegularFile(input)) {
            System.out.println("Input file not found.");
            return;
        }

        String text = Files.readString(
            input, StandardCharsets.UTF_8
        );

        if (text.isEmpty()) {
            System.out.println(
                "Empty files are not supported in this version."
            );
            return;
        }

        Map<Character, Integer> frequency =
            getFrequencies(text);

        Node root = buildTree(frequency);
        codes.clear();
        generateCodes(root, "");

        System.out.print("Enter output .huf file path: ");
        Path output = Path.of(sc.nextLine().trim());

        if (input.toAbsolutePath().normalize()
                .equals(output.toAbsolutePath().normalize())) {
            System.out.println(
                "Input and output paths must be different."
            );
            return;
        }

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                    Files.newOutputStream(output)))) {

            out.writeInt(MAGIC);
            out.writeInt(text.length());
            out.writeInt(frequency.size());

            for (Map.Entry<Character, Integer> entry
                    : frequency.entrySet()) {
                out.writeChar(entry.getKey());
                out.writeInt(entry.getValue());
            }

            BitWriter writer = new BitWriter(out);

            for (char ch : text.toCharArray()) {
                String code = codes.get(ch);

                for (char bit : code.toCharArray()) {
                    writer.writeBit(bit == '1' ? 1 : 0);
                }
            }

            writer.finish();
        }

        long originalSize = Files.size(input);
        long compressedSize = Files.size(output);

        System.out.println("\nFile compressed successfully!");
        System.out.println("Compressed file: " + output);
        System.out.println("Original size: " + originalSize + " bytes");
        System.out.println("Compressed size: " + compressedSize + " bytes");

        double saved = 100.0
            * (originalSize - compressedSize) / originalSize;

        System.out.printf("Space saved: %.2f%%%n", saved);
        System.out.println(
            "The .huf file includes the decoding information."
        );
    }

    static void decompressFile(Scanner sc) throws IOException {
        System.out.print("Enter .huf file path: ");
        Path input = Path.of(sc.nextLine().trim());

        if (!Files.isRegularFile(input)) {
            System.out.println("Compressed file not found.");
            return;
        }

        System.out.print("Enter output text file path: ");
        Path output = Path.of(sc.nextLine().trim());

        if (input.toAbsolutePath().normalize()
                .equals(output.toAbsolutePath().normalize())) {
            System.out.println(
                "Input and output paths must be different."
            );
            return;
        }

        String decoded;

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                    Files.newInputStream(input)))) {

            if (in.readInt() != MAGIC) {
                throw new IOException(
                    "Invalid Huffman file format."
                );
            }

            int length = in.readInt();
            int unique = in.readInt();

            if (length < 1 || unique < 1
                    || unique > 65536 || unique > length) {
                throw new IOException(
                    "Invalid file metadata."
                );
            }

            Map<Character, Integer> frequency =
                new HashMap<>();

            long total = 0;

            for (int i = 0; i < unique; i++) {
                char ch = in.readChar();
                int count = in.readInt();

                if (count < 1 || frequency.containsKey(ch)) {
                    throw new IOException(
                        "Invalid frequency table."
                    );
                }

                frequency.put(ch, count);
                total += count;
            }

            if (total != length) {
                throw new IOException(
                    "Frequency table does not match text length."
                );
            }

            Node root = buildTree(frequency);
            StringBuilder result = new StringBuilder(length);

            if (root.isLeaf()) {
                for (int i = 0; i < length; i++) {
                    result.append(root.ch);
                }
            } else {
                BitReader reader = new BitReader(in);

                for (int i = 0; i < length; i++) {
                    Node current = root;

                    while (!current.isLeaf()) {
                        int bit = reader.readBit();
                        current = bit == 0
                            ? current.left : current.right;

                        if (current == null) {
                            throw new IOException(
                                "Invalid Huffman data."
                            );
                        }
                    }

                    result.append(current.ch);
                }
            }

            decoded = result.toString();
        }

        Files.writeString(
            output, decoded, StandardCharsets.UTF_8
        );

        System.out.println("\nFile decompressed successfully!");
        System.out.println("Restored file: " + output);
        System.out.println("Restored characters: " + decoded.length());
        System.out.println("Compressed file size: "
            + Files.size(input) + " bytes");
        System.out.println("Restored file size: "
            + Files.size(output) + " bytes");
    }

    static void displayCodesForText(Scanner sc) {
        System.out.print("Enter text: ");
        String text = sc.nextLine();

        if (text.isEmpty()) {
            System.out.println("Text cannot be empty.");
            return;
        }

        Map<Character, Integer> frequency =
            getFrequencies(text);

        Node root = buildTree(frequency);
        codes.clear();
        generateCodes(root, "");

        showFrequencies(frequency);
        showCodes();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println(
                "\n========== HUFFMAN COMPRESSOR =========="
            );
            System.out.println("1. Compress Text");
            System.out.println("2. Compress File");
            System.out.println("3. Decompress File");
            System.out.println("4. Show Huffman Codes");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        compressText(sc);
                        break;

                    case "2":
                        compressFile(sc);
                        break;

                    case "3":
                        decompressFile(sc);
                        break;

                    case "4":
                        displayCodesForText(sc);
                        break;

                    case "5":
                        System.out.println("Thank you!");
                        sc.close();
                        return;

                    default:
                        System.out.println(
                            "Invalid choice. Enter 1 to 5."
                        );
                }
            } catch (Exception e) {
                System.out.println(
                    "Error: " + e.getMessage()
                );
            }
        }
    }
}