package(default_visibility = ["//src/main:__subpackages__"])

java_library(
    name = "dragon_console",
    srcs = [
        "CommandProcessor.java",
        "DragonConsole.java",
    ],
    deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole/file:file_processor",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:ansi",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:document_styler",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:text_color",
        "//src/main/kotlin/dev/bbuck/dragonconsole/ui:input_controller",
        "//src/main/kotlin/dev/bbuck/dragonconsole/ui:prompt_panel",
    ],
)
