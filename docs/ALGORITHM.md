# Algorithm and File Format

## Huffman tree construction

The program first builds a frequency map containing each Java `char` and its occurrence count. Each character becomes a leaf node in a priority queue.

The queue is ordered by:
1. Lower frequency first.
2. Lower minimum character value as a deterministic tie-breaker.

The program repeatedly removes the two lowest-priority nodes and combines them into a parent node. The process ends when one node remains: the root of the Huffman tree.

## Generating codes

Starting at the root:
- Moving left appends `0`.
- Moving right appends `1`.

The code for a character is the bit sequence on the path from the root to its leaf. A single-character input is assigned code `0`.

Because every character is stored at a leaf, no character's code is a prefix of another character's code.

## Encoding and decoding

Encoding replaces every input character with its Huffman bit sequence.

Decoding starts at the tree root and follows each bit until a leaf is reached. The leaf character is appended to the output, and traversal restarts at the root.

## Current .huf file layout

The file compressor writes these fields in order:

| Field | Description |
| --- | --- |
| Magic number | Identifies this application's Huffman file format |
| Original character count | Number of Java characters in the source text |
| Unique character count | Number of distinct characters in the source text |
| Frequency table | Each character and its frequency |
| Encoded bit stream | Huffman bits packed into bytes, most-significant bit first |

The frequency table is enough to rebuild the same Huffman tree during decompression. The final byte is padded with zero bits when the encoded stream does not end on a byte boundary; the original character count tells the decoder when to stop.

## Implementation notes and limitations

- Text files are read and written as UTF-8.
- The implementation builds codes from Java `char` values (UTF-16 code units).
- Empty input files are rejected by the current file-compression flow.
- The current archive format is a project-specific format, not a standard compression format.
- The text-mode size reduction is an estimate based on 16 bits per Java character and excludes the code table.
