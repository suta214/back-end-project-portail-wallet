package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.model.Agent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    private static final String PORTAL_URL = "https://portailagent.hps.ma/login";
    private static final String FROM_EMAIL = "noreply@portailagent.ma";
    private static final String FROM_NAME  = "Portail Agent HPS";

    @Value("${mailtrap.api-token:}")
    private String apiToken;

    @Value("${mailtrap.inbox-id:4414169}")
    private String inboxId;

    @Value("${mailtrap.api-url:https://sandbox.api.mailtrap.io/api/send}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // ─────────────────────────────────────────────────────────────────
    // Point d'entrée unique — fire-and-forget depuis AgentService
    // ─────────────────────────────────────────────────────────────────

    @Async("notificationExecutor")
    public void sendNotificationsAsync(Agent agent, String otpChannel, String tempPassword, String otp) {
        String[] parts   = splitName(agent.getFullName());
        String firstName = parts[0];
        String lastName  = parts[1];

        try {
            if ("SMS".equals(otpChannel)) {
                sendSmsOtp(agent.getPhone(), firstName, otp);
                sendCredentialsEmail(agent, firstName, lastName, tempPassword,
                    "Votre code OTP a été envoyé par SMS au " + agent.getPhone() + ".");
            } else {
                sendOtpEmail(agent.getEmail(), firstName, lastName, otp);
                sendCredentialsEmail(agent, firstName, lastName, tempPassword, null);
            }
        } catch (Exception e) {
            log.error("[NOTIFICATION] Erreur pour {}: {}", agent.getAgentCode(), e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Email 1 — Identifiants de connexion
    // ─────────────────────────────────────────────────────────────────

    private void sendCredentialsEmail(Agent agent, String firstName, String lastName,
                                      String tempPassword, String otpNote) {
        String noteHtml = (otpNote != null) ? "<p>ℹ️ " + otpNote + "</p>" : "";

        String html =
            "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto'>" +
            "<h2 style='color:#C8102E'>Portail Agent HPS</h2>" +
            "<p>Bonjour <b>" + firstName + " " + lastName + "</b>,</p>" +
            "<p>Votre compte agent a été créé avec succès sur le Portail Agent HPS.</p>" +
            "<p><b>Vos identifiants de connexion :</b></p>" +
            "<table style='border-collapse:collapse;margin:16px 0;background:#f9f9f9;border-radius:8px;width:100%'>" +
            "<tr><td style='padding:12px 16px;font-weight:bold;width:180px'>Identifiant</td>" +
                "<td style='padding:12px 16px;font-family:monospace;font-size:15px;font-weight:bold'>" + agent.getAgentCode() + "</td></tr>" +
            "<tr style='background:#f0f0f0'><td style='padding:12px 16px;font-weight:bold'>Mot de passe temporaire</td>" +
                "<td style='padding:12px 16px;font-family:monospace;font-size:15px;font-weight:bold'>" + tempPassword + "</td></tr>" +
            "</table>" +
            noteHtml +
            "<p style='color:#C8102E;font-weight:bold'>⚠️ Vous devrez changer votre mot de passe lors de votre première connexion.</p>" +
            "<p>Lien de connexion : <a href='" + PORTAL_URL + "'>" + PORTAL_URL + "</a></p>" +
            "<br><p style='color:#888'>Équipe HPS — ne pas répondre à cet email</p>" +
            "</div>";

        sendEmail(agent.getEmail(), firstName + " " + lastName,
            "Vos identifiants – Portail Agent HPS", html);
        log.info("[EMAIL] Identifiants envoyés à {}", agent.getEmail());
    }

    // ─────────────────────────────────────────────────────────────────
    // Email 2 — Code OTP
    // ─────────────────────────────────────────────────────────────────

    private void sendOtpEmail(String to, String firstName, String lastName, String otp) {
        String html =
            "<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto'>" +
            "<h2 style='color:#C8102E'>Portail Agent HPS</h2>" +
            "<p>Bonjour <b>" + firstName + " " + lastName + "</b>,</p>" +
            "<p>Votre compte agent a été créé avec succès.</p>" +
            "<p><b>Votre code de confirmation OTP :</b></p>" +
            "<div style='font-size:32px;font-weight:bold;letter-spacing:10px;" +
                "padding:20px;background:#f0f0f0;border-radius:8px;" +
                "text-align:center;margin:20px 0;color:#C8102E'>" + otp + "</div>" +
            "<p>Ce code est valable <b>15 minutes</b>. Ne le communiquez à personne.</p>" +
            "<br><p style='color:#888'>Équipe HPS</p>" +
            "</div>";

        sendEmail(to, firstName + " " + lastName,
            "Bienvenue sur le Portail Agent HPS – Code de confirmation", html);
        log.info("[EMAIL] OTP envoyé à {}", to);
    }

    // ─────────────────────────────────────────────────────────────────
    // SMS OTP — stub (log uniquement; brancher Twilio/Infobip ici)
    // ─────────────────────────────────────────────────────────────────

    private void sendSmsOtp(String phone, String firstName, String otp) {
        String smsText =
            "HPS-PORTAIL: Bonjour " + firstName + ", votre compte Portail Agent a ete cree. " +
            "Code OTP: " + otp + ". Valable 15 min. Ne le communiquez pas.";
        log.info("[SMS] TO:{} | MSG:{}", phone, smsText);
        // TODO: Twilio.init(accountSid, authToken); Message.creator(...).create();
    }

    // ─────────────────────────────────────────────────────────────────
    // Envoi via Mailtrap Email API (HTTPS port 443 — contourne firewall)
    // ─────────────────────────────────────────────────────────────────

    private void sendEmail(String toEmail, String toName, String subject, String htmlBody) {
        if (apiToken == null || apiToken.isBlank() || apiToken.startsWith("METTRE")) {
            log.warn("[EMAIL] API token Mailtrap non configuré — email ignoré pour {}", toEmail);
            log.info("[EMAIL-PREVIEW] TO:{} | SUJET:{}", toEmail, subject);
            return;
        }

        try {
            String url = apiUrl + "/" + inboxId;

            Map<String, Object> body = new HashMap<>();
            body.put("to", List.of(Map.of("email", toEmail, "name", toName)));
            body.put("from", Map.of("email", FROM_EMAIL, "name", FROM_NAME));
            body.put("subject", subject);
            body.put("html", htmlBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken != null ? apiToken : "");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("[EMAIL] Envoyé avec succès à {} via API Mailtrap", toEmail);
            } else {
                log.error("[EMAIL] Échec API Mailtrap: HTTP {} pour {}", response.getStatusCode(), toEmail);
            }
        } catch (Exception e) {
            log.error("[EMAIL] Erreur API Mailtrap pour {}: {}", toEmail, e.getMessage());
        }
    }

    private String[] splitName(String fullName) {
        if (fullName == null) return new String[]{"", ""};
        String[] parts = fullName.split(" ", 2);
        return new String[]{
            parts.length > 0 ? parts[0] : "",
            parts.length > 1 ? parts[1] : ""
        };
    }
}
