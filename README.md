# Huffman Text Compressor

A menu-driven Java application that demonstrates **lossless text compression** using the Huffman Coding algorithm. It can encode text, compress UTF-8 text files into a custom `.huf` format, and restore compressed files.

## Features

- Compress text entered in the console.
- Compress a text file into a binary `.huf` file.
- Decompress a `.huf` file back into a UTF-8 text file.
- Display character frequencies and generated Huffman codes.
- Show encoded size and estimated size reduction for text input.
- Store the frequency table in the compressed file so it can be decoded later.

## Requirements

- Java Development Kit (JDK) **11 or newer**
- A terminal or Java IDE (IntelliJ IDEA, Eclipse, or VS Code with Java support)

## Getting started

### 1. Clone the repository

```bash
git clone https://github.com/anuragchaurasiya/HUFFMAN-TEXT-COMPRESSOR.git
cd HUFFMAN-TEXT-COMPRESSOR
```

### 2. Compile

From the repository root:

```bash
javac -encoding UTF-8 -d out src/HuffmanCompressor/HuffmanCompressor.java
```

### 3. Run

```bash
java -cp out HuffmanCompressor.HuffmanCompressor
```

## How to use

When the program starts, choose an option from the menu:

| Option | Action |
| --- | --- |
| 1 | Compress text entered in the console |
| 2 | Compress a text file into a `.huf` file |
| 3 | Decompress a `.huf` file into a text file |
| 4 | Display Huffman codes for entered text |
| 5 | Exit |

For file operations, enter the input and output paths when prompted. Use different paths for the input and output files.

> **Note:** Empty text and empty files are not supported by the current implementation. The text-mode size comparison is an estimate and does not include the code table.

## How Huffman Coding works

1. Count how often each character appears.
2. Insert the characters into a priority queue ordered by frequency.
3. Repeatedly combine the two lowest-frequency nodes to build a binary Huffman tree.
4. Assign `0` to left edges and `1` to right edges.
5. Encode each character using its generated bit sequence.
6. Decode by walking the same tree according to the stored bits.

Huffman codes are prefix-free, which allows the encoded stream to be decoded unambiguously.

See [docs/ALGORITHM.md](docs/ALGORITHM.md) for more detail about the implementation and file format.

## Project structure

```text
HUFFMAN-TEXT-COMPRESSOR/
├── .github/
│   └── workflows/
│       └── java-ci.yml
├── docs/
│   └── ALGORITHM.md
├── src/
│   └── HuffmanCompressor/
│       └── HuffmanCompressor.java
├── .gitignore
├── CONTRIBUTING.md
└── README.md
```

## Build verification

A GitHub Actions workflow compiles the Java source on pushes and pull requests using supported JDK versions. It checks that the project builds; it does not replace a full automated test suite.

## Contributing

Suggestions and improvements are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

## License

No license has been added yet. Until a license is selected, the repository's code is not granted an open-source reuse license.

---

**Maintainer:** [@anuragchaurasiya](https://github.com/anuragchaurasiya)
