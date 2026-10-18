package com.krutika.interviewprepai.service;

import com.krutika.interviewprepai.exception.ResumeParsingException;
import com.krutika.interviewprepai.exception.UnextractableResumeTextException;
import com.krutika.interviewprepai.exception.UnsupportedResumeFileTypeException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeParser {

    public String extractText(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (file.isEmpty() || filename == null || filename.isBlank()) {
            throw new UnsupportedResumeFileTypeException("No resume file was provided");
        }

        String lower = filename.toLowerCase();
        String text;
        if (lower.endsWith(".pdf")) {
            text = extractFromPdf(file);
        } else if (lower.endsWith(".docx")) {
            text = extractFromDocx(file);
        } else {
            throw new UnsupportedResumeFileTypeException(
                    "Unsupported file type - only .pdf and .docx are supported");
        }

        if (text.trim().isBlank()) {
            throw new UnextractableResumeTextException(
                    "We couldn't find any text in this file - it may be a scanned image. "
                            + "Try pasting your resume text instead.");
        }

        return text;
    }

    private String extractFromPdf(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        } catch (Exception e) {
            throw new ResumeParsingException("Failed to parse PDF resume - it may be corrupted or password protected", e);
        }
    }

    private String extractFromDocx(MultipartFile file) {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        } catch (Exception e) {
            throw new ResumeParsingException("Failed to parse DOCX resume - it may be corrupted", e);
        }
    }
}
