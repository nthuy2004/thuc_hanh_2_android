package com.loader.thuc_hanh_2_android.ui.employee;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.databinding.ActivityEmployeeListBinding;
import com.loader.thuc_hanh_2_android.employee.EmployeeRepository;
import com.loader.thuc_hanh_2_android.session.AccessGuard;

/** Man hinh Admin: quan ly nhan vien (xem, tim, them, sua, xoa mem). */
public class EmployeeListActivity extends AppCompatActivity
        implements EmployeeListAdapter.Listener, EmployeeEditDialog.Host {

    private ActivityEmployeeListBinding binding;
    private EmployeeListViewModel viewModel;
    private TextView emptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AccessGuard.requireRole(this, Account.ROLE_ADMIN)) {
            return;
        }
        binding = ActivityEmployeeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(EmployeeListViewModel.class);

        EmployeeListAdapter adapter = new EmployeeListAdapter(this);
        binding.rvEmployees.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEmployees.setAdapter(adapter);

        View emptyState = addEmptyState();

        viewModel.getEmployees().observe(this, list -> {
            adapter.submitList(list);
            boolean empty = list == null || list.isEmpty();
            emptyMessage.setText(viewModel.getKeyword().isEmpty()
                    ? R.string.employee_list_empty : R.string.employee_list_no_result);
            emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.rvEmployees.setVisibility(empty ? View.GONE : View.VISIBLE);
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setKeyword(s.toString());
            }
        });

        binding.fabAddEmployee.setOnClickListener(v ->
                EmployeeEditDialog.forAdd().show(getSupportFragmentManager(),
                        EmployeeEditDialog.TAG));
    }

    /** Dung lai layout empty_state.xml cua P4, dat duoi thanh tieu de. */
    private View addEmptyState() {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.empty_state, binding.el, false);
        emptyMessage = view.findViewById(R.id.tv_empty_message);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.header);
        view.setLayoutParams(params);
        view.setVisibility(View.GONE);
        binding.el.addView(view);
        return view;
    }

    // ----- EmployeeListAdapter.Listener -----

    @Override
    public void onEdit(Employee employee) {
        EmployeeEditDialog.forEdit(employee).show(getSupportFragmentManager(),
                EmployeeEditDialog.TAG);
    }

    @Override
    public void onDelete(Employee employee) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.employee_delete_title)
                .setMessage(getString(R.string.employee_delete_message, employee.fullname))
                .setPositiveButton(R.string.employee_delete_confirm,
                        (d, w) -> softDelete(employee))
                .setNegativeButton(R.string.employee_delete_cancel, null)
                .show();
    }

    private void softDelete(Employee employee) {
        AppDatabase db = AppDatabase.getInstance(this);
        AppDatabase.databaseExecutor.execute(() -> {
            new EmployeeRepository(db).softDelete(employee.id);
            runOnUiThread(() -> Toast.makeText(this, R.string.employee_deleted,
                    Toast.LENGTH_SHORT).show());
        });
    }

    // ----- EmployeeEditDialog.Host -----

    @Override
    public void onEmployeeAdded(String username) {
        Toast.makeText(this, getString(R.string.employee_added, username,
                EmployeeRepository.DEFAULT_PASSWORD), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEmployeeUpdated() {
        Toast.makeText(this, R.string.employee_updated, Toast.LENGTH_SHORT).show();
    }
}
