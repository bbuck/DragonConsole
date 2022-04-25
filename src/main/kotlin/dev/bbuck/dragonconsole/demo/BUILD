load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

package(default_visibility = ["//src/main:__subpackages__"])

kt_jvm_library(
    name = "dragon_console_frame",
    srcs = ["DragonConsoleFrame.kt"],
    deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole:dragon_console",
    ],
)

kt_jvm_library(
    name = "demo_processor",
    srcs = ["DemoProcessor.kt"],
    deps = [
        "//src/main/kotlin/dev/bbuck/dragonconsole:dragon_console",
        "//src/main/kotlin/dev/bbuck/dragonconsole/file:file_processor",
    ],
)

kt_jvm_library(
    name = "demo",
    srcs = ["Demo.kt"],
    deps = [
        ":demo_processor",
        ":dragon_console_frame",
    ],
)

java_binary(
    name = "dragon_console_demo",
    main_class = "dev.bbuck.dragonconsole.demo.Demo",
    resources = [
        "//src/main/kotlin/dev/bbuck/dragonconsole/text:ansi",
        "//src/main/resources/com/eleet/dragonconsole:resources/ansi",
        "//src/main/resources/com/eleet/dragonconsole:resources/colors",
        "//src/main/resources/com/eleet/dragonconsole:resources/input",
        "//src/main/resources/com/eleet/dragonconsole:resources/l_console",
        "//src/main/resources/com/eleet/dragonconsole:resources/l_font",
        "//src/main/resources/com/eleet/dragonconsole:resources/logo_b",
        "//src/main/resources/com/eleet/dragonconsole:resources/logo_w",
    ],
    runtime_deps = [
        ":demo",
    ],
)
