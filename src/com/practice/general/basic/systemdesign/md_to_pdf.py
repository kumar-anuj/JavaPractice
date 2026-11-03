"""Convert systemdesign.md (a prompt) into a readable PDF."""
import os
from xml.sax.saxutils import escape

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (ListFlowable, ListItem, PageBreak, Paragraph,
                                SimpleDocTemplate, Spacer)

HERE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.join(HERE, "systemdesign.md")
OUT = os.path.join(HERE, "systemdesign.pdf")

ss = getSampleStyleSheet()
title = ParagraphStyle("T", parent=ss["Title"], fontSize=20, spaceAfter=14)
h = ParagraphStyle("H", parent=ss["Heading2"], fontSize=13, spaceBefore=12,
                   spaceAfter=6, textColor="#1a3d7c")
body = ParagraphStyle("B", parent=ss["BodyText"], fontSize=10, leading=14)
bullet = ParagraphStyle("L", parent=body, leading=13)


def is_heading(line, nxt):
    s = line.strip()
    return bool(s) and s[0].isdigit() and ". " in s[:5] and s.split(". ", 1)[1].isupper()


def build():
    with open(SRC, encoding="utf-8") as f:
        lines = f.read().splitlines()

    story = [Paragraph("System Design Encyclopedia - Master Prompt", title)]
    items, para = [], []

    def flush_para():
        if para:
            story.append(Paragraph(escape(" ".join(para)), body))
            story.append(Spacer(1, 4))
            para.clear()

    def flush_items():
        if items:
            story.append(ListFlowable(
                [ListItem(Paragraph(escape(i), bullet), leftIndent=14) for i in items],
                bulletType="bullet", start="\u2022", leftIndent=14))
            story.append(Spacer(1, 6))
            items.clear()

    for i, raw in enumerate(lines):
        line = raw.strip()
        if not line:
            flush_para(); flush_items()
        elif is_heading(line, None):
            flush_para(); flush_items()
            story.append(Paragraph(escape(line), h))
        elif line.startswith("- "):
            flush_para()
            items.append(line[2:])
        elif line.startswith("###") or line.startswith("####"):
            flush_para(); flush_items()
            story.append(Paragraph(escape(line.lstrip("# ")), ParagraphStyle(
                "S", parent=body, fontName="Helvetica-Bold", spaceBefore=4)))
        else:
            flush_items()
            para.append(line)
    flush_para(); flush_items()

    doc = SimpleDocTemplate(OUT, pagesize=A4, leftMargin=2 * cm, rightMargin=2 * cm,
                            topMargin=2 * cm, bottomMargin=2 * cm,
                            title="System Design Encyclopedia Prompt")
    doc.build(story, onFirstPage=_footer, onLaterPages=_footer)
    print("Created", OUT)


def _footer(canvas, doc):
    canvas.setFont("Helvetica", 8)
    canvas.drawCentredString(A4[0] / 2, 1 * cm, f"Page {doc.page}")


if __name__ == "__main__":
    build()

