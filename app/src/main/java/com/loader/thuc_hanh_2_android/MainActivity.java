package com.loader.thuc_hanh_2_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.loader.thuc_hanh_2_android.session.HomeRouter;
import com.loader.thuc_hanh_2_android.session.SessionManager;

/**
 * Cong dieu huong: khong co giao dien, chi quyet dinh mo man hinh nao.
 * Chua dang nhap -> LoginActivity; da dang nhap -> man hinh theo role.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session = new SessionManager(this);
        Intent target = session.isLoggedIn()
                ? HomeRouter.homeFor(this, session.getRole())
                : HomeRouter.loginIntent(this);
        startActivity(target);
        finish();
    }
}
