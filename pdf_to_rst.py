#!/usr/bin/env python3
"""
PDF to RST Converter
Converts PDF text content to reStructuredText (RST) format
"""

import os
import re
from PyPDF2 import PdfReader
from pathlib import Path

def extract_text_from_pdf(pdf_path):
    """Extract text from PDF file"""
    try:
        reader = PdfReader(pdf_path)
        text = ""
        for page in reader.pages:
            text += page.extract_text() + "\n"
        return text
    except Exception as e:
        print(f"Error reading PDF: {e}")
        return None

def text_to_rst(text, output_dir):
    """Convert plain text to RST format and save to files"""
    if not text.strip():
        print("No text content found in PDF")
        return
    
    # Create output directory if it doesn't exist
    Path(output_dir).mkdir(exist_ok=True)
    
    # Split text into sections (assuming paragraphs separated by double newlines)
    paragraphs = re.split(r'\n\s*\n', text.strip())
    
    # Create main index.rst file
    index_content = """
PDF Document
============

This document was automatically converted from PDF to RST format.

.. toctree::
   :maxdepth: 2
   :caption: Contents:

"""
    
    # Process each paragraph as a separate section
    section_files = []
    for i, paragraph in enumerate(paragraphs):
        if paragraph.strip():
            # Clean up the paragraph text
            clean_text = re.sub(r'\s+', ' ', paragraph.strip())
            
            # Create a filename based on the first few words or section number
            first_words = clean_text[:30].replace(' ', '_').replace('/', '_').replace('\\', '_')
            first_words = re.sub(r'[^\w_]', '', first_words)
            if not first_words:
                first_words = f"section_{i+1}"
            
            filename = f"{i+1:02d}_{first_words.lower()}.rst"
            section_files.append(filename)
            
            # Create RST content for this section
            rst_content = f"""
Section {i+1}
{'=' * (len(f'Section {i+1}'))}

{clean_text}

"""
            
            # Write the section file
            filepath = os.path.join(output_dir, filename)
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(rst_content)
            
            # Add to index
            index_content += f"   {filename}\n"
    
    # Write the index file
    index_path = os.path.join(output_dir, "index.rst")
    with open(index_path, 'w', encoding='utf-8') as f:
        f.write(index_content)
    
    # Also create a single consolidated RST file
    consolidated_content = """
PDF Document - Complete Text
=============================

This document contains the complete text extracted from the PDF.

"""
    
    for i, paragraph in enumerate(paragraphs):
        if paragraph.strip():
            clean_text = re.sub(r'\s+', ' ', paragraph.strip())
            consolidated_content += f"""
Section {i+1}
{'-' * (len(f'Section {i+1}'))}

{clean_text}

"""
    
    consolidated_path = os.path.join(output_dir, "complete_document.rst")
    with open(consolidated_path, 'w', encoding='utf-8') as f:
        f.write(consolidated_content)
    
    return len(section_files)

def main():
    pdf_path = "just-text.pdf"
    output_dir = "rst_output"
    
    if not os.path.exists(pdf_path):
        print(f"PDF file not found: {pdf_path}")
        return
    
    print(f"Extracting text from {pdf_path}...")
    text = extract_text_from_pdf(pdf_path)
    
    if text:
        print("Converting to RST format...")
        num_sections = text_to_rst(text, output_dir)
        print(f"Successfully converted PDF to {num_sections} RST sections in '{output_dir}' directory")
        print(f"Files created:")
        print(f"  - {output_dir}/index.rst (main index)")
        print(f"  - {output_dir}/complete_document.rst (consolidated)")
        print(f"  - {num_sections} section files")
    else:
        print("Failed to extract text from PDF")

if __name__ == "__main__":
    main()
