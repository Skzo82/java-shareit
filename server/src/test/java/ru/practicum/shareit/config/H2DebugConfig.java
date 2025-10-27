package ru.practicum.shareit.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Configuration
@Profile("test")
public class H2DebugConfig {

    private final DataSource dataSource;

    public H2DebugConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void logH2Mode() {
        final String sqlSettings =
                "SELECT \"VALUE\" FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME='MODE'";
        final String sqlShow = "SHOW MODE";

        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {

            String mode = null;

            try (ResultSet rs = st.executeQuery(sqlSettings)) {
                if (rs.next()) mode = rs.getString(1);
            } catch (Exception ignored) {
                try (ResultSet rs = st.executeQuery(sqlShow)) {
                    if (rs.next()) mode = rs.getString(1);
                }
            }

            if (mode != null) {
                log.warn("H2 MODE = {}", mode);
            } else {
                log.warn("H2 MODE non rilevato (nessuna riga)");
            }
        } catch (Exception e) {
            log.error("Errore durante il controllo del MODE di H2", e);
        }
    }
}
