package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import ma.hps.portailagent.dto.request.CreateAgentRequest;
import ma.hps.portailagent.dto.response.CreatedAgent;
import ma.hps.portailagent.exception.ConflictException;
import ma.hps.portailagent.model.Agent;
import ma.hps.portailagent.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AgentService {
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private NotificationService notificationService;

    private final SecureRandom secureRandom = new SecureRandom();

    private String generateTempPassword() {
        String upper   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower   = "abcdefghijklmnopqrstuvwxyz";
        String digits  = "0123456789";
        String special = "!@#$%^&*";
        List<Character> chars = new ArrayList<>();
        // Garantir au moins 1 majuscule, 1 minuscule, 1 chiffre, 1 spécial
        chars.add(upper.charAt(secureRandom.nextInt(upper.length())));
        chars.add(lower.charAt(secureRandom.nextInt(lower.length())));
        chars.add(digits.charAt(secureRandom.nextInt(digits.length())));
        chars.add(special.charAt(secureRandom.nextInt(special.length())));
        // Compléter jusqu'à 12 caractères
        String all = upper + lower + digits + special;
        for (int i = 4; i < 12; i++) {
            chars.add(all.charAt(secureRandom.nextInt(all.length())));
        }
        Collections.shuffle(chars, secureRandom);
        StringBuilder sb = new StringBuilder();
        chars.forEach(sb::append);
        return sb.toString();
    }

    public List<CreatedAgent> getAllAgents() {
        return agentRepository.findAll().stream().map(agent -> {
            CreatedAgent response = new CreatedAgent();
            response.setId(agent.getId());
            response.setIdentifiant(agent.getAgentCode());
            String[] parts = agent.getFullName().split(" ", 2);
            response.setFirstName(parts.length > 0 ? parts[0] : "");
            response.setLastName(parts.length > 1 ? parts[1] : "");
            response.setAgentType(agent.getAgentType());
            response.setEmail(agent.getEmail());
            response.setPhone(agent.getPhone());
            response.setStatus(agent.isEnabled() ? "ACTIF" : "INACTIF");
            response.setOtpChannel(agent.getOtpChannel());
            response.setCreatedAt(agent.getCreatedAt() != null ? agent.getCreatedAt().toString() : null);
            return response;
        }).collect(Collectors.toList());
    }

    public CreatedAgent createAgent(CreateAgentRequest request) {

        // 1. Vérifier unicité identifiant
        if (agentRepository.findByAgentCode(request.getIdentifiant()).isPresent()) {
            throw new ConflictException("IDENTIFIANT_ALREADY_EXISTS", "Cet identifiant est déjà utilisé");
        }

        // 2. Vérifier unicité email
        if (agentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "Cet email est déjà associé à un compte");
        }

        // 3. Générer mot de passe temporaire (min 12 chars, 1 maj, 1 min, 1 chiffre, 1 spécial)
        String tempPwd = generateTempPassword();

        // 4. Construire et sauvegarder l'agent
        String otpChannel = request.getOtpChannel() != null ? request.getOtpChannel().toUpperCase() : "EMAIL";

        Agent agent = new Agent();
        agent.setAgentCode(request.getIdentifiant());
        agent.setFullName(request.getFirstName() + " " + request.getLastName());
        agent.setEmail(request.getEmail());
        agent.setPhone(request.getPhone());
        agent.setPasswordHash(passwordEncoder.encode(tempPwd));
        agent.setAgentType(request.getAgentType());
        agent.setEnabled(true);
        agent.setMustChangePassword(true);
        agent.setOtpChannel(otpChannel);

        Agent saved = agentRepository.save(agent);

        // 5. Sauvegarder les fonctionnalités (features)
        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            agentRepository.saveFeatures(saved.getId(), request.getFeatures());
        }

        // 6. Générer OTP + lancer les notifications en fire-and-forget
        // La réponse HTTP 201 est renvoyée IMMÉDIATEMENT — les notifications
        // tournent dans le notificationExecutor (thread séparé)
        try {
            String otp = otpService.generate(saved.getId());
            notificationService.sendNotificationsAsync(saved, otpChannel, tempPwd, otp);
        } catch (Exception e) {
            log.error("Erreur lancement notifications pour {}: {}", saved.getAgentCode(), e.getMessage());
        }

        // 7. Construire la réponse — createdAt en ISO 8601 avec Z
        String createdAtIso = OffsetDateTime.now(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS)
                .toString();

        CreatedAgent response = new CreatedAgent();
        response.setId(saved.getId());
        response.setIdentifiant(saved.getAgentCode());
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        response.setAgentType(request.getAgentType());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        response.setStatus("ACTIF");
        response.setCreatedAt(createdAtIso);
        response.setOtpChannel(otpChannel);

        return response;
    }
}
