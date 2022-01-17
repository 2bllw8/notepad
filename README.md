# Notepad

[![Notepad CI](https://github.com/2bllw8/notepad/actions/workflows/main.yml/badge.svg)](https://github.com/2bllw8/notepad/actions/workflows/main.yml)
[![Latest release](https://img.shields.io/github/v/release/2bllw8/notepad?color=red&label=download)](https://github.com/2bllw8/notepad/releases/latest)

Notepad is a simple notepad application for Android: it can read, edit and create text files from
the device storage or other apps.

## Features

- Open, edit and create text files
- Cursor and selection information
- Undo actions
- Automatically close brackets and quotes: `' '`, `" "`, `( )`, `[ ]`, `{ }`
- Support different EOL
- Text wrapping
- Commands
  - `/my text`: find next occurrence of _my text_ from the current cursor position
  - `d/my text`: delete all occurrences of _my text_
  - `N d/my text`: delete first N occurrences of _my text_ (write the number instead of N)
  - `s/my text/new text`: substitute all occurrences of _my text_ with _new text_
  - `N s/my text/new text`: substitute first N occurrences of _my text_ with _new text_
      (write the number instead of N)
- Open selected text from other apps as a new text file and save it on the device
- Keyboard shortcuts
  - `ctrl` + `N`: Create a new file
  - `ctrl` + `O`: Open a file
  - `ctrl` + `Q`: Quit
  - `ctrl` + `S`: Save
  - `ctrl` + `Z`: Undo
  - `ctrl` + `+`: Increase text size
  - `ctrl` + `-`: Decrease text size
  - `ctrl` + `/`: Show (or hide) command field

## Download

Download the apk from [latest](https://github.com/2bllw8/notepad/releases/latest) release tag.

## Build

With Gradle:
- `./gradlew :app:assembleRelease`
- `./gradlew :app:assembleDebug`

With AOSP / LineageOS:
- `mka notepad`

## Get help

Open an issue [on github](https://github.com/2bllw8/notepad/issues/)

## License

notepad is licensed under the [GNU General Public License v3 (GPL-3)](http://www.gnu.org/copyleft/gpl.html).
