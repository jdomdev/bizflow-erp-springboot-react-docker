
package io.sunbit.app.test.payroll;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import io.sunbit.app.dao.IPayrollDao;
import io.sunbit.app.dao.IEmployeeDao;
import io.sunbit.app.dao.IPositionDao;
import io.sunbit.app.entity.Employee;
import io.sunbit.app.entity.Payroll;
import io.sunbit.app.entity.Position;
import io.sunbit.app.util.DateUtil;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PayrollTest {

    @Autowired
    IPayrollDao payrollDao;
    @Autowired
    IEmployeeDao employeeDao;
    @Autowired
    IPositionDao positionDao;

    @Test
    @DisplayName("Test payroll saving")
    @Order(1)
    public void testPayrollSaving() {
        Employee employee = ensureEmployee();
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
    @Order(2)
    public void testPayrollUpdating() {
        Payroll payroll = payrollDao.findAll().stream().findFirst().orElse(null);
        assertThat(payroll).isNotNull();
        payroll.setAmount(2500.0);
        Payroll updatedPayroll = payrollDao.save(payroll);
        assertThat(updatedPayroll.getAmount()).isEqualTo(2500.0);
    }

    @Test
    @DisplayName("Test payroll deleting")
    @Order(3)
    public void testPayrollDeleting() {
        Payroll payroll = payrollDao.findAll().stream().findFirst().orElse(null);
        assertThat(payroll).isNotNull();
        Long id = payroll.getId();
        payrollDao.delete(payroll);
        assertThat(payrollDao.findById(java.util.Objects.requireNonNull(id))).isEmpty();
    }

    @Test
    @DisplayName("Test payroll finding by id")
    @Order(4)
    public void testPayrollFindingById() {
        Payroll payroll = new Payroll();
        payroll.setAmount(3000.0);
        payroll.setPayrollDate(DateUtil.formattingDate(LocalDateTime.now()));
        Employee employee = ensureEmployee();
        payroll.setEmployee(employee);
        Payroll savedPayroll = payrollDao.save(payroll);
        Payroll foundPayroll = payrollDao.findById(java.util.Objects.requireNonNull(savedPayroll.getId())).orElse(null);
        assertThat(foundPayroll).isNotNull();
        assertThat(foundPayroll.getAmount()).isEqualTo(3000.0);
    }

    @Test
    @DisplayName("Test payroll-employee relation")
    @Order(5)
    public void testPayrollEmployeeRelation() {
        Payroll payroll = new Payroll();
        payroll.setAmount(4000.0);
        payroll.setPayrollDate(DateUtil.formattingDate(LocalDateTime.now()));
        Employee employee = ensureEmployee();
        payroll.setEmployee(employee);
        Payroll savedPayroll = payrollDao.save(payroll);
        assertThat(savedPayroll.getEmployee()).isNotNull();
        assertThat(savedPayroll.getEmployee().getId()).isEqualTo(employee.getId());
    }

    private Employee ensureEmployee() {
        return employeeDao.findAll().stream().findFirst().orElseGet(() -> {
            Position position = positionDao.findByNameIgnoreCase("Payroll Analyst")
                    .orElseGet(() -> {
                        Position newPosition = new Position();
                        newPosition.setName("Payroll Analyst");
                        return positionDao.save(newPosition);
                    });
            Employee employee = new Employee(
                "Payroll",
                "Tester",
                DateUtil.formattingDate(LocalDateTime.of(1985, 1, 1, 0, 0, 0)),
                position,
                "payroll.tester@example.com",
                new ArrayList<>());
            return employeeDao.save(employee);
        });
    }
}
