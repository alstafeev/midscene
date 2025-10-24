package com.midscene.shared;

import java.util.Objects;

final class TestSupport {
    private TestSupport() {}

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " expected=" + expected + " actual=" + actual);
        }
    }

    static void assertContains(String text, String fragment, String message) {
        if (text == null || !text.contains(fragment)) {
            throw new AssertionError(message + " fragment=" + fragment + " text=" + text);
        }
    }

    static void assertThrows(Class<? extends Throwable> type, Runnable runnable, String message) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            if (type.isInstance(throwable)) {
                return;
            }
            throw new AssertionError(message + " wrong exception: " + throwable, throwable);
        }
        throw new AssertionError(message + " did not throw");
    }
}
