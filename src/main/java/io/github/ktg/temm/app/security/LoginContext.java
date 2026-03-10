package io.github.ktg.temm.app.security;

import java.util.List;
import java.util.UUID;

public class LoginContext {

    private static final ThreadLocal<LoginUser> LOGIN_CONTEXT = new ThreadLocal<>();

    public static void set(LoginUser loginUser) {
        LOGIN_CONTEXT.set(loginUser);
    }

    public static LoginUser get() {
        return LOGIN_CONTEXT.get();
    }

    public static UUID getUserId() {
        LoginUser loginUser = get();
        return loginUser == null ? null : loginUser.userId();
    }

    public static List<LoginUserStore> getStoreInfos() {
        LoginUser loginUser = get();
        return loginUser == null ? null : loginUser.storeInfos();
    }

    public static void remove() {
        LOGIN_CONTEXT.remove();
    }

}
