package com.midscene.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * Minimal JSON parser that supports objects with nested objects, numbers,
 * booleans, strings and {@code null} values. It is purposely small to avoid
 * pulling external dependencies in the constrained execution environment.
 */
final class SimpleJsonParser {
    private final String text;
    private int index;

    private SimpleJsonParser(String text) {
        this.text = text;
    }

    static Map<String, Object> parseObject(String text) {
        SimpleJsonParser parser = new SimpleJsonParser(text);
        parser.skipWhitespace();
        return parser.readObject();
    }

    private Map<String, Object> readObject() {
        expect('{');
        Map<String, Object> result = new HashMap<>();
        skipWhitespace();
        if (peek('}')) {
            index++;
            return result;
        }
        while (true) {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            Object value = readValue();
            result.put(key, value);
            skipWhitespace();
            if (peek('}')) {
                index++;
                break;
            }
            expect(',');
        }
        return result;
    }

    private Object readValue() {
        skipWhitespace();
        if (peek('"')) {
            return readString();
        }
        if (peek('{')) {
            return readObject();
        }
        if (peek('t') || peek('f')) {
            return readBoolean();
        }
        if (peek('n')) {
            readLiteral("null");
            return null;
        }
        return readNumber();
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (index < text.length()) {
            char ch = text.charAt(index++);
            if (ch == '\\') {
                if (index >= text.length()) {
                    throw error("Unexpected end of input inside string");
                }
                char escaped = text.charAt(index++);
                switch (escaped) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(escaped);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        if (index + 4 > text.length()) {
                            throw error("Invalid unicode escape");
                        }
                        String hex = text.substring(index, index + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        index += 4;
                        break;
                    default:
                        throw error("Invalid escape character: " + escaped);
                }
            } else if (ch == '"') {
                break;
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private Boolean readBoolean() {
        if (peek('t')) {
            readLiteral("true");
            return Boolean.TRUE;
        }
        readLiteral("false");
        return Boolean.FALSE;
    }

    private Number readNumber() {
        int start = index;
        if (peek('-')) {
            index++;
        }
        while (index < text.length() && Character.isDigit(text.charAt(index))) {
            index++;
        }
        if (peek('.')) {
            index++;
            while (index < text.length() && Character.isDigit(text.charAt(index))) {
                index++;
            }
        }
        if (peek('e') || peek('E')) {
            index++;
            if (peek('+') || peek('-')) {
                index++;
            }
            while (index < text.length() && Character.isDigit(text.charAt(index))) {
                index++;
            }
        }
        String number = text.substring(start, index);
        if (number.contains(".") || number.contains("e") || number.contains("E")) {
            return Double.parseDouble(number);
        }
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException ex) {
            return Double.parseDouble(number);
        }
    }

    private void readLiteral(String literal) {
        if (!text.startsWith(literal, index)) {
            throw error("Expected literal " + literal);
        }
        index += literal.length();
    }

    private void skipWhitespace() {
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            index++;
        }
    }

    private void expect(char expected) {
        skipWhitespace();
        if (index >= text.length() || text.charAt(index) != expected) {
            throw error("Expected '" + expected + "'");
        }
        index++;
    }

    private boolean peek(char ch) {
        skipWhitespace();
        return index < text.length() && text.charAt(index) == ch;
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " at position " + index);
    }
}
