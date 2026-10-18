package com.krutika.interviewprepai.controller;

import com.krutika.interviewprepai.dto.CreateSessionRequest;
import com.krutika.interviewprepai.dto.GenerateArtifactRequest;
import com.krutika.interviewprepai.dto.GenerateArtifactResponse;
import com.krutika.interviewprepai.dto.SessionResponse;
import com.krutika.interviewprepai.model.PrepSession;
import com.krutika.interviewprepai.security.AuthenticatedUser;
import com.krutika.interviewprepai.service.ArtifactGenerationService;
import com.krutika.interviewprepai.service.PrepSessionService;
import com.krutika.interviewprepai.service.ResumeParser;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final PrepSessionService prepSessionService;
    private final ArtifactGenerationService artifactGenerationService;
    private final ResumeParser resumeParser;
    private final Validator validator;

    public SessionController(
            PrepSessionService prepSessionService,
            ArtifactGenerationService artifactGenerationService,
            ResumeParser resumeParser,
            Validator validator
    ) {
        this.prepSessionService = prepSessionService;
        this.artifactGenerationService = artifactGenerationService;
        this.resumeParser = resumeParser;
        this.validator = validator;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateSessionRequest request
    ) {
        PrepSession session = prepSessionService.create(
                user.userId(), request.jobDescription(), request.resumeText(), request.company(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionResponse.from(session));
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SessionResponse> createSessionFromUpload(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false, defaultValue = "") String jobDescription,
            @RequestParam(value = "company", required = false) String company,
            @RequestParam(value = "role", required = false) String role
    ) {
        String resumeText = resumeParser.extractText(file);
        CreateSessionRequest request = new CreateSessionRequest(jobDescription, resumeText, company, role);

        Set<ConstraintViolation<CreateSessionRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        PrepSession session = prepSessionService.create(
                user.userId(), request.jobDescription(), request.resumeText(), request.company(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(SessionResponse.from(session));
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<GenerateArtifactResponse> generate(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @RequestBody(required = false) GenerateArtifactRequest request
    ) {
        var type = (request != null ? request : new GenerateArtifactRequest(null)).typeOrDefault();
        GenerateArtifactResponse response = artifactGenerationService.generate(user.userId(), id, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> listSessions(@AuthenticationPrincipal AuthenticatedUser user) {
        List<SessionResponse> sessions = prepSessionService.findAllForUser(user.userId()).stream()
                .map(SessionResponse::from)
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSession(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        PrepSession session = prepSessionService.findByIdForUserOrThrow(id, user.userId());
        return ResponseEntity.ok(SessionResponse.from(session));
    }

    @GetMapping("/{id}/artifacts")
    public ResponseEntity<List<GenerateArtifactResponse>> listArtifacts(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(artifactGenerationService.listForSession(user.userId(), id));
    }
}
