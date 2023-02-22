# DIM-Modifier

## About

This software is meant for personal use in the reading and re-writing of DIM image files.

It displays all sprites, stats, evolutions, fusions, and adventures.

## Usage
When this application first loads, it requires the user to direct it to the BE firmware provided by Bandai. This firmware can be found at https://toy.bandai.co.jp/special/vbbedl/pc_download/

The firmware is used for the display of attack sprites and attribute symbols.

The application is broken into three sections: Characters, Battles, and System

### Characters Section
This section contains the stats, sprites, and various transformations for each character.

Sprites in this section can be replaced by either clicking a sprite, by dragging and dropping an image file, or by importing a sprite sheet.

### Battles Section
This section contains the NFC Battle Pools and Adventure Missions

### System Section
This contains various system sprites like backgrounds, custom attacks, attributes, icon, etc.

Sprites in this section can be replaced by clicking a sprite or by dragging and dropping an image file.

## Development
This project relies on the VB-DIM-Reader library found at https://github.com/cfogrady/VB-DIM-Reader

At present, that above project must be build locally and have the `publishToMavenLocal` command run so gradle can access it when building this application.

### Run
To run the project execute execute `./gradlew run` from the project's root directory.
### Debug
To run the project in debug execute `./gradlew -DDEBUG=true run` and connect with a remote debugger.

### Build Image
To build a deployable image run `gradlew.bat buildImageWin` from the project's root directory. Similar commands exist for linux, but haven't been tested yet.
