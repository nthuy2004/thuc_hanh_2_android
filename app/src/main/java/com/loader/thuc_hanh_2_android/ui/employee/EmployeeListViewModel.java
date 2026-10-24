package com.loader.thuc_hanh_2_android.ui.employee;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Employee;

import java.util.List;

public class EmployeeListViewModel extends AndroidViewModel {

    private final MutableLiveData<String> keyword = new MutableLiveData<>("");
    private final LiveData<List<Employee>> employees;

    public EmployeeListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        employees = Transformations.switchMap(keyword,
                k -> db.employeeDao().search(escapeLike(k)));
    }

    public LiveData<List<Employee>> getEmployees() {
        return employees;
    }

    public String getKeyword() {
        String k = keyword.getValue();
        return k == null ? "" : k;
    }

    public void setKeyword(String text) {
        String k = text == null ? "" : text.trim();
        if (!k.equals(getKeyword())) {
            keyword.setValue(k);
        }
    }

    /** Escape ky tu dac biet cua LIKE de tim dung chuoi nguoi dung go. */
    static String escapeLike(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
