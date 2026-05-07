package com.example.employeeapi.controller;

import com.example.employeeapi.dto.ApiResponse;
import com.example.employeeapi.dto.EmployeeRequestDto;
import com.example.employeeapi.dto.EmployeeResponseDto;
import com.example.employeeapi.dto.PagedResponse;
import com.example.employeeapi.entity.Employee.EmployeeStatus;
import com.example.employeeapi.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Create a new employee [ADMIN, HR]")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(
            @Valid @RequestBody EmployeeRequestDto requestDto) {
        EmployeeResponseDto created = employeeService.createEmployee(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Employee fetched", employeeService.getEmployeeById(id)));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get employee by email")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success("Employee fetched", employeeService.getEmployeeByEmail(email)));
    }

    @GetMapping
    @Operation(summary = "Get all employees (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getAllEmployees(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.success("Employees fetched",
                employeeService.getAllEmployees(page, size, sortBy, sortDir)));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Employees fetched",
                employeeService.getEmployeesByDepartment(department, page, size)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get employees by status")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getByStatus(
            @PathVariable EmployeeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Employees fetched",
                employeeService.getEmployeesByStatus(status, page, size)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by keyword")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Search results fetched",
                employeeService.searchEmployees(keyword, page, size)));
    }

    @GetMapping("/departments")
    @Operation(summary = "Get all unique departments")
    public ResponseEntity<ApiResponse<List<String>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success("Departments fetched", employeeService.getAllDepartments()));
    }

    @GetMapping("/department/{department}/count")
    @Operation(summary = "Get employee count for a department")
    public ResponseEntity<ApiResponse<Long>> getCountByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(ApiResponse.success("Count fetched",
                employeeService.getEmployeeCountByDepartment(department)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update an employee [ADMIN, HR]")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success("Employee updated",
                employeeService.updateEmployee(id, requestDto)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @Operation(summary = "Update employee status [ADMIN, HR]")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateStatus(
            @PathVariable Long id, @RequestParam EmployeeStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                employeeService.patchEmployeeStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an employee [ADMIN only]")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted", null));
    }
}
