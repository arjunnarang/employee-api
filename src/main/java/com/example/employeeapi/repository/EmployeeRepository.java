package com.example.employeeapi.repository;

import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.entity.Employee.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Employee> findByDepartment(String department, Pageable pageable);

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    List<Employee> findByDepartmentAndStatus(String department, EmployeeStatus status);

    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.designation) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department")
    List<String> findAllDepartments();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department = :department")
    long countByDepartment(@Param("department") String department);
}
