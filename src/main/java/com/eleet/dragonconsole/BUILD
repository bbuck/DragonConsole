package(default_visibility = ["//src/main:__subpackages__"])

java_library(
    name = "dragon_console",
    srcs = [
        "CommandProcessor.java",
        "DragonConsole.java",
    ],
    deps = [
        "//src/main/java/com/eleet/dragonconsole/util:ansi",
        "//src/main/java/com/eleet/dragonconsole/util:input_controller",
        "//src/main/java/com/eleet/dragonconsole/util:prompt_panel",
        "//src/main/kotlin/dev/bbuck/dragonconsole/file:file_processor",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:document_styler",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:text_color",
    ],
)

java_library(
    name = "demo_processor",
    srcs = ["DemoProcessor.java"],
    deps = [
        ":dragon_console",
        "//src/main/kotlin/dev/bbuck/dragonconsole/file:file_processor",
    ],
)

java_library(
    name = "dragon_console_frame",
    srcs = ["DragonConsoleFrame.java"],
    deps = [":dragon_console"],
)

java_binary(
    name = "dragon_console_demo",
    main_class = "dev.bbuck.dragonconsole.Demo",
    resources = [
        "//src/main/resources/com/eleet/dragonconsole:resources/ansi",
        "//src/main/resources/com/eleet/dragonconsole:resources/colors",
        "//src/main/resources/com/eleet/dragonconsole:resources/input",
        "//src/main/resources/com/eleet/dragonconsole:resources/l_console",
        "//src/main/resources/com/eleet/dragonconsole:resources/l_font",
        "//src/main/resources/com/eleet/dragonconsole:resources/logo_b",
        "//src/main/resources/com/eleet/dragonconsole:resources/logo_w",
    ],
    runtime_deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole:demo",
    ],
)
