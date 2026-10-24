package com.loader.thuc_hanh_2_android.employee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EmployeeValidatorTest {

    private static EmployeeForm form(String code, String name, String email, String phone,
                                     String rate) {
        return new EmployeeForm(code, name, email, phone, rate);
    }

    @Test
    public void validForm_hasNoErrors() {
        FormErrors e = EmployeeValidator.validate(
                form("EMP01", "Nguyen Van A", "a@gmail.com", "0901234567", "50000"));
        assertFalse(e.hasErrors());
    }

    @Test
    public void emailAndPhoneAreOptional() {
        FormErrors e = EmployeeValidator.validate(form("EMP01", "Nguyen Van A", "", "", "50000"));
        assertFalse(e.hasErrors());
    }

    @Test
    public void emptyCode_isError() {
        assertNotNull(EmployeeValidator.validate(form("", "Ten", "", "", "1")).code);
        assertNotNull(EmployeeValidator.validate(form("   ", "Ten", "", "", "1")).code);
    }

    @Test
    public void invalidCodeFormat_isError() {
        assertNotNull(EmployeeValidator.validate(form("A", "Ten", "", "", "1")).code);
        assertNotNull(EmployeeValidator.validate(form("EM P", "Ten", "", "", "1")).code);
        assertNotNull(EmployeeValidator.validate(form("EMP@1", "Ten", "", "", "1")).code);
        assertNull(EmployeeValidator.validate(form("EMP_01-a", "Ten", "", "", "1")).code);
    }

    @Test
    public void emptyName_isError() {
        assertNotNull(EmployeeValidator.validate(form("EMP01", "  ", "", "", "1")).fullname);
    }

    @Test
    public void invalidEmail_isError() {
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "abc", "", "1")).email);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "a@b", "", "1")).email);
        assertNull(EmployeeValidator.validate(form("EMP01", "Ten", "a@b.vn", "", "1")).email);
    }

    @Test
    public void invalidPhone_isError() {
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "123", "1")).phone);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "09a1234567", "1")).phone);
        assertNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "+84901234567", "1")).phone);
    }

    @Test
    public void hourlyRate_mustBePositiveNumber() {
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "")).hourlyRate);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "0")).hourlyRate);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "-5")).hourlyRate);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "abc")).hourlyRate);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "NaN")).hourlyRate);
        assertNotNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "1e12")).hourlyRate);
        assertNull(EmployeeValidator.validate(form("EMP01", "Ten", "", "", "0.5")).hourlyRate);
    }

    @Test
    public void parseHourlyRate_returnsValue() {
        assertEquals(50000d, EmployeeValidator.parseHourlyRate(" 50000 "), 0.0001);
        assertNull(EmployeeValidator.parseHourlyRate(null));
    }

    @Test
    public void formTrimsInput() {
        EmployeeForm f = form("  EMP01 ", " Ten ", null, null, " 1 ");
        assertEquals("EMP01", f.code);
        assertEquals("Ten", f.fullname);
        assertEquals("", f.email);
        assertTrue(EmployeeValidator.parseHourlyRate(f.hourlyRateText) > 0);
    }
}
