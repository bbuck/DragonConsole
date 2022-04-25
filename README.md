# Dragon Console

## Preview

![Dragon Console Image 1](http://content.screencast.com/users/izuriel/folders/Jing/media/72276c8d-7987-4622-ac41-c29b8995ffab/2010-11-23_0008.png)

## About

DragonConsole v3.0.0 beta is finally finished and available for forking as it's
been migrated to GitHub. Version 3 is the first to be released as an Open Source
project and is a lightweight and useful Terminal Emulator for any Java
application designed for any Operation System (with a JVM).

## Roadmap

DragonConsole is undergoing some pretty massive changes in it's transition from
"v3" to "v4." The largest of which is the migration from Java to Kotlin (which
shouldn't affect your ability to use it in a Java program).

The current largest changes:

- Source has been ported (directly) to Kotlin, doing the best job to keep the
  exisiting interface intact (hence the current "3.1" version number).
- The import path was changed from `com.eleet.dragonconsole` to
  `dev.bbuck.dragonconsole` since I don't own `com.eleet`.

Upcoming larger changes:

- Ground up rewrite in Kotlin, using Kotlin patterns
- Saner logic (now that I've had 10 years to grows as an engineer since this
  was originally written)
- Implementation of "builders" for the various color codes/input directives
  and other pieces to make integration of this library easier to read and
  understand

## Building

DragonConsole uses [Bazel](https://bazel.build/) to build. If you don't already
have a means to build your project you should consider this build system. If
you want to get the demo up and running simple pull the project down:

```
$ git clone https://github.com/bbuck/dragonconsole 'DragonConsole'
$ cd DragonConsole
```

Ensure you have Bazel installed, and run:

```
$ bazel build //src/main/kotlin/dragonconsole/demo:dragon_console
$ bazel-build/src/main/kotlin/dragonconsole/demo/dragon_console
```

And that should be it.

## DragonConsole v3 Features

- The ability to Color Text that is output to the Console with simple three
  character !DragonConsole Color Codes (DCCC).
- A CommandProcessor to process all input given by the user.
- Two methods of input. Inline, which allows the user to type directly in the
  Console (for a Console look and feel) or Separated input which uses an
  "Input Area" at the bottom of the Console for input.
- FileProcessor for quick and easy reading of a plain text file and converting
  it into a String.
- InputScript which allows the programmer to script input methods directly
  into Output sent to the console. There are four different types of input,
  Ranged, Protected Ranged, Infinite, and Protected Infinite.
- ANSI Color Code support (must be enabled/disabled) with more ANSI Code Support
  on the way!
