"""Merge all systemdesign/*.md parts into one PDF (Markdown -> HTML -> Edge/Chrome headless)."""
import glob
import os
import subprocess
import sys

import markdown

HERE = os.path.dirname(os.path.abspath(__file__))
PARTS = os.path.join(HERE, "systemdesign")
HTML = os.path.join(PARTS, "System_Design_Encyclopedia.html")
PDF = os.path.join(HERE, "System_Design_Encyclopedia.pdf")

BROWSERS = [
    r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Microsoft\Edge\Application\msedge.exe",
    r"C:\Program Files\Google\Chrome\Application\chrome.exe",
]

CSS = """
@page { size: A4; margin: 15mm 14mm; }
body { font-family: Segoe UI, Arial, sans-serif; font-size: 10.5pt; line-height: 1.45; color: #1b1b1b; }
h1 { color: #12356b; border-bottom: 3px solid #12356b; padding-bottom: 4px; page-break-before: always; font-size: 20pt; }
h1:first-of-type { page-break-before: avoid; }
h2 { color: #1f4f9c; margin-top: 18px; font-size: 14.5pt; border-bottom: 1px solid #c9d6ee; }
h3 { color: #2b5fb4; font-size: 12pt; }
pre { background: #f5f7fa; border: 1px solid #d8dee9; border-left: 4px solid #2b5fb4; padding: 8px;
      font-family: Consolas, monospace; font-size: 8.4pt; line-height: 1.25; white-space: pre-wrap;
      word-break: break-word; page-break-inside: avoid; }
code { font-family: Consolas, monospace; font-size: 9pt; background: #eef1f6; padding: 0 3px; border-radius: 3px; }
pre code { background: none; padding: 0; font-size: inherit; }
table { border-collapse: collapse; width: 100%; margin: 8px 0; font-size: 8.8pt; page-break-inside: auto; }
tr { page-break-inside: avoid; }
th { background: #12356b; color: #fff; text-align: left; padding: 4px 6px; }
td { border: 1px solid #cfd8e6; padding: 4px 6px; vertical-align: top; }
tr:nth-child(even) td { background: #f6f8fc; }
blockquote { border-left: 4px solid #aab; margin-left: 0; padding-left: 10px; color: #444; }
"""


def main():
    files = sorted(glob.glob(os.path.join(PARTS, "[0-9][0-9]_*.md")))
    if not files:
        sys.exit("No part files found")
    cover = ("# System Design Encyclopedia\n\n*A historical, causal guide to system design: concepts, trade-offs, "
             "examples and diagrams.*\n\nParts 1-13: reference tables. Parts 14-19: deep dives with examples and "
             "diagrams.\n\n")
    text = cover
    for f in files:
        if os.path.basename(f).startswith("00_"):
            continue
        with open(f, encoding="utf-8") as fh:
            text += "\n\n" + fh.read()
    body = markdown.markdown(text, extensions=["tables", "fenced_code", "sane_lists"])
    with open(HTML, "w", encoding="utf-8") as fh:
        fh.write(f"<html><head><meta charset='utf-8'><title>System Design Encyclopedia</title>"
                 f"<style>{CSS}</style></head><body>{body}</body></html>")
    browser = next((b for b in BROWSERS if os.path.exists(b)), None)
    if not browser:
        sys.exit("Edge/Chrome not found")
    subprocess.run([browser, "--headless", "--disable-gpu", "--no-pdf-header-footer",
                    f"--print-to-pdf={PDF}", "file:///" + HTML.replace("\\", "/")], check=True, timeout=300)
    print("Created", PDF, os.path.getsize(PDF), "bytes from", len(files), "files")


if __name__ == "__main__":
    main()

