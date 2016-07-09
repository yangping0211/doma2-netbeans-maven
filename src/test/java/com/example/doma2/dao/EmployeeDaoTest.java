package com.example.doma2.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.doma2.AppConfig;
import com.example.doma2.DbResource;
import com.example.doma2.entity.Employee;
import org.junit.Rule;
import org.junit.Test;
import org.seasar.doma.jdbc.tx.TransactionManager;

import java.util.List;

public class EmployeeDaoTest {

    @Rule
    public final DbResource dbResource = new DbResource();

    private final EmployeeDao dao = new EmployeeDaoImpl();

    @Test
    public void testSelectById() {
        TransactionManager tm = AppConfig.singleton().getTransactionManager();
        tm.required(() -> {
            Employee employee = dao.selectById(1);
            assertNotNull(employee);
            assertEquals("ALLEN", employee.name);
            assertEquals(Integer.valueOf(30), employee.age);
            assertEquals(Integer.valueOf(0), employee.version);
        });
    }

    @Test
    public void testSelectByAge() {
        TransactionManager tm = AppConfig.singleton().getTransactionManager();
        tm.required(() -> {
            List<Employee> employees = dao.selectByAge(35);
            assertEquals(2, employees.size());
        });
    }

    @Test
    public void testInsert() {
        TransactionManager tm = AppConfig.singleton().getTransactionManager();

        Employee employee = new Employee();

        // 最初のトランザクション
        // 挿入を実行している
        tm.required(() -> {
            employee.name = "HOGE";
            employee.age = 20;
            dao.insert(employee);
            assertNotNull(employee.id);
        });

        // 2番目のトランザクション
        // 挿入が成功していることを確認している
        tm.required(() -> {
            Employee employee2 = dao.selectById(employee.id);
            assertEquals("HOGE", employee2.name);
            assertEquals(Integer.valueOf(20), employee2.age);
            assertEquals(Integer.valueOf(1), employee2.version);
        });
    }

    @Test
    public void testUpdate() {
        TransactionManager tm = AppConfig.singleton().getTransactionManager();

        // 最初のトランザクション
        // 検索して age フィールドを更新している
        tm.required(() -> {
            Employee employee = dao.selectById(1);
            assertEquals("ALLEN", employee.name);
            assertEquals(Integer.valueOf(30), employee.age);
            assertEquals(Integer.valueOf(0), employee.version);
            employee.age = 50;
            dao.update(employee);
            assertEquals(Integer.valueOf(1), employee.version);
        });

        // 2番目のトランザクション
        // 更新が成功していることを確認している
        tm.required(() -> {
            Employee employee = dao.selectById(1);
            assertEquals("ALLEN", employee.name);
            assertEquals(Integer.valueOf(50), employee.age);
            assertEquals(Integer.valueOf(1), employee.version);
        });
    }

    @Test
    public void testDelete() {
        TransactionManager tm = AppConfig.singleton().getTransactionManager();

        // 最初のトランザクション
        // 削除を実行している
        tm.required(() -> {
            Employee employee = dao.selectById(1);
            dao.delete(employee);
        });

        // 2番目のトランザクション
        // 削除が成功していることを確認している
        tm.required(() -> {
            Employee employee = dao.selectById(1);
            assertNull(employee);
        });
    }
}