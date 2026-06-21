package school.cesar.acadlab;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Executa migrações Flyway APÓS o Hibernate criar/atualizar o schema (ddl-auto=update).
 * O auto-configure do Flyway está desabilitado (spring.flyway.enabled=false) para evitar
 * que o Flyway rode antes das tabelas existirem.
 */
@Component
@DependsOn("entityManagerFactory")
class FlywayConfig {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load()
                .migrate();
    }
}
