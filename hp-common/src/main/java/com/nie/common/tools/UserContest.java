package com.nie.common.tools;

public class UserContest {
    private static final ThreadLocal<Integer> local = new ThreadLocal<>();

    public static void setUserId(Integer id) {
        local.set(id);
    }

    public static Integer getUserId() {
        return local.get();
    }

    public static void removeUser() {
        local.remove();
    }
}
