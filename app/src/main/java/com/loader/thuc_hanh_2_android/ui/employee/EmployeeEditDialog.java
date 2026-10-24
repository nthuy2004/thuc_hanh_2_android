package com.loader.thuc_hanh_2_android.ui.employee;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.loader.thuc_hanh_2_android.R;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.employee.EmployeeForm;
import com.loader.thuc_hanh_2_android.employee.EmployeeRepository;
import com.loader.thuc_hanh_2_android.employee.FormErrors;

/** Hop thoai them / sua nhan vien, dung layout dialog_employee_edit.xml cua P4. */
public class EmployeeEditDialog extends DialogFragment {

    public static final String TAG = "EmployeeEditDialog";

    public interface Host {
        void onEmployeeAdded(String username);

        void onEmployeeUpdated();
    }

    private static final String ARG_ID = "id";
    private static final String ARG_CODE = "code";
    private static final String ARG_NAME = "name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_RATE = "rate";

    private static final long NEW_ID = -1;

    public static EmployeeEditDialog forAdd() {
        EmployeeEditDialog d = new EmployeeEditDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, NEW_ID);
        d.setArguments(args);
        return d;
    }

    public static EmployeeEditDialog forEdit(Employee e) {
        EmployeeEditDialog d = new EmployeeEditDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, e.id);
        args.putString(ARG_CODE, e.code);
        args.putString(ARG_NAME, e.fullname);
        args.putString(ARG_EMAIL, e.email);
        args.putString(ARG_PHONE, e.phone);
        args.putString(ARG_RATE, formatRate(e.hourlyRate));
        d.setArguments(args);
        return d;
    }

    private static String formatRate(double rate) {
        return rate == Math.rint(rate) ? String.valueOf((long) rate) : String.valueOf(rate);
    }

    private long employeeId;
    private EditText edtCode;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPhone;
    private EditText edtRate;
    private View btnSave;

    private boolean isEdit() {
        return employeeId != NEW_ID;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = requireArguments();
        employeeId = args.getLong(ARG_ID, NEW_ID);

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_employee_edit, null);
        edtCode = view.findViewById(R.id.edt_emp_code);
        edtName = view.findViewById(R.id.edt_emp_name);
        edtEmail = view.findViewById(R.id.edt_emp_email);
        edtPhone = view.findViewById(R.id.edt_emp_phone);
        edtRate = view.findViewById(R.id.edt_emp_rate);
        btnSave = view.findViewById(R.id.btn_save);

        ((TextView) view.findViewById(R.id.tv_dialog_title)).setText(isEdit()
                ? R.string.employee_dialog_edit_title : R.string.employee_dialog_add_title);

        if (isEdit() && savedInstanceState == null) {
            edtCode.setText(args.getString(ARG_CODE));
            edtName.setText(args.getString(ARG_NAME));
            edtEmail.setText(args.getString(ARG_EMAIL));
            edtPhone.setText(args.getString(ARG_PHONE));
            edtRate.setText(args.getString(ARG_RATE));
        }
        if (isEdit()) {
            // Ma nhan vien gan voi tai khoan dang nhap nen khong cho doi.
            edtCode.setEnabled(false);
        }

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> save());

        return new MaterialAlertDialogBuilder(requireContext()).setView(view).create();
    }

    private void save() {
        EmployeeForm form = new EmployeeForm(
                edtCode.getText().toString(),
                edtName.getText().toString(),
                edtEmail.getText().toString(),
                edtPhone.getText().toString(),
                edtRate.getText().toString());
        clearErrors();
        btnSave.setEnabled(false);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        AppDatabase.databaseExecutor.execute(() -> {
            EmployeeRepository repo = new EmployeeRepository(db);
            FormErrors errors = isEdit()
                    ? repo.update(employeeId, form)
                    : repo.add(form);
            String username = EmployeeRepository.usernameFor(form.code);
            if (getActivity() == null) {
                return;
            }
            requireActivity().runOnUiThread(() -> onSaveResult(errors, username));
        });
    }

    private void onSaveResult(FormErrors errors, String username) {
        if (!isAdded()) {
            return;
        }
        btnSave.setEnabled(true);
        if (errors.hasErrors()) {
            showErrors(errors);
            return;
        }
        Host host = (Host) requireActivity();
        if (isEdit()) {
            host.onEmployeeUpdated();
        } else {
            host.onEmployeeAdded(username);
        }
        dismiss();
    }

    private void clearErrors() {
        edtCode.setError(null);
        edtName.setError(null);
        edtEmail.setError(null);
        edtPhone.setError(null);
        edtRate.setError(null);
    }

    private void showErrors(FormErrors e) {
        EditText first = null;
        if (e.code != null) {
            edtCode.setError(e.code);
            first = edtCode;
        }
        if (e.fullname != null) {
            edtName.setError(e.fullname);
            if (first == null) first = edtName;
        }
        if (e.email != null) {
            edtEmail.setError(e.email);
            if (first == null) first = edtEmail;
        }
        if (e.phone != null) {
            edtPhone.setError(e.phone);
            if (first == null) first = edtPhone;
        }
        if (e.hourlyRate != null) {
            edtRate.setError(e.hourlyRate);
            if (first == null) first = edtRate;
        }
        if (e.general != null) {
            edtName.setError(e.general);
            if (first == null) first = edtName;
        }
        if (first != null && first.isEnabled()) {
            first.requestFocus();
        }
    }
}
