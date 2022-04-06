package(default_visibility = ["//src/main:__subpackages__"])

java_library(
    name = "dragon_console",
    srcs = [
        "CommandProcessor.java",
        "DragonConsole.java",
    ],
    deps = [
        "//src/main/java/com/eleet/dragonconsole/util:input_controller",
        "//src/main/kotlin/dev/bbuck/dragonconsole/file:file_processor",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:ansi",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:document_styler",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:text_color",
        "//src/main/kotlin/dev/bbuck/dragonconsole/ui:prompt_panel",
    ],
)

java_binary(
    name = "dragon_console_demo",
    main_class = "dev.bbuck.dragonconsole.Demo",
    resources = [
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:ansi",
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
