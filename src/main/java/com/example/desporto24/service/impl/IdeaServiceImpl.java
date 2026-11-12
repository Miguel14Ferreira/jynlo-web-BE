package com.example.desporto24.service.impl;

import com.example.desporto24.enumeration.IdeaStatus;
import com.example.desporto24.model.Ideas;
import com.example.desporto24.model.IdeaAttachment;
import com.example.desporto24.model.dto.idea.AdminIdeaResponse;
import com.example.desporto24.model.dto.idea.IdeaSubmissionRequest;
import com.example.desporto24.repository.IdeasRepository;
import com.example.desporto24.repository.IdeaAttachmentRepository;
import com.example.desporto24.repository.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import com.example.desporto24.service.EmailService;
import com.example.desporto24.service.interfaces.IdeaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;
import java.io.IOException;
import java.util.Map;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaServiceImpl implements IdeaService {

    private static final String MANAGEMENT_EMAIL = "desporto24app@gmail.com";

    private final IdeasRepository ideasRepository;
    private final IdeaAttachmentRepository ideaAttachments;
    private final EmailService emailService;
    private final ProfileRepository profiles;

    @Override
    public void submitNewIdea(IdeaSubmissionRequest request) {
        submitNewIdeaWithAttachment(request, null);
    }

    @Override
    @Transactional
    public void submitNewIdeaWithAttachment(IdeaSubmissionRequest request, MultipartFile image) {
        ValidImage validated = image == null ? null : validateImage(image);
        Ideas idea = new Ideas(
                request.name().trim(),
                request.email().trim(),
                trimToEmpty(request.city()),
                trimToEmpty(request.gender()),
                request.subject().trim(),
                request.problem().trim()
        );

        if (validated != null) {
            idea.setAttachmentName(validated.filename());
            idea.setAttachmentMimeType(validated.mimeType());
        }
        Ideas saved = ideasRepository.save(idea);
        if (validated != null) {
            ideaAttachments.save(new IdeaAttachment(saved.getId(), validated.bytes()));
        }

        try {
            emailService.send(request.email().trim(), buildConfirmationEmail());
        } catch (RuntimeException exception) {
            log.warn("Idea {} was saved but confirmation email could not be sent", saved.getId(), exception);
        }

        try {
            emailService.send(MANAGEMENT_EMAIL, buildManagementAlertEmail(saved));
        } catch (RuntimeException exception) {
            log.warn("Idea {} was saved but management notification email could not be sent", saved.getId(), exception);
        }
    }


    /** Only metadata is added to the admin listing; the binary image is never serialized. */
    private record ValidImage(String filename, String mimeType, byte[] bytes) {}
    private static final long MAX_IMAGE_SIZE = 3L * 1024L * 1024L;
    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png",
            "image/webp", ".webp", "image/gif", ".gif");

    private ValidImage validateImage(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > MAX_IMAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem inválida: máximo 3 MB");
        }
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length == 0 || bytes.length > MAX_IMAGE_SIZE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagem inválida: máximo 3 MB");
            }
            String mime = new Tika().detect(bytes);
            String extension = IMAGE_EXTENSIONS.get(mime);
            String declared = file.getContentType();
            if (extension == null || declared == null || !mime.equalsIgnoreCase(declared)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagem inválido");
            }
            String filename = file.getOriginalFilename() == null ? "captura" + extension
                    : file.getOriginalFilename().replace((char) 92, '/');
            filename = filename.substring(filename.lastIndexOf('/') + 1).trim();
            filename = filename.replaceAll("[\\p{Cntrl}]", "");
            if (filename.isEmpty()) filename = "captura" + extension;
            if (filename.length() > 150) filename = filename.substring(0, 150);
            return new ValidImage(filename, mime, bytes);
        } catch (IOException failure) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não foi possível ler a imagem");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDownload attachment(Long ideaId) {
        Ideas idea = ideasRepository.findById(ideaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mensagem não encontrada"));
        if (idea.getAttachmentMimeType() == null || idea.getAttachmentName() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Anexo não encontrado");
        }
        IdeaAttachment stored = ideaAttachments.findById(ideaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anexo não encontrado"));
        return new AttachmentDownload(idea.getAttachmentName(),
                idea.getAttachmentMimeType(), stored.getContent());
    }

    @Override
    public List<AdminIdeaResponse> getAllIdeas() {
        return ideasRepository.findAll().stream()
                .sorted(Comparator.comparing(
                        Ideas::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(this::toAdminResponse)
                .toList();
    }

    @Override
    public AdminIdeaResponse updateStatus(Long id, IdeaStatus status) {
        Ideas idea = ideasRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada"));

        idea.setStatus(status);
        return toAdminResponse(ideasRepository.save(idea));
    }

    @Override
    @Transactional
    public AdminIdeaResponse reply(Long id, String message, String actorUsername) {
        var actor = profiles.findUserByUsernameIgnoreCase(actorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem autorização"));
        if (!("ROLE_ADMIN".equals(actor.getRole()) || "ROLE_SUPER_ADMIN".equals(actor.getRole()))
                || !Boolean.TRUE.equals(actor.getEnabled())
                || !Boolean.TRUE.equals(actor.getNotLocked())
                || actor.getModerationSuspendedAt() != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem autorização");
        }
        String text = message == null ? "" : message.trim();
        if (text.length() < 3 || text.length() > 2000
                || text.chars().anyMatch(ch -> ch < 32 && ch != '\n' && ch != '\r')) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resposta inválida");
        }
        var idea = ideasRepository.findForReply(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sugestão não encontrada"));
        if (idea.getAdminRepliedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta sugestão já foi respondida");
        }
        String recipient = idea.getEmail() == null ? "" : idea.getEmail().trim();
        if (!recipient.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email do destinatário inválido");
        }
        String html = "<p>Olá " + html(idea.getName()) + ",</p>"
                + "<p>Obrigado por teres contactado a Jynlo.</p>"
                + "<p>" + html(text).replace("\n", "<br>") + "</p>"
                + "<p>Com os melhores cumprimentos,<br>Equipa Jynlo</p>";
        // Send synchronously. Failed delivery must not mark the idea as answered.
        emailService.send(recipient, "Jynlo — resposta à tua sugestão", html);
        idea.setAdminReply(text);
        idea.setAdminRepliedBy(actor.getUsername());
        idea.setAdminRepliedAt(Instant.now());
        return toAdminResponse(ideasRepository.save(idea));
    }

    private AdminIdeaResponse toAdminResponse(Ideas idea) {
        return new AdminIdeaResponse(
                idea.getId(),
                idea.getName(),
                idea.getCity(),
                idea.getEmail(),
                idea.getGender(),
                idea.getSubject(),
                idea.getProblem(),
                idea.getStatus() == null ? IdeaStatus.NEW : idea.getStatus(),
                idea.getCreatedAt(),
                idea.getAdminReply(),
                idea.getAdminRepliedAt(),
                idea.getAdminRepliedBy(),
                idea.getAttachmentName(),
                idea.getAttachmentMimeType()
        );
    }

    private String buildConfirmationEmail() {
        return "<h2>Obrigado pela tua mensagem</h2>"
                + "<p>Recebemos a tua sugestão no Desporto24 com sucesso.</p>"
                + "<p>A nossa equipa irá analisá-la no painel de gestão.</p>"
                + "<p>Se precisares de falar connosco diretamente, envia-nos um email para "
                + "<strong>" + MANAGEMENT_EMAIL + "</strong>.</p>"
                + "<p>Cumprimentos,<br>DESPORTO24 MANAGEMENT TEAM</p>";
    }

    private String buildManagementAlertEmail(Ideas idea) {
        return "<h2>Nova ideia recebida no DESPORTO24</h2>"
                + "<p><strong>ID:</strong> " + idea.getId() + "</p>"
                + "<p><strong>De:</strong> " + html(idea.getName()) + " &lt;" + html(idea.getEmail()) + "&gt;</p>"
                + "<p><strong>Categoria:</strong> " + html(idea.getSubject()) + "</p>"
                + "<p><strong>Mensagem:</strong></p>"
                + "<p>" + html(idea.getProblem()).replace("\n", "<br>") + "</p>"
                + "<p>A sugestão ficou guardada com o estado <strong>Nova</strong> no painel de administração.</p>";
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String html(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
