package com.example.desporto24.service;

import com.example.desporto24.email.TransactionalEmailTemplate;
import com.example.desporto24.exception.EmailExistException;
import com.example.desporto24.model.EmailChangeRequest;
import com.example.desporto24.model.Profile;
import com.example.desporto24.repository.EmailChangeRequestRepository;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.security.PasswordHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.example.desporto24.constant.UserImplConstant.EMAIL_ALREADY_EXIST;
import static com.example.desporto24.constant.UserImplConstant.INCORRECT_CREDENTIALS;

/**
 * Changing the account email is security sensitive (it is how a password is
 * recovered), so it needs the current password and is applied only after the
 * new address confirms it through a single-use link. Google/Apple accounts,
 * which have no password they know, confirm through the link alone.
 */
@Service
@RequiredArgsConstructor
public class EmailChangeService {
    static final Duration LINK_TTL = Duration.ofHours(24);
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]{1,64}@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ProfileRepository profiles;
    private final EmailChangeRequestRepository requests;
    private final PasswordHashService passwords;
    private final EmailService email;

    @Value("${ui.app.url}")
    private String uiAppUrl;

    @Transactional
    public void request(String username, String newEmailRaw, String currentPassword) {
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new BadCredentialsException(INCORRECT_CREDENTIALS));
        String newEmail = newEmailRaw == null ? "" : newEmailRaw.strip().toLowerCase(Locale.ROOT);
        if (newEmail.length() > 254 || !EMAIL.matcher(newEmail).matches()) {
            throw new IllegalArgumentException("Indica um email válido");
        }
        if (newEmail.equalsIgnoreCase(profile.getEmail())) {
            throw new IllegalArgumentException("Esse já é o email da tua conta");
        }
        boolean social = profile.getGoogleSubject() != null || profile.getAppleSubject() != null;
        boolean passwordGiven = currentPassword != null && !currentPassword.isEmpty();
        if (passwordGiven ? !passwords.matchesAndUpgrade(profile, currentPassword) : !social) {
            throw new BadCredentialsException(INCORRECT_CREDENTIALS);
        }
        profiles.findUserByEmailIgnoreCase(newEmail)
                .filter(other -> !other.getId().equals(profile.getId()))
                .ifPresent(other -> { throw new EmailExistException(EMAIL_ALREADY_EXIST); });

        requests.deletePendingFor(profile.getUsername());
        String token = newToken();
        EmailChangeRequest pending = new EmailChangeRequest();
        pending.setTokenHash(hash(token));
        pending.setUsername(profile.getUsername());
        pending.setNewEmail(newEmail);
        pending.setCreatedAt(Instant.now());
        pending.setExpiresAt(Instant.now().plus(LINK_TTL));
        requests.save(pending);

        String base = uiAppUrl.replaceAll("/+$", "");
        email.send(newEmail, "Confirma o teu novo email | Confirm your new email — Jynlo",
                TransactionalEmailTemplate.render(
                        "Confirma o teu novo email",
                        "Olá " + profile.getUsername() + ", pediste para usar este endereço na tua conta Jynlo. Confirma para concluir a alteração.",
                        "Confirm your new email",
                        "Hi " + profile.getUsername() + ", you asked to use this address on your Jynlo account. Confirm to finish the change.",
                        "Confirmar email / Confirm email",
                        base + "/login/confirm-email/" + token,
                        "O link é válido durante 24 horas. Se não foste tu, ignora este email.",
                        "The link is valid for 24 hours. If this wasn't you, ignore this email."));
        if (profile.getEmail() != null && !profile.getEmail().isBlank()) {
            email.send(profile.getEmail(), "Pedido de alteração de email | Email change requested — Jynlo",
                    TransactionalEmailTemplate.render(
                            "Pedido de alteração de email",
                            "Olá " + profile.getUsername() + ", foi pedido para mudar o email da tua conta Jynlo para " + mask(newEmail)
                                    + ". O email só muda quando o novo endereço for confirmado.",
                            "Email change requested",
                            "Hi " + profile.getUsername() + ", someone asked to change your Jynlo account email to " + mask(newEmail)
                                    + ". It only changes once the new address is confirmed.",
                            "Redefinir palavra-passe / Reset password",
                            base + "/login/resetPassword",
                            "Se não foste tu, redefine já a palavra-passe e termina as sessões noutros dispositivos.",
                            "If this wasn't you, reset your password now and sign out of other devices."));
        }
    }

    /** Applies the change; returns the new email. */
    @Transactional
    public String confirm(String token) {
        if (token == null || token.length() < 20 || token.length() > 100) throw new IllegalStateException("Este link não é válido");
        EmailChangeRequest pending = requests.findById(hash(token))
                .orElseThrow(() -> new IllegalStateException("Este link não é válido ou já foi usado"));
        if (pending.getUsedAt() != null) throw new IllegalStateException("Este link já foi usado");
        if (pending.getExpiresAt().isBefore(Instant.now())) throw new IllegalStateException("Este link expirou. Pede a alteração outra vez");
        Profile profile = profiles.findUserByUsername(pending.getUsername())
                .orElseThrow(() -> new IllegalStateException("Este link não é válido"));
        profiles.findUserByEmailIgnoreCase(pending.getNewEmail())
                .filter(other -> !other.getId().equals(profile.getId()))
                .ifPresent(other -> { throw new EmailExistException(EMAIL_ALREADY_EXIST); });
        String previous = profile.getEmail();
        profile.setEmail(pending.getNewEmail());
        profiles.save(profile);
        pending.setUsedAt(Instant.now());
        requests.save(pending);
        if (previous != null && !previous.isBlank()) {
            email.send(previous, "Email alterado | Email address changed — Jynlo",
                    TransactionalEmailTemplate.render(
                            "Email alterado",
                            "Olá " + profile.getUsername() + ", o email da tua conta Jynlo passou a ser " + mask(pending.getNewEmail()) + ".",
                            "Email address changed",
                            "Hi " + profile.getUsername() + ", your Jynlo account email is now " + mask(pending.getNewEmail()) + ".",
                            "Redefinir palavra-passe / Reset password",
                            uiAppUrl.replaceAll("/+$", "") + "/login/resetPassword",
                            "Se não foste tu, contacta o suporte de imediato.",
                            "If this wasn't you, contact support immediately."));
        }
        return pending.getNewEmail();
    }

    static String mask(String address) {
        int at = address.indexOf('@');
        if (at <= 0) return "***";
        String local = address.substring(0, at), domain = address.substring(at + 1);
        return local.charAt(0) + "***@" + (domain.isEmpty() ? "***" : domain.charAt(0) + "***");
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
