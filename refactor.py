import os
import shutil

src_dir = r"c:\work\New folder (2)\upstox-auth-pro-java\src"

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    content = content.replace("package com.upstox.auth", "package io.github.growthquantix.upstoxauth")
    content = content.replace("import com.upstox.auth", "import io.github.growthquantix.upstoxauth")
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

# Move files
for root, dirs, files in os.walk(src_dir):
    for f in files:
        if f.endswith(".java"):
            old_path = os.path.join(root, f)
            new_root = root.replace(r"com\upstox\auth", r"io\github\growthquantix\upstoxauth")
            os.makedirs(new_root, exist_ok=True)
            new_path = os.path.join(new_root, f)
            shutil.move(old_path, new_path)
            replace_in_file(new_path)

# Cleanup old empty dirs
for root_path in [r"c:\work\New folder (2)\upstox-auth-pro-java\src\main\java\com", r"c:\work\New folder (2)\upstox-auth-pro-java\src\test\java\com"]:
    if os.path.exists(root_path):
        shutil.rmtree(root_path)

print("Refactor complete.")
