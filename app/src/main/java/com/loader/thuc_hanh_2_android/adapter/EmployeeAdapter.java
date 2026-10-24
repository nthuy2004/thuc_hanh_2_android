package com.loader.thuc_hanh_2_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loader.thuc_hanh_2_android.R;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    // Tạm thời dùng String, khi P1 tạo xong class Employee thì đổi String thành Employee
    private List<String> employeeList;

    public EmployeeAdapter(List<String> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        // Gán dữ liệu giả
        String data = employeeList.get(position);
        holder.tvName.setText("Nhân viên " + data);
        
        // Bắt sự kiện nút sửa / xóa
        holder.btnEdit.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Sửa nhân viên " + data, Toast.LENGTH_SHORT).show()
        );
        holder.btnDelete.setOnClickListener(v -> 
            Toast.makeText(v.getContext(), "Xóa nhân viên " + data, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return employeeList != null ? employeeList.size() : 0;
    }

    static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvRate;
        ImageButton btnEdit, btnDelete;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_employee_name);
            tvCode = itemView.findViewById(R.id.tv_employee_code);
            tvRate = itemView.findViewById(R.id.tv_employee_hourly_rate);
            btnEdit = itemView.findViewById(R.id.btn_edit_employee);
            btnDelete = itemView.findViewById(R.id.btn_delete_employee);
        }
    }
}