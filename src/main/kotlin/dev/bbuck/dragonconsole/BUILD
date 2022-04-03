load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//src/main:__subpackages__"])

kt_jvm_library(
    name = "dragon_console_frame",
    srcs = ["DragonConsoleFrame.kt"],
    deps = [
        "//src/main/java/com/eleet/dragonconsole:dragon_console",
    ],
)

kt_jvm_library(
    name = "demo",
    srcs = ["Demo.kt"],
    deps = [
        ":dragon_console_frame",
        "//src/main/java/com/eleet/dragonconsole:demo_processor",
    ],
)
