# Complexity Analysis

Let:

- `n` = number of input Java `char` values.
- `k` = number of distinct Java `char` values, where `k ≤ min(n, 65,536)`.
- `b` = number of encoded bits produced.

| Operation | Time complexity | Reason |
| --- | --- | --- |
| Count frequencies | `O(n)` expected | Visit each input character once using a hash map |
| Build Huffman tree | `O(k log k)` | Each priority-queue insertion/removal costs logarithmic time |
| Generate codes | `O(k)` | Visit each node in the tree once |
| Encode text | `O(n + b)` | Visit characters and append their code bits |
| Decode bit stream | `O(b)` | Follow one tree edge per bit |
| Store frequency table | `O(k)` | One entry per distinct character |

## Auxiliary space

- Frequency map and code map: `O(k)`.
- Huffman tree: `O(k)` nodes.
- Encoded output: `O(b)` bits (the text-mode implementation stores it as a Java string).
- Decoded output: `O(n)`.

The file compressor writes bits incrementally through a byte writer, rather than first building one full encoded-bit string.

## Practical note

The asymptotic complexity does not guarantee that every input becomes smaller. The archive includes a header and a frequency table, so short text or text with a nearly uniform character distribution may have little benefit or may grow in size.
