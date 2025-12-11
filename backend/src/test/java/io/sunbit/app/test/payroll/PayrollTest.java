package io.sunbit.app.test.payroll;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
// ...existing code...

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.dao.IPayrollDao;
import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.util.DateUtil;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class PayrollTest {

    @Autowired
    IPayrollDao payrollDao;
    @Autowired
    IEmployeeDao employeeDao;

    @Test
    @DisplayName("Test payroll saving")
    public void testPayrollSaving() {
        Employee employee = employeeDao.findAll().stream().findFirst().orElse(null);
        Payroll payroll = new Payroll();
        payroll.setAmount(2000.0);
        payroll.setPayrollDate(DateUtil.formattingDate(LocalDateTime.now()));
        payroll.setEmployee(employee);
        Payroll savedPayroll = payrollDao.save(payroll);
        assertThat(savedPayroll).isNotNull();
        assertThat(savedPayroll.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test payroll updating")
    public void testPayrollUpdating() {
        Payroll payroll = payrollDao.findAll().stream().findFirst().orElse(null);
        assertThat(payroll).isNotNull();
        payroll.setAmount(2500.0);
        Payroll updatedPayroll = payrollDao.save(payroll);
        assertThat(updatedPayroll.getAmount()).isEqualTo(2500.0);
    }

    @Test
    @DisplayName("Test payroll deleting")
    public void testPayrollDeleting() {
        Payroll payroll = payrollDao.findAll().stream().findFirst().orElse(null);
        assertThat(payroll).isNotNull();
        Long id = payroll.getId();
        payrollDao.delete(payroll);
        assertThat(payrollDao.findById(java.util.Objects.requireNonNull(id))).isEmpty();
    }

    @Test
    @DisplayName("Test payroll finding by id")
    public void testPayrollFindingById() {
        Payroll payroll = new Payroll();
        payroll.setAmount(3000.0);
        payroll.setPayrollDate(DateUtil.formattingDate(LocalDateTime.now()));
        Employee employee = employeeDao.findAll().stream().findFirst().orElse(null);
        payroll.setEmployee(employee);
        Payroll savedPayroll = payrollDao.save(payroll);
        Payroll foundPayroll = payrollDao.findById(java.util.Objects.requireNonNull(savedPayroll.getId())).orElse(null);
        assertThat(foundPayroll).isNotNull();
        assertThat(foundPayroll.getAmount()).isEqualTo(3000.0);
    }

    @Test
    @DisplayName("Test payroll-employee relation")
    public void testPayrollEmployeeRelation() {
        Payroll payroll = new Payroll();
        payroll.setAmount(4000.0);
        payroll.setPayrollDate(DateUtil.formattingDate(LocalDateTime.now()));
        Employee employee = employeeDao.findAll().stream().findFirst().orElse(null);
        payroll.setEmployee(employee);
        Payroll savedPayroll = payrollDao.save(payroll);
        assertThat(savedPayroll.getEmployee()).isNotNull();
        assertThat(savedPayroll.getEmployee().getId()).isEqualTo(employee.getId());
    }
}
