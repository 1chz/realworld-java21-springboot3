package io.github.shirohoo.realworld.application.config;

import static java.util.Arrays.stream;

import java.util.Locale;
import java.util.Stack;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class SQLTracer implements MessageFormattingStrategy {
    @Override
    public String formatMessage(
            int connectionId, String now, long elapsed, String category, String prepared, String query, String url) {
        if (query == null || query.isBlank()) {
            return "";
        }

        query = query.trim();
        Formatter queryFormatter =
                isDDL(category, query) ? FormatStyle.DDL.getFormatter() : FormatStyle.BASIC.getFormatter();

        query = queryFormatter.format(query).replace("+0900", "").trim();
        return formatQuery(query, connectionId, elapsed, callstack());
    }

    private boolean isDDL(String category, String query) {
        return isStatement(category) && isDDL(query.toLowerCase(Locale.ROOT));
    }

    private boolean isStatement(String category) {
        return Category.STATEMENT.getName().equalsIgnoreCase(category);
    }

    private boolean isDDL(String query) {
        return query.startsWith("create") || query.startsWith("alter") || query.startsWith("comment");
    }

    private String formatQuery(String query, int connectionId, long elapsed, StringBuilder callstack) {
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
                .formatted(query, connectionId, elapsed, callstack);
    }

    private StringBuilder callstack() {
        Stack<String> callstack = new Stack<>();
        stream(new Throwable().getStackTrace())
                .map(StackTraceElement::toString)
                .filter(stack -> stack.startsWith("io.github.shirohoo")
                        && !stack.contains(getClass().getSimpleName()))
                .forEach(callstack::push);

        int order = 1;
        StringBuilder trace = new StringBuilder();
        while (!callstack.empty()) {
            String stack = String.format("\t%s. %s\n", order++, callstack.pop());
            trace.append(stack);
        }
        return trace;
    }
}
