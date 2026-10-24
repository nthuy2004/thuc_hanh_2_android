package com.loader.thuc_hanh_2_android.session;

import android.app.Activity;
import android.content.Intent;

/**
 * Kiem tra quyen truy cap man hinh. Goi dau onCreate; neu tra ve false thi
 * dung khoi tao man hinh (Activity da duoc finish va chuyen huong).
 */
public final class AccessGuard {

    private AccessGuard() {
    }

    /** Chua dang nhap -> ve man dang nhap. */
    public static boolean requireLogin(Activity activity) {
        SessionManager session = new SessionManager(activity);
        if (session.isLoggedIn()) {
            return true;
        }
        redirect(activity, HomeRouter.loginIntent(activity));
        return false;
    }

    /** Sai role -> dua ve man hinh dung cua role hien tai (hoac man dang nhap). */
    public static boolean requireRole(Activity activity, String requiredRole) {
        SessionManager session = new SessionManager(activity);
        if (!session.isLoggedIn()) {
            redirect(activity, HomeRouter.loginIntent(activity));
            return false;
        }
        if (requiredRole.equals(session.getRole())) {
            return true;
        }
        redirect(activity, HomeRouter.homeFor(activity, session.getRole()));
        return false;
    }

    public static void logout(Activity activity) {
        new SessionManager(activity).clear();
        Intent login = HomeRouter.loginIntent(activity);
        activity.startActivity(login);
        activity.finish();
    }

    private static void redirect(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.finish();
    }
}
