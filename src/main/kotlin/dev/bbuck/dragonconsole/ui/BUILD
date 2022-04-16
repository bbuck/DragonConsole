load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//src/main:__subpackages__"])

kt_jvm_library(
    name = "prompt_label",
    srcs = ["PromptLabel.kt"],
    visibility = ["//visibility:private"],
    deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:text_color",
    ],
)

kt_jvm_library(
    name = "prompt_panel",
    srcs = ["PromptPanel.kt"],
    deps = [
        ":prompt_label",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:text_color",
    ],
)

kt_jvm_library(
    name = "input_controller",
    srcs = ["InputController.kt"],
    deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:input_string",
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:stored_input",
    ],
)
