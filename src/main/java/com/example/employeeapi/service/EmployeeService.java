package com.example.employeeapi.service;

import com.example.employeeapi.dto.EmployeeRequestDto;
import com.example.employeeapi.dto.EmployeeResponseDto;
import com.example.employeeapi.dto.PagedResponse;
import com.example.employeeapi.entity.Employee.EmployeeStatus;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto);

    EmployeeResponseDto getEmployeeById(Long id);

    EmployeeResponseDto getEmployeeByEmail(String email);

    PagedResponse<EmployeeResponseDto> getAllEmployees(int page, int size, String sortBy, String sortDir);

    PagedResponse<EmployeeResponseDto> getEmployeesByDepartment(String department, int page, int size);

    PagedResponse<EmployeeResponseDto> getEmployeesByStatus(EmployeeStatus status, int page, int size);

    PagedResponse<EmployeeResponseDto> searchEmployees(String keyword, int page, int size);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto);

    EmployeeResponseDto patchEmployeeStatus(Long id, EmployeeStatus status);

    void deleteEmployee(Long id);

    List<String> getAllDepartments();

    long getEmployeeCountByDepartment(String department);
}
