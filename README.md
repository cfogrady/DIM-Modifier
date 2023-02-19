# DIM-Modifier

## To Do For 2.0:
* Good Sprite Size enforcement
* Pop Up Notification/confirmation
* Validation
* Verify Specific Fusion working for characters with and without next steps
* Export System (or maybe all) sprites
* Sprite Sheet export/import
* Stats export/import


## About

This software is meant for personal use in the reading and re-writing of DIM image files.

It displays all sprites, stats, evolutions, fusions, and adventures.

The `b` key can also be pressed to display some solid color backgrounds.

The application supports changing sprite sizes, but compatibility with VB can't be guaranteed if sprite dimensions are changed.

Names can be changed by clicking on the name on the Stats page.

## Development
This project relies on the VB-DIM-Reader library found at https://github.com/cfogrady/VB-DIM-Reader

At present, that above project must be build locally and have the `publishToMavenLocal` command run so gradle can access it when building this application.

### Run
To run the project execute execute `./gradlew run` from the project's root directory.
### Debug
To run the project in debug execute `./gradlew -DDEBUG=true run` and connect with a remote debugger.

### Build Image
To build a deployable image run `gradlew.bat buildImageWin` from the project's root directory. Similar commands exist for linux, but haven't been tested yet.

