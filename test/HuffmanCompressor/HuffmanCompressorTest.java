// Concepts: regression testing, assertions, edge-case checks, and encode/decode round-trip verification.
package HuffmanCompressor;

import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight regression checks for the Huffman coding implementation.
 * Run with: java -cp out HuffmanCompressor.HuffmanCompressorTest
 */
public class HuffmanCompressorTest {

    private static int checks = 0;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void testFrequencyCounting() {
        Map<Character, Integer> expected = new HashMap<>();
        expected.put('b', 1);
        expected.put('a', 3);

        check(HuffmanCompressor.getFrequencies("aaab").equals(expected),
                "Frequency counting should count every character.");
    }

    private static void testEncodeDecodeRoundTrip() {
        String original = "Huffman coding is fun!";
        Map<Character, Integer> frequency =
                HuffmanCompressor.getFrequencies(original);

        HuffmanCompressor.Node root =
                HuffmanCompressor.buildTree(frequency);

        HuffmanCompressor.codes.clear();
        HuffmanCompressor.generateCodes(root, "");

        String bits = HuffmanCompressor.encode(original);
        String restored =
                HuffmanCompressor.decode(bits, root, original.length());

        check(original.equals(restored),
                "Decoded text should match the original text.");
    }

    private static void testSingleCharacterInput() {
        String original = "zzzzz";
        HuffmanCompressor.Node root =
                HuffmanCompressor.buildTree(
                        HuffmanCompressor.getFrequencies(original));

        HuffmanCompressor.codes.clear();
        HuffmanCompressor.generateCodes(root, "");

        check("0".equals(HuffmanCompressor.codes.get('z')),
                "A single distinct character should receive code 0.");
        check(original.equals(
                HuffmanCompressor.decode("00000", root, original.length())),
                "Single-character input should decode correctly.");
    }

    private static void testInvalidEncodedBit() {
        String original = "ab";
        HuffmanCompressor.Node root =
                HuffmanCompressor.buildTree(
                        HuffmanCompressor.getFrequencies(original));

        boolean rejected = false;
        try {
            HuffmanCompressor.decode("02", root, 1);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }

        check(rejected, "Encoded input containing a non-binary digit must be rejected.");
    }

    public static void main(String[] args) {
        testFrequencyCounting();
        testEncodeDecodeRoundTrip();
        testSingleCharacterInput();
        testInvalidEncodedBit();

        System.out.println("All " + checks + " Huffman checks passed.");
    }
}
