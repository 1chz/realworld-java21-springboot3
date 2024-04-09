package sample.shirohoo.realworld.config;

import static java.util.Arrays.stream;

import java.util.Stack;

import jakarta.annotation.PostConstruct;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.context.annotation.Configuration;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

@Configuration
class DataSourceConfiguration {
    @PostConstruct
    void setQueryLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(PrettyQueryFormatStrategy.class.getName());
    }

    public static class PrettyQueryFormatStrategy implements MessageFormattingStrategy {
        @Override
        public String formatMessage(
                int connectionId,
                String now,
                long elapsed,
                String category,
                String prepared,
                String query,
                String url) {
            if (query == null || query.isBlank()) {
                return "";
            }

            Formatter queryFormatter = getQueryFormatter(query);
            String prettyQuery = getPrettyQuery(query, queryFormatter);
            return summary(prettyQuery, connectionId, elapsed, stacktrace());
        }

        private Formatter getQueryFormatter(String query) {
            if (query.startsWith("create") || query.startsWith("alter") || query.startsWith("comment")) {
                return FormatStyle.DDL.getFormatter();
            }
            return FormatStyle.BASIC.getFormatter();
        }

        private String getPrettyQuery(String query, Formatter queryFormatter) {
            return queryFormatter.format(query).replace("+0900", "").strip();
        }

        private String summary(String query, int connectionId, long elapsed, String stacktrace) {
            return """

                ----------------------------------------------------------------------------------------------------
                                                            QUERY LOG
                ----------------------------------------------------------------------------------------------------
                    %s
                ----------------------------------------------------------------------------------------------------
                - Connection ID                           : %s
                - Execution Time                          : %s ms
                - Call Stacks                             :
                %s
                ----------------------------------------------------------------------------------------------------
                """
                    .formatted(query, connectionId, elapsed, stacktrace);
        }

        private String stacktrace() {
            Stack<String> callstack = new Stack<>();
            stream(new Throwable().getStackTrace())
                    .map(StackTraceElement::toString)
                    .filter(trace -> trace.startsWith("sample.shirohoo.realworld"))
                    .filter(trace -> !trace.contains(getClass().getSimpleName()))
                    .filter(trace -> !trace.contains("CGLIB"))
                    .filter(trace -> !trace.contains("$Proxy"))
                    .forEach(callstack::push);

            StringBuilder traceBuilder = new StringBuilder();
            int order = 1;

            while (!callstack.empty()) {
                String trace = "     %s. %s\n".formatted(order++, callstack.pop());
                traceBuilder.append(trace);
            }

            return traceBuilder.toString();
        }
    }
}
