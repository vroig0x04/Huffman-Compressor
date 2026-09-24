# Distribution artifacts

The project already exposes user-facing executables at the repository root:

- `WinHuff_jar.jar`
- `WinHuff_x86_x64.exe`

This keeps the binaries directly available to end users without requiring a build step. A future refactor can move packaged builds under a dedicated `dist/` directory, but the current project structure intentionally keeps the executables easy to access.
