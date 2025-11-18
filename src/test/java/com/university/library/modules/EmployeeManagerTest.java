package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeManagerTest {
    private EmployeeManager employeeManager;

    @BeforeEach
    void setUp() {
        employeeManager = new EmployeeManager();
    }

    @Test
    void testAddEmployee() {
        EmployeeManager.Employee employee = employeeManager.addEmployee("emp1", "pass1");
        assertNotNull(employee);
        assertEquals("emp1", employee.username());
        assertEquals("pass1", employee.password());
    }

    @Test
    void testAddEmployeeDuplicateUsername() {
        employeeManager.addEmployee("emp1", "pass1");
        EmployeeManager.Employee employee2 = employeeManager.addEmployee("emp1", "pass2");
        assertNull(employee2);
    }

    @Test
    void testLoginSuccess() {
        employeeManager.addEmployee("emp1", "pass1");
        EmployeeManager.Employee result = employeeManager.login("emp1", "pass1");
        assertNotNull(result);
        assertEquals("emp1", result.username());
    }

    @Test
    void testLoginFailureWrongPassword() {
        employeeManager.addEmployee("emp1", "pass1");
        EmployeeManager.Employee result = employeeManager.login("emp1", "wrong");
        assertNull(result);
    }

    @Test
    void testLoginFailureWrongUsername() {
        employeeManager.addEmployee("emp1", "pass1");
        EmployeeManager.Employee result = employeeManager.login("wrong", "pass1");
        assertNull(result);
    }

    @Test
    void testGetEmployeeById() {
        EmployeeManager.Employee employee = employeeManager.addEmployee("emp1", "pass1");
        EmployeeManager.Employee found = employeeManager.getEmployeeById(employee.id());
        assertNotNull(found);
        assertEquals(employee.id(), found.id());
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        EmployeeManager.Employee found = employeeManager.getEmployeeById(999);
        assertNull(found);
    }

    @Test
    void testGetAllEmployees() {
        employeeManager.addEmployee("emp1", "pass1");
        employeeManager.addEmployee("emp2", "pass2");
        assertEquals(2, employeeManager.getAllEmployees().size());
    }

    @Test
    void testGetCurrentUsername() {
        EmployeeManager.Employee employee = employeeManager.addEmployee("emp1", "pass1");
        String username = employeeManager.getCurrentUsername(employee);
        assertEquals("emp1", username);
    }

    @Test
    void testGetCurrentUsernameWithNull() {
        String username = employeeManager.getCurrentUsername(null);
        assertEquals("", username);
    }

    @Test
    void testGetCurrentUsernameWithWrongType() {
        String username = employeeManager.getCurrentUsername("not an employee");
        assertEquals("", username);
    }
}

