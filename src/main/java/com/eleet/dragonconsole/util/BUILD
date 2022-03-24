package(default_visibility = ["//src/main/java/com/eleet/dragonconsole:__subpackages__"])

java_library(
    name = "text_color",
    srcs = ["TextColor.java"],
)

java_library(
    name = "ansi",
    srcs = ["ANSI.java"],
    deps = [":text_color"],
)

java_library(
    name = "debug",
    srcs = ["Debug.java"],
)

java_library(
    name = "document_styler",
    srcs = ["DocumentStyler.java"],
    deps = [":text_color"],
)

java_library(
    name = "input_string",
    srcs = ["InputString.java"],
    deps = [":debug"],
)

java_library(
    name = "input_controller",
    srcs = ["InputController.java"],
    deps = [":input_string"],
)

java_library(
    name = "prompt_panel",
    srcs = ["PromptPanel.java"],
    deps = [":text_color"],
)
