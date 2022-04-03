package(default_visibility = ["//src/main:__subpackages__"])

java_library(
    name = "input_string",
    srcs = ["InputString.java"],
    deps = ["//src/main/kotlin/dev/bbuck/dragonconsole/log:debug"],
)

java_library(
    name = "input_controller",
    srcs = ["InputController.java"],
    deps = [":input_string"],
)

java_library(
    name = "prompt_panel",
    srcs = ["PromptPanel.java"],
    deps = ["//src/main/kotlin/dev/bbuck/dragonconsole/text:text_color"],
)
