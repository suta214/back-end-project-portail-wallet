package ma.hps.portailagent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Slf4j
public class OtpService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final SecureRandom random = new SecureRandom();

    public String generate(Long agentId) {
        // Générer 6 chiffres aléatoires
        int code = 100000 + random.nextInt(900000);
        String codeStr = String.valueOf(code);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);

        jdbcTemplate.update(
                "INSERT INTO otp_codes (agent_id, code, expired_at, used) VALUES (?, ?, ?, FALSE)",
                agentId, codeStr, Timestamp.valueOf(expiredAt)
        );

        log.info("OTP généré pour l'agent {}: expire à {}", agentId, expiredAt);
        return codeStr;
    }
}
