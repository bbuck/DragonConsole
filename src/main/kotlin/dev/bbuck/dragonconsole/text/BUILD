load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//src/main:__subpackages__"])

kt_jvm_library(
    name = "text_color",
    srcs = ["TextColor.kt"],
)

kt_jvm_library(
    name = "document_styler",
    srcs = ["DocumentStyler.kt"],
    deps = [":text_color"],
)

kt_jvm_library(
    name = "ansi",
    srcs = ["Ansi.kt"],
    deps = [":text_color"],
)

kt_jvm_library(
    name = "input_string",
    srcs = ["InputString.kt"],
    deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole/log:debug",
    ],
)

kt_jvm_library(
    name = "stored_input",
    srcs = ["StoredInput.kt"],
    deps = [":input_string"],
)
