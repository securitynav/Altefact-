import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

# Try to suppress the warning by overriding the dependency resolution
# or just inform the user we can't. Let's see if upgrading AGP to 8.5.0 fixes it.
