# Source layout note

This project is intentionally kept close to a minimal Java layout. The source files are currently placed at the repository root because the application is built in the default package and is compiled with a direct `javac *.java` command.

This means the overall structure is intentionally simple and avoids introducing package declarations that would otherwise require a larger refactor.

## Current layout

- Root: Java source files and executable artifacts
- `src/`: reserved for a future package-based refactor if desired
- `dist/`: intended for ready-to-run binaries if the project is reorganized later
- `examples/`: intended for sample input/output files when the project is expanded

## Why the sources remain at the root

The existing source code does not declare a package, and the `Huffman.java` launcher is designed to run as a default-package application. Moving the files without adjusting package declarations would break the current build process.

For this reason, the repository keeps the current structure stable while still documenting the intended organization for future maintenance.
