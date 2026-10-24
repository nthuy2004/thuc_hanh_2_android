package com.loader.thuc_hanh_2_android;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.session.AccessGuard;
import com.loader.thuc_hanh_2_android.session.SessionManager;


public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AccessGuard.requireRole(this, Account.ROLE_ADMIN)) {
            return;
        }
        SessionManager session = new SessionManager(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);

        TextView hello = new TextView(this);
        hello.setTag("hello");
        hello.setText(getString(R.string.auth_welcome, session.getUsername(), session.getRole()));
        hello.setTextSize(20);
        root.addView(hello);

        Button logout = new Button(this);
        logout.setTag("logout");
        logout.setText(R.string.auth_logout);
        logout.setOnClickListener(v -> AccessGuard.logout(this));
        root.addView(logout);

        setContentView(root);
    }
}
