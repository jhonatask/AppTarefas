package br.com.jproject.apptarefas.core.config;

import br.com.jproject.apptarefas.entities.User;
import br.com.jproject.apptarefas.enums.RolesValuesEnum;
import br.com.jproject.apptarefas.repository.RoleRepository;
import br.com.jproject.apptarefas.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Set;

@Configuration
public class AdminUserDataInitializationConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserDataInitializationConfig(RoleRepository roleRepository,
                                             UserRepository userRepository,
                                             PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        runReactive().block();
    }

    public Mono<Void> runReactive() {
        return roleRepository.findByName(RolesValuesEnum.ADMIN.name())
                .flatMap(roleAdmin -> userRepository.findByName("admin")
                        .flatMap(user -> {
                            System.out.println("admin ja existe");
                            return Mono.empty();
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            var user = new User();
                            user.setName("admin");
                            user.setEmail("admin@tasks.com.br");
                            user.setPassword(passwordEncoder.encode("Pamonha123*"));
                            user.setRoles(Set.of(roleAdmin));
                            return userRepository.save(user).then();
                        }))
                ).then();
    }
}
