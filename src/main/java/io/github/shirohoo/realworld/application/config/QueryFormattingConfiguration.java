package io.github.shirohoo.realworld.application.config;

import static java.util.Arrays.stream;

import java.util.Stack;

import jakarta.annotation.PostConstruct;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.context.annotation.Configuration;

import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

@Configuration
public class QueryFormattingConfiguration {
    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(CustomMessageFormattingStrategy.class.getName());
    }

    public static class CustomMessageFormattingStrategy implements MessageFormattingStrategy {
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

            FormatStyle formatStyle = isDDL(query) ? FormatStyle.DDL : FormatStyle.BASIC;
            Formatter queryFormatter = formatStyle.getFormatter();

            String prettyQuery =
                    queryFormatter.format(query).replace("+0900", "").strip();

            return summary(prettyQuery, connectionId, elapsed, stacktrace());
        }

        private boolean isDDL(String query) {
            return query.startsWith("create") || query.startsWith("alter") || query.startsWith("comment");
        }

        private String summary(String query, int connectionId, long elapsed, String stacktrace) {
            return """

                ----------------------------------------------------------------------------------------------------
                                                               QUERY
                ----------------------------------------------------------------------------------------------------
                \t%s
                ----------------------------------------------------------------------------------------------------
                                                            INFORMATION
                ----------------------------------------------------------------------------------------------------
                Connection ID                           : %s
                Execution Time                          : %s ms
                Call Stack (number 1 is entry point)    :
                %s
                ----------------------------------------------------------------------------------------------------
                """
                    .formatted(query, connectionId, elapsed, stacktrace);
        }

        private String stacktrace() {
            Stack<String> callstack = new Stack<>();
            stream(new Throwable().getStackTrace())
                    .map(StackTraceElement::toString)
                    .filter(trace -> trace.startsWith("io.github.shirohoo")
                            && !trace.contains(getClass().getSimpleName()))
                    .forEach(callstack::push);

            StringBuilder traceBuilder = new StringBuilder();
            int order = 1;

            while (!callstack.empty()) {
                String trace = "\t%s. %s\n".formatted(order++, callstack.pop());
                traceBuilder.append(trace);
            }

            return traceBuilder.toString();
        }
    }
}
