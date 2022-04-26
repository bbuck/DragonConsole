load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//src/main:__subpackages__"])

kt_jvm_library(
    name = "dragon_console",
    srcs = [
        "CommandProcessor.kt",
        "DragonConsole.kt",
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
