package com.example.desporto24.service.clienterror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Writes errors reported by the app and the website to the server log, where
 * Render keeps them. No third-party service and no new account are needed.
 *
 * The report is treated as untrusted: fields are cut to a fixed size, control
 * characters are removed (so a report cannot forge extra log lines), and
 * emails, tokens, phone-like numbers and URL query strings are masked. The
 * username is never logged, only whether the person was signed in.
 *
 * Limits are per server instance and per minute: a global cap, a cap per
 * signed-in person, and a cap per distinct error so one crash loop cannot
 * flood the log. Reports over a limit are dropped silently.
 */
@Service
public class ClientErrorReporter {

    static final int GLOBAL_PER_MINUTE = 120;
    static final int PER_USER_PER_MINUTE = 20;
    static final int PER_ERROR_PER_MINUTE = 5;
    static final int MAX_MESSAGE = 300;
    static final int MAX_STACK = 1500;
    static final int MAX_ROUTE = 120;
    static final int MAX_VERSION = 32;

    private static final Set<String> PLATFORMS = Set.of("android", "ios", "web");
    private static final Logger log = LoggerFactory.getLogger("client-error");

    private static final Pattern CONTROL = Pattern.compile("[\\p{Cntrl}&&[^\\n]]");
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    private static final Pattern BEARER = Pattern.compile("(?i)bearer\\s+\\S+");
    private static final Pattern JWT = Pattern.compile("eyJ[\\w-]{5,}\\.[\\w-]{5,}\\.[\\w-]{5,}");
    private static final Pattern LONG_TOKEN = Pattern.compile("\\b[A-Za-z0-9_-]{32,}\\b");
    private static final Pattern PHONE = Pattern.compile("\\+?\\d[\\d ]{8,}\\d");
    private static final Pattern QUERY = Pattern.compile("\\?[^\\s)\"']*");

    private final Clock clock;
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private volatile long window = -1;

    public ClientErrorReporter() {
        this(Clock.systemUTC());
    }

    ClientErrorReporter(Clock clock) {
        this.clock = clock;
    }

    public record Report(String platform, String appVersion, String route,
                         String message, String stack, Boolean fatal) {}

    /** @return true when the report was written to the log. */
    public boolean report(Report report, boolean signedIn, String username) {
        if (report == null) return false;
        String platform = report.platform() == null ? "" : report.platform().trim().toLowerCase(Locale.ROOT);
        if (!PLATFORMS.contains(platform)) return false;
        String message = clean(report.message(), MAX_MESSAGE, false);
        if (message.isEmpty()) return false;
        String stack = clean(report.stack(), MAX_STACK, true);
        String route = clean(report.route(), MAX_ROUTE, false);
        String version = clean(report.appVersion(), MAX_VERSION, false);

        rollWindow();
        if (!take("global", GLOBAL_PER_MINUTE)) return false;
        if (signedIn && username != null
                && !take("user:" + username.toLowerCase(Locale.ROOT), PER_USER_PER_MINUTE)) return false;
        String firstFrame = stack.isEmpty() ? "" : stack.lines().findFirst().orElse("");
        if (!take("error:" + platform + '|' + message + '|' + firstFrame, PER_ERROR_PER_MINUTE)) return false;

        log.warn("client_error platform={} version={} route={} fatal={} signedIn={} message=\"{}\" stack=\"{}\"",
                platform, version.isEmpty() ? "-" : version, route.isEmpty() ? "-" : route,
                Boolean.TRUE.equals(report.fatal()), signedIn, message,
                stack.replace("\n", " | "));
        return true;
    }

    static String clean(String value, int max, boolean keepLines) {
        if (value == null) return "";
        String text = value.length() > max * 4 ? value.substring(0, max * 4) : value;
        text = CONTROL.matcher(text).replaceAll(" ");
        if (!keepLines) text = text.replace('\n', ' ');
        text = BEARER.matcher(text).replaceAll("Bearer [removido]");
        text = JWT.matcher(text).replaceAll("[token]");
        text = EMAIL.matcher(text).replaceAll("[email]");
        text = QUERY.matcher(text).replaceAll("?[...]");
        text = LONG_TOKEN.matcher(text).replaceAll("[token]");
        text = PHONE.matcher(text).replaceAll("[numero]");
        text = text.replace("\"", "'").strip();
        return text.length() > max ? text.substring(0, max) : text;
    }

    private void rollWindow() {
        long minute = clock.millis() / 60_000L;
        if (minute != window) {
            synchronized (this) {
                if (minute != window) {
                    counters.clear();
                    window = minute;
                }
            }
        }
    }

    private boolean take(String key, int max) {
        if (counters.size() > 5_000 && !counters.containsKey(key)) return false;
        return counters.computeIfAbsent(key, k -> new AtomicInteger()).incrementAndGet() <= max;
    }
}
