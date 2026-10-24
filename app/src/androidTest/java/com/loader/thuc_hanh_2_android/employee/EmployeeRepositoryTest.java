package com.loader.thuc_hanh_2_android.employee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.auth.AuthService;
import com.loader.thuc_hanh_2_android.auth.LoginResult;
import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Account;
import com.loader.thuc_hanh_2_android.data.entity.Employee;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EmployeeRepositoryTest {

    private Context context;
    private AppDatabase db;
    private EmployeeRepository repo;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        AppDatabase.resetForTesting(context);
        db = AppDatabase.getInstance(context);
        repo = new EmployeeRepository(db);
    }

    @After
    public void tearDown() {
        // Tra DB ve trang thai seed sach cho cac test khac.
        AppDatabase.resetForTesting(context);
        AppDatabase.getInstance(context);
    }

    private static EmployeeForm form(String code, String name, String email, String rate) {
        return new EmployeeForm(code, name, email, "0901234567", rate);
    }

    @Test
    public void add_createsAccountAndEmployee_inOneGo() {
        FormErrors e = repo.add(form("EMP01", "Le Van C", "c@gmail.com", "70000"));
        assertFalse(e.hasErrors());

        Account acc = db.accountDao().getByUsername("emp01");
        assertNotNull(acc);
        assertEquals(Account.ROLE_EMPLOYEE, acc.role);
        Employee emp = db.employeeDao().getByAccountId(acc.id);
        assertNotNull(emp);
        assertEquals("EMP01", emp.code);
        assertEquals(70000d, emp.hourlyRate, 0.001);
        assertTrue(emp.isActive);
    }

    @Test
    public void add_newEmployeeCanLoginWithDefaultPassword() {
        repo.add(form("EMP02", "Pham D", "", "60000"));
        LoginResult r = new AuthService(db)
                .login("emp02", EmployeeRepository.DEFAULT_PASSWORD);
        assertTrue(r.isSuccess());
        assertEquals("EMP02", r.employee.code);
    }

    @Test
    public void add_codeIsStoredUppercase_caseInsensitiveDuplicate() {
        assertFalse(repo.add(form("emp03", "Anh Anh", "", "1000")).hasErrors());
        assertEquals("EMP03", db.employeeDao()
                .getByAccountId(db.accountDao().getByUsername("emp03").id).code);
        assertNotNull(repo.add(form("Emp03", "Bao Bao", "", "1000")).code);
    }

    @Test
    public void add_duplicateCode_isRejected_andNothingInserted() {
        // NV001 la ma da seed san.
        FormErrors e = repo.add(form("NV001", "Trung Ma", "", "1000"));
        assertNotNull(e.code);
        assertNull(db.accountDao().getByUsername("nv001"));
    }

    @Test
    public void add_duplicateEmail_isRejected() {
        FormErrors e = repo.add(form("EMP04", "Trung Mail", "AN.NGUYEN@example.com", "1000"));
        assertNotNull(e.email);
        assertNull(db.accountDao().getByUsername("emp04"));
    }

    @Test
    public void add_codeClashingWithExistingUsername_isRejected() {
        FormErrors e = repo.add(form("ADMIN", "Gia Admin", "", "1000"));
        assertNotNull(e.code);
        assertEquals(Account.ROLE_ADMIN, db.accountDao().getByUsername("admin").role);
    }

    @Test
    public void add_invalidRate_isRejected() {
        assertNotNull(repo.add(form("EMP05", "A", "", "-1")).hourlyRate);
        assertNotNull(repo.add(form("EMP05", "A", "", "0")).hourlyRate);
        assertNull(db.accountDao().getByUsername("emp05"));
    }

    @Test
    public void update_changesFieldsAndRate_keepsCode() {
        Employee e = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv1").id);
        FormErrors errors = repo.update(e.id,
                new EmployeeForm("KHAC", "Ten Moi", "moi@example.com", "", "99000"));
        assertFalse(errors.hasErrors());

        Employee after = db.employeeDao().getById(e.id);
        assertEquals("NV001", after.code);
        assertEquals("Ten Moi", after.fullname);
        assertEquals("moi@example.com", after.email);
        assertNull(after.phone);
        assertEquals(99000d, after.hourlyRate, 0.001);
    }

    @Test
    public void update_keepingOwnEmail_isAllowed() {
        Employee e = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv1").id);
        FormErrors errors = repo.update(e.id,
                new EmployeeForm(e.code, e.fullname, e.email, e.phone, "55000"));
        assertFalse(errors.hasErrors());
    }

    @Test
    public void update_emailOfAnotherEmployee_isRejected() {
        Employee e = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv1").id);
        FormErrors errors = repo.update(e.id,
                new EmployeeForm(e.code, e.fullname, "binh.tran@example.com", "", "55000"));
        assertNotNull(errors.email);
    }

    @Test
    public void update_negativeRate_isRejected_andDataUnchanged() {
        Employee e = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv1").id);
        double before = e.hourlyRate;
        FormErrors errors = repo.update(e.id,
                new EmployeeForm(e.code, e.fullname, e.email, e.phone, "-10"));
        assertNotNull(errors.hourlyRate);
        assertEquals(before, db.employeeDao().getById(e.id).hourlyRate, 0.001);
    }

    @Test
    public void update_unknownEmployee_returnsGeneralError() {
        assertNotNull(repo.update(99999, form("X1", "A", "", "1000")).general);
    }

    @Test
    public void softDelete_keepsRow_blocksLogin_andFreesNothing() {
        Account acc = db.accountDao().getByUsername("nv2");
        Employee e = db.employeeDao().getByAccountId(acc.id);

        repo.softDelete(e.id);

        Employee after = db.employeeDao().getById(e.id);
        assertNotNull("ban ghi van con de giu lich su", after);
        assertFalse(after.isActive);
        assertEquals(LoginResult.Status.ACCOUNT_INACTIVE,
                new AuthService(db).login("nv2", "123456").status);
        // Ma van bi giu cho (UNIQUE), khong cho tao trung.
        assertNotNull(repo.add(form("NV002", "Tao Lai", "", "1000")).code);
    }

    @Test
    public void update_softDeletedEmployee_isRejected() {
        Employee e = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv2").id);
        repo.softDelete(e.id);
        assertNotNull(repo.update(e.id, form("NV002", "Sua Lai", "", "1000")).general);
    }
}
