package com.example.desporto24.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds every REST controller (and so the services, repositories and Hibernate pieces they use)
 * before Tomcat starts accepting requests. Production keeps lazy beans, so without this the first
 * requests after a deploy built them on demand and took 2-10 s. A controller that cannot be built is
 * logged and skipped: it would have failed on its first request anyway, and must not stop the app.
 * Runs in a lifecycle phase before the web server (Integer.MAX_VALUE - 1), so the port opens warm.
 */
@Component
@ConditionalOnProperty(name = "jynlo.startup.warm-up", havingValue = "true")
public class StartupWarmUp implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(StartupWarmUp.class);
    private final ApplicationContext context;
    private volatile boolean running;

    public StartupWarmUp(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void start() {
        long started = System.currentTimeMillis();
        int built = 0;
        List<String> failed = new ArrayList<>();
        for (String name : context.getBeanNamesForAnnotation(RestController.class)) {
            try {
                context.getBean(name);
                built++;
            } catch (RuntimeException e) {
                failed.add(name);
                log.warn("JYNLO_WARMUP_SKIPPED bean={} reason={}", name, e.getClass().getSimpleName());
            }
        }
        try {
            DataSource dataSource = context.getBean(DataSource.class);
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                statement.execute("select 1");
            }
        } catch (Exception e) {
            log.warn("JYNLO_WARMUP_DB_SKIPPED reason={}", e.getClass().getSimpleName());
        }
        running = true;
        log.info("JYNLO_WARMUP_DONE controllers={} skipped={} ms={}", built, failed.size(), System.currentTimeMillis() - started);
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /** Before WebServerStartStopLifecycle (Integer.MAX_VALUE - 1): the port opens after the warm-up. */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 100;
    }
}
