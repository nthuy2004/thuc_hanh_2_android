package com.loader.thuc_hanh_2_android.salary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.loader.thuc_hanh_2_android.data.AppDatabase;
import com.loader.thuc_hanh_2_android.data.entity.Employee;
import com.loader.thuc_hanh_2_android.data.entity.Salary;
import com.loader.thuc_hanh_2_android.data.entity.WorkLog;
import com.loader.thuc_hanh_2_android.data.repository.SalaryRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SalaryRepositoryTest {

    private Context context;
    private AppDatabase db;
    private SalaryRepository repo;
    private Employee an;   // nv1, 50.000 d/h
    private Employee binh; // nv2, 60.000 d/h

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        AppDatabase.resetForTesting(context);
        db = AppDatabase.getInstance(context);
        repo = new SalaryRepository(context);
        an = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv1").id);
        binh = db.employeeDao().getByAccountId(db.accountDao().getByUsername("nv2").id);
    }

    @After
    public void tearDown() {
        AppDatabase.resetForTesting(context);
        AppDatabase.getInstance(context);
    }

    private void log(Employee e, String date, double hours, String status) {
        db.workLogDao().insert(new WorkLog(e.id, date, "08:00:00", "17:00:00", hours, status));
    }

    @Test
    public void generate_computesHoursOvertimeAndSalary() {
        log(an, "2026-09-01", 8, WorkLog.STATUS_COMPLETED);
        log(an, "2026-09-02", 10, WorkLog.STATUS_COMPLETED); // OT 2h

        assertEquals(2, repo.generateForMonth(9, 2026));

        Salary s = db.salaryDao().getByEmployeeAndPeriod(an.id, 9, 2026);
        assertNotNull(s);
        assertEquals(18d, s.totalHours, 0.001);
        assertEquals(2d, s.overtimeHours, 0.001);
        // (18 - 2) * 50.000 + 2 * 50.000 * 1,5
        assertEquals(950000d, s.totalSalary, 0.01);
        assertEquals(Salary.STATUS_UNPAID, s.status);
    }

    @Test
    public void generate_ignoresIncompleteLogs() {
        log(an, "2026-09-01", 8, WorkLog.STATUS_COMPLETED);
        log(an, "2026-09-02", 9, WorkLog.STATUS_INCOMPLETE);
        log(an, "2026-09-03", 7, WorkLog.STATUS_WORKING);

        repo.generateForMonth(9, 2026);

        Salary s = db.salaryDao().getByEmployeeAndPeriod(an.id, 9, 2026);
        assertEquals(8d, s.totalHours, 0.001);
        assertEquals(0d, s.overtimeHours, 0.001);
        assertEquals(400000d, s.totalSalary, 0.01);
    }

    @Test
    public void generate_onlyCountsLogsOfTheRequestedMonth() {
        log(an, "2026-08-31", 8, WorkLog.STATUS_COMPLETED);
        log(an, "2026-09-01", 8, WorkLog.STATUS_COMPLETED);
        log(an, "2026-10-01", 8, WorkLog.STATUS_COMPLETED);

        repo.generateForMonth(9, 2026);

        assertEquals(8d, db.salaryDao().getByEmployeeAndPeriod(an.id, 9, 2026).totalHours, 0.001);
    }

    @Test
    public void generate_monthWithoutLogs_givesZeroSalary_noCrash() {
        assertEquals(2, repo.generateForMonth(1, 2026));
        for (Salary s : repo.getSalaries(1, 2026)) {
            assertEquals(0d, s.totalHours, 0.001);
            assertEquals(0d, s.totalSalary, 0.001);
        }
    }

    @Test
    public void generateAgain_doesNotDuplicate_andUpdatesValues() {
        log(an, "2026-09-01", 8, WorkLog.STATUS_COMPLETED);
        repo.generateForMonth(9, 2026);
        log(an, "2026-09-02", 8, WorkLog.STATUS_COMPLETED);
        repo.generateForMonth(9, 2026);

        List<Salary> list = repo.getSalaries(9, 2026);
        assertEquals(2, list.size()); // moi nhan vien dung mot ban ghi
        assertEquals(16d, db.salaryDao().getByEmployeeAndPeriod(an.id, 9, 2026).totalHours, 0.001);
    }

    @Test
    public void markPaid_setsStatus_andSurvivesRegenerate() {
        repo.generateForMonth(9, 2026);
        Salary s = db.salaryDao().getByEmployeeAndPeriod(an.id, 9, 2026);

        assertTrue(repo.markPaid(s.id));
        assertEquals(Salary.STATUS_PAID, db.salaryDao().getById(s.id).status);

        repo.generateForMonth(9, 2026);
        assertEquals(Salary.STATUS_PAID, db.salaryDao().getById(s.id).status);
    }

    @Test
    public void markPaid_unknownId_returnsFalse() {
        assertFalse(repo.markPaid(99999));
    }

    @Test
    public void employeeSalaries_onlyContainOwnRows_newestFirst() {
        repo.generateForMonth(8, 2026);
        repo.generateForMonth(9, 2026);

        List<Salary> mine = repo.getEmployeeSalaries(an.id);
        assertEquals(2, mine.size());
        for (Salary s : mine) {
            assertEquals(an.id, s.employeeId);
        }
        assertEquals(9, mine.get(0).month);
        assertTrue(repo.getEmployeeSalaries(99999).isEmpty());
    }

    @Test
    public void softDeletedEmployee_isSkippedWhenGenerating() {
        db.employeeDao().softDelete(binh.id);

        assertEquals(1, repo.generateForMonth(9, 2026));
        assertNull(db.salaryDao().getByEmployeeAndPeriod(binh.id, 9, 2026));
    }

    @Test
    public void invalidPeriod_isRejected() {
        try {
            repo.generateForMonth(13, 2026);
            fail("thang 13 phai bi tu choi");
        } catch (IllegalArgumentException expected) {
            // ok
        }
        try {
            repo.getSalaries(0, 2026);
            fail("thang 0 phai bi tu choi");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }
}
