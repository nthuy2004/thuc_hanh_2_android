package com.loader.thuc_hanh_2_android;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loader.thuc_hanh_2_android.auth.AuthService;
import com.loader.thuc_hanh_2_android.auth.LoginResult;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.databinding.ActivityLoginBinding;
import com.loader.thuc_hanh_2_android.session.HomeRouter;
import com.loader.thuc_hanh_2_android.session.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager session;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        session = new SessionManager(this);
        authService = new AuthService(AppDatabase.getInstance(this));

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = binding.edtUsername.getText().toString().trim();
        String password = binding.edtPassword.getText().toString();

        binding.edtUsername.setError(null);
        binding.edtPassword.setError(null);
        if (TextUtils.isEmpty(username)) {
            binding.edtUsername.setError(getString(R.string.auth_error_username_required));
            binding.edtUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.edtPassword.setError(getString(R.string.auth_error_password_required));
            binding.edtPassword.requestFocus();
            return;
        }

        binding.btnLogin.setEnabled(false);
        AppDatabase.databaseExecutor.execute(() -> {
            LoginResult result = authService.login(username, password);
            runOnUiThread(() -> onLoginResult(result));
        });
    }

    private void onLoginResult(LoginResult result) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        binding.btnLogin.setEnabled(true);
        switch (result.status) {
            case SUCCESS:
                long employeeId = result.employee != null
                        ? result.employee.id : SessionManager.NO_ID;
                session.saveSession(result.account.id, employeeId,
                        result.account.username, result.account.role);
                startActivity(HomeRouter.homeFor(this, result.account.role));
                finish();
                break;
            case ACCOUNT_INACTIVE:
                Toast.makeText(this, R.string.auth_error_account_inactive,
                        Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, R.string.auth_error_invalid_credentials,
                        Toast.LENGTH_LONG).show();
                binding.edtPassword.setText("");
                break;
        }
    }
}
