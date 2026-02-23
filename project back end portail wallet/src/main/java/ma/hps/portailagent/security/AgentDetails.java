package ma.hps.portailagent.security;

import lombok.Data;
import ma.hps.portailagent.enums.Privilege;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AgentDetails implements UserDetails {
    private Long id;
    private String agentId;
    private String password;
    private String email;
    private Boolean enabled;
    private List<Privilege> privileges;

    public AgentDetails(Long id, String agentId, String password, String email, Boolean enabled, List<Privilege> privileges) {
        this.id = id;
        this.agentId = agentId;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.privileges = privileges != null ? privileges : List.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return privileges.stream()
                .map(privilege -> new SimpleGrantedAuthority("ROLE_" + privilege.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return agentId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }
}
