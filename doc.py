from pathlib import Path
from docx import Document
from docx.shared import Pt
from docx.enum.text import WD_BREAK

# 🗂️ Folder where your project folders are
apps_root = Path(".")  # since your script is in the same folder

# ✅ List of app folders in desired order
project_order = [
        "SMS_Sender"
]

# 📝 Create a new Word document
doc = Document()

# Set default font to monospace for code blocks
style = doc.styles['Normal']
font = style.font
font.name = 'Courier New'
font.size = Pt(10)

def add_heading(text, level=1):
    doc.add_heading(text, level=level)

def add_code_block(text):
    paragraph = doc.add_paragraph()
    run = paragraph.add_run(text)
    run.font.name = 'Courier New'
    run.font.size = Pt(10)

def add_file_to_doc(file_path, relative_to):
    try:
        content = file_path.read_text(encoding='utf-8')
        doc.add_heading(str(file_path.relative_to(relative_to)), level=2)
        add_code_block(content)
    except Exception as e:
        add_code_block(f"⚠️ Could not read {file_path}: {e}")

for project in project_order:
    project_path = apps_root / project
    main_path = project_path / "app" / "src" / "main"
    java_path = main_path / "java"
    layout_path = main_path / "res" / "layout"
    values_path = main_path / "res" / "values"
    manifest_path = main_path / "AndroidManifest.xml"

    # Add project title
    add_heading(f"Project: {project}", level=1)

    # AndroidManifest.xml
    if manifest_path.exists():
        add_file_to_doc(manifest_path, main_path)

    # Java files
    if java_path.exists():
        for java_file in java_path.rglob("*.java"):
            add_file_to_doc(java_file, java_path)

    # Layout XMLs
    if layout_path.exists():
        for xml_file in layout_path.glob("*.xml"):
            add_file_to_doc(xml_file, layout_path)

    # colors.xml and styles.xml
    for file in ["colors.xml", "styles.xml"]:
        file_path = values_path / file
        if file_path.exists():
            add_file_to_doc(file_path, values_path)

    # Page break after each project
    doc.add_page_break()

# 🔽 Save the Word document
doc.save("smsCode.docx")
print("✅ Word document created: MAD_Assignment_Code.docx")
