load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//src/main:__subpackages__"])

kt_jvm_library(
    name = "demo",
    srcs = ["Demo.kt"],
    deps = [
        "//src/main/java/com/eleet/dragonconsole:demo_processor",
        "//src/main/java/com/eleet/dragonconsole:dragon_console_frame",
    ],
)
