# Code Quality Workflow for AI

## Purpose

Use this document as the source of truth when checking Java code quality in this repository. Run the checks in the order below, fix issues at the source, and report the commands and results accurately.

## Scope and assumptions

- Repository: `P2PCoursesMicroservice`
- Initial target module: `p2p-common`
- Shell: Git Bash on Windows
- Existing report generators:
  - `config/checkstyles/generate-checkstyle-report.py`
  - `config/spotbugs/generate-spotbugs-report.py`
- Existing check scripts:
  - `config/scripts/checkstyle-report.sh`
  - `config/scripts/spotbug-check.sh`
- Do not assume Maven plugin goals, configuration files, or report paths exist beyond what is verified in the repository. Inspect `pom.xml`, parent POMs, and scripts before changing commands.
- Run commands from the repository root unless the project configuration requires otherwise.

## Required order

### 1. Spotless — formatting

First inspect the configured Spotless Maven plugin and its available goals.

1. Check formatting without changing files, using the project's configured Spotless check goal.
2. If formatting fails, run the configured Spotless apply goal.
3. Review the resulting diff; do not overwrite unrelated user changes.
4. Re-run the Spotless check and confirm it passes.

Typical Maven goals, only if configured in this repository:

```bash
mvn -pl p2p-common spotless:check
mvn -pl p2p-common spotless:apply
```

### 2. Checkstyle — coding conventions

After formatting is clean:

1. Run Checkstyle using the repository's configured rules and generate its HTML report with the existing script.
2. If it fails, inspect the report and fix source code issues without weakening or deleting rules merely to make the build pass.
3. Re-run the script and verify the final result.

Run from the repository root in Git Bash:

```bash
bash config/scripts/checkstyle-report.sh
```

The script runs the configured Maven Checkstyle goal and generates
`p2p-common/target/checkstyle-report.html` from the XML report.

### 3. SpotBugs — potential bugs and security findings

After Checkstyle is clean:

1. Run SpotBugs using the repository's configured plugin and analysis settings and generate its HTML report with the existing script.
2. Confirm that the analysis and report generation completed successfully.
3. Review findings by priority and category. Fix genuine defects; do not suppress findings without understanding and documenting why they are false positives or acceptable.
4. Re-run the script after fixes.

Run from the repository root in Git Bash:

```bash
bash config/scripts/spotbug-check.sh
```

The script runs the configured SpotBugs Maven goal and generates
`p2p-common/target/spotbugs-report.html` from the XML report.

Open the generated reports from the repository root using:

```bash
start p2p-common/target/checkstyle-report.html
start p2p-common/target/spotbugs-report.html
```

## Execution policy

- Execute one stage at a time; stop and diagnose a failed command before proceeding.
- Prefer existing Maven profiles, plugin versions, rule files, and report scripts. Do not invent alternate configuration.
- Do not modify build configuration, disable rules, or add suppressions just to obtain a green result.
- Make the smallest safe code changes that address the findings.
- Preserve unrelated working-tree changes. Before applying automated formatting, inspect `git status --short` and review the diff afterwards.
- Do not claim a check passed unless its command completed successfully and its exit status was verified.
- If a command or report path differs from this guide, inspect the POM and script, then update the command based on evidence.
- Do not commit, push, open a pull request, or merge unless explicitly asked.

## Final verification

After fixing issues, run all checks again in the same order:

1. Spotless check
2. Checkstyle check and HTML report generation
3. SpotBugs analysis and HTML report generation

If the project has a configured test or verification lifecycle that is appropriate for the change, run it after the static checks.

## Required final summary

Report:

- Commands executed, in order
- Pass/fail status for each tool
- Number and severity of remaining findings, when available
- HTML report paths that were actually generated
- Files changed and a concise explanation of the fixes
- Any skipped step and the reason

Never report a successful result based only on the existence of an old report file.
