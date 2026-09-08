# Take It [Fixed] — Fork

A fixed and extended unofficial fork of Take It for Minecraft Forge 1.20.1.

«You can now customize the keybind used for item pickup.»

## About

This is an unofficial fork of Take It, created primarily for use in modpacks.

Most of this mod was originally written by cutexxgirl, and all credit for the original work belongs to the original author.

The original project is licensed under the MIT License, and this fork remains under the same license.

This fork is not affiliated with or endorsed by the original author. It is provided as-is, and support for this fork is not guaranteed.

For the original project, visit:

Original Take It:
https://github.com/cutexxgirl/Take-It

---

## Changes from Take It

🎮 Custom Item Pickup Keybind

Added a dedicated keybind for item pickup.

The keybind can be changed from:

Minecraft → Options → Controls

You are no longer restricted to the original keybind.

📦 Hold-to-Pickup

Hold the item pickup key to continuously attempt to pick up nearby items.

🎯 Single-Item Pickup

Added a separate keybind for picking up a specific item you're looking at.

🚫 Inventory Full Message

When an item cannot be picked up because there is not enough inventory space, the player can receive:

Inventory is Full

This message can be enabled or disabled through the mod configuration.

🔄 Item Synchronization

Improved client/server item synchronization to help prevent visual item-stack desynchronization after pickup attempts.

🛠️ Stability Improvements

Various fixes and improvements to the pickup logic, including fixes for item duplication issues encountered during development.

---

Requirements

- Minecraft: 1.20.1
- Mod Loader: Minecraft Forge
- Java: Java 17

---

Installation

1. Download the latest ".jar" file from the Releases section.
2. Make sure you have Minecraft Forge 1.20.1 installed.
3. Place the downloaded ".jar" file into your Minecraft "mods" folder.
4. Launch Minecraft.

This fork is intended to be compatible with modpacks using Minecraft Forge 1.20.1.

---

Configuration

The mod configuration is located in:

config/takeit-common.toml

Available Options

enableMod = true

enableFullMessage = true

radiusX = 2

radiusY = 2

radiusZ = 2

"enableMod"

Enables or disables the mod.

enableMod = true

"enableFullMessage"

Controls whether the Inventory is Full message is displayed.

enableFullMessage = true

Set it to "false" to disable the message.

Pickup Radius

The pickup radius can be configured independently along the X, Y, and Z axes.

radiusX = 2

radiusY = 2

radiusZ = 2

---

Building from Source

This project uses the standard Minecraft Forge development environment.

Requirements

- Java 17
- Git
- A working internet connection for downloading Gradle and Minecraft/Forge dependencies

Build

Clone the repository:

git clone https://github.com/AminAA2009/Take-It-Fixed-Fork.git

Enter the project directory:

cd Take-It-Fixed-Fork

Build the mod:

Windows

gradlew.bat build

Linux / macOS

./gradlew build

After a successful build, the compiled ".jar" file will be located in:

build/libs/

---

Development Environment

You can use any IDE or editor you prefer.

For example:

- Visual Studio Code
- IntelliJ IDEA
- Eclipse

You do not need IntelliJ IDEA specifically to build the project.

For IntelliJ run configurations:

gradlew.bat genIntellijRuns

For Eclipse:

gradlew.bat genEclipseRuns

---

Credits

Original Project

Take It by cutexxgirl

Original repository:

https://github.com/cutexxgirl/Take-It

Most of the original code and functionality comes from the original project.

All credit for the original work belongs to the original author.

This Fork

This fork contains additional features, fixes, and modifications made independently from the original project.

---

License

This project is licensed under the MIT License, in accordance with the original Take It project.

The original copyright and license notices are retained.

See the ""LICENSE"" (LICENSE) file for the full license text.

---

Disclaimer

Take It [Fixed] — Fork is an unofficial community modification.

It is not affiliated with, sponsored by, or endorsed by the original author.

This project is provided as-is. No guarantee of support, compatibility, or continued maintenance is provided.

If you are looking for the original version of Take It, please use the original project linked above.
