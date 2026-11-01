# Contributing

Thank you for considering a contribution to Huffman Text Compressor.

## Before you start

- Check existing issues and pull requests to avoid duplicate work.
- Keep changes focused and explain the problem they solve.
- Do not commit generated `.class` files, build output, or temporary `.huf` files.

## Development setup

Use JDK 11 or newer.

Compile from the repository root:

```bash
javac -encoding UTF-8 -d out src/HuffmanCompressor/HuffmanCompressor.java
```

Run:

```bash
java -cp out HuffmanCompressor.HuffmanCompressor
```

## Pull request checklist

- [ ] The project compiles successfully.
- [ ] Existing menu options still work.
- [ ] Compression and decompression preserve the original text.
- [ ] New behavior and limitations are documented.
- [ ] The pull request description explains the change and how it was checked.

For larger changes, open an issue first to discuss the proposed approach.
