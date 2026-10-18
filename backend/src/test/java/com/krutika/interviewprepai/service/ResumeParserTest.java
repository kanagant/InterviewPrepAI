package com.krutika.interviewprepai.service;

import com.krutika.interviewprepai.exception.ResumeParsingException;
import com.krutika.interviewprepai.exception.UnextractableResumeTextException;
import com.krutika.interviewprepai.exception.UnsupportedResumeFileTypeException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResumeParserTest {

    private final ResumeParser resumeParser = new ResumeParser();

    @Test
    void extractsTextFromPdf() throws IOException {
        byte[] pdfBytes = buildPdfWithText("Krutika Anaganti - Software Engineer");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        String text = resumeParser.extractText(file);

        assertThat(text).contains("Krutika Anaganti");
    }

    @Test
    void extractsTextFromDocx() throws IOException {
        byte[] docxBytes = buildDocxWithText("Krutika Anaganti - Software Engineer");
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", docxBytes);

        String text = resumeParser.extractText(file);

        assertThat(text).contains("Krutika Anaganti");
    }

    @Test
    void rejectsUnsupportedFileType() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.txt", "text/plain", "hello".getBytes());

        assertThatThrownBy(() -> resumeParser.extractText(file))
                .isInstanceOf(UnsupportedResumeFileTypeException.class);
    }

    @Test
    void rejectsCorruptPdf() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "not a real pdf".getBytes());

        assertThatThrownBy(() -> resumeParser.extractText(file))
                .isInstanceOf(ResumeParsingException.class);
    }

    @Test
    void rejectsCorruptDocx() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "not a real docx".getBytes());

        assertThatThrownBy(() -> resumeParser.extractText(file))
                .isInstanceOf(ResumeParsingException.class);
    }

    @Test
    void rejectsPdfWithNoExtractableText() throws IOException {
        byte[] pdfBytes = buildBlankPdf();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        assertThatThrownBy(() -> resumeParser.extractText(file))
                .isInstanceOf(UnextractableResumeTextException.class);
    }

    @Test
    void rejectsEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> resumeParser.extractText(file))
                .isInstanceOf(UnsupportedResumeFileTypeException.class);
    }

    private byte[] buildPdfWithText(String text) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                content.newLineAtOffset(50, 700);
                content.showText(text);
                content.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private byte[] buildBlankPdf() throws IOException {
        try (PDDocument document = new PDDocument()) {
            document.addPage(new PDPage());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    private byte[] buildDocxWithText(String text) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return out.toByteArray();
        }
    }
}
