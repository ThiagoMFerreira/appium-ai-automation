# Appium AI Automation

This project is a mobile test automation framework designed to be operated by **AI Agents**. It utilizes a set of specialized "Skills" that allow the AI to manage the entire Appium test lifecycle with minimal human intervention.

## 🚀 The Goal

The core idea is to simplify automation:
1. **Provide the APK**: The user simply points to the application they want to test.
2. **Describe the Test**: The user specifies in natural language which scenarios they want to automate.
3. **Execution & Correction**: The AI creates the code (Page Objects and Tests), executes them, and if anything fails, analyzes the error and applies the fix automatically.

## 🧠 Agent Skills

The project is guided by directives contained in the `.skills/` folder:

- **[Appium Setup](.skills/appium-setup/SKILL.md)**: Procedures for environment validation (Java, Node, Appium, ADB) and initial `APPIUM_CONTEXT.md` generation.
- **[Appium Developer](.skills/appium-developer/SKILL.md)**: Instructions for generating robust Kotlin code following Page Object and Clean Code patterns.
- **[Appium Inspector](.skills/appium-inspector/SKILL.md)**: Ability to analyze the UI hierarchy and extract the best selectors (ID, Accessibility ID, etc.).
- **[Appium Runner](.skills/appium-runner/SKILL.md)**: Protocol for safe test execution, failure analysis via screenshots, and logs.
- **[Appium Guideline](.skills/appium-guideline/SKILL.md)**: General rules for navigation, state validation, and best practices.

## 📂 Project Structure

- `.skills/`: Definitions of the AI agent's capabilities.
- `appium-tests/`: Main Kotlin project containing:
    - `src/test/kotlin/pages/`: Automated Page Objects.
    - `src/test/kotlin/tests/`: JUnit5 test suites.
    - `src/test/kotlin/base/`: Driver configurations and Hooks.
- `APPIUM_CONTEXT.md`: Maintains the current state of automation (which screens and flows have already been mapped).

## 🛠️ How It Works

1. **Setup & Context**: The Agent uses the `appium-setup` skill to validate the environment and reads `APPIUM_CONTEXT.md` to understand the app's entry points.
2. **Generation**: When a new test is requested, the AI uses the `appium-developer` skill to create the `.kt` files.
3. **Execution**: The AI uses the `appium-runner` skill to trigger Gradle and monitor the status.
4. **Self-Healing**: If a test fails, the AI enters debugging mode, reads the failure screenshot, identifies whether the issue is in the selector or the page logic, and fixes the code in real-time.

## 📋 Prerequisites

- **Appium Server** (v2.0+)
- **Android SDK** & Configured Emulator
- **Java/Kotlin** & Gradle
- **AI Agent** configured with access to read/write tools and command execution.

---
*This repository is an example of how AI can raise the bar for software quality through autonomous automation.*
