package io.github.ktg.temm.app.security;

public class LoginContext {

    private static final ThreadLocal<String> LOGIN_CONTEXT = new ThreadLocal<>();

    public static void set(String loginUserId) {
        LOGIN_CONTEXT.set(loginUserId);
    }

    public static String get() {
        return LOGIN_CONTEXT.get();
    }

    public static void remove() {
        LOGIN_CONTEXT.remove();
    }

}
