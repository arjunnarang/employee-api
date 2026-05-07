package com.example.employeeapi.serviceimpl;

import com.example.employeeapi.dto.EmployeeRequestDto;
import com.example.employeeapi.dto.EmployeeResponseDto;
import com.example.employeeapi.dto.PagedResponse;
import com.example.employeeapi.entity.Employee;
import com.example.employeeapi.entity.Employee.EmployeeStatus;
import com.example.employeeapi.exception.DuplicateResourceException;
import com.example.employeeapi.exception.ResourceNotFoundException;
import com.example.employeeapi.repository.EmployeeRepository;
import com.example.employeeapi.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Override
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        log.info("Creating employee with email: {}", requestDto.getEmail());

        if (employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException(
                "Employee", "email", requestDto.getEmail()
            );
        }

        Employee employee = modelMapper.map(requestDto, Employee.class);
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", saved.getId());
        return modelMapper.map(saved, EmployeeResponseDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        Employee employee = findEmployeeById(id);
        return modelMapper.map(employee, EmployeeResponseDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto getEmployeeByEmail(String email) {
        log.info("Fetching employee with email: {}", email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
        return modelMapper.map(employee, EmployeeResponseDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponseDto> getAllEmployees(
            int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employee> employeePage = employeeRepository.findAll(pageable);
        return buildPagedResponse(employeePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponseDto> getEmployeesByDepartment(
            String department, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<Employee> employeePage = employeeRepository.findByDepartment(department, pageable);
        return buildPagedResponse(employeePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponseDto> getEmployeesByStatus(
            EmployeeStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Employee> employeePage = employeeRepository.findByStatus(status, pageable);
        return buildPagedResponse(employeePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponseDto> searchEmployees(
            String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = employeeRepository.searchEmployees(keyword, pageable);
        return buildPagedResponse(employeePage);
    }

    @Override
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto requestDto) {
        log.info("Updating employee with ID: {}", id);

        Employee existing = findEmployeeById(id);

        // Check email uniqueness if changed
        if (!existing.getEmail().equalsIgnoreCase(requestDto.getEmail())
                && employeeRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", requestDto.getEmail());
        }

        modelMapper.map(requestDto, existing);
        existing.setId(id); // Ensure ID is not overwritten

        Employee updated = employeeRepository.save(existing);
        log.info("Employee updated successfully: {}", id);
        return modelMapper.map(updated, EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto patchEmployeeStatus(Long id, EmployeeStatus status) {
        log.info("Updating status of employee {} to {}", id, status);
        Employee employee = findEmployeeById(id);
        employee.setStatus(status);
        Employee updated = employeeRepository.save(employee);
        return modelMapper.map(updated, EmployeeResponseDto.class);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        Employee employee = findEmployeeById(id);
        employeeRepository.delete(employee);
        log.info("Employee deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllDepartments() {
        return employeeRepository.findAllDepartments();
    }

    @Override
    @Transactional(readOnly = true)
    public long getEmployeeCountByDepartment(String department) {
        return employeeRepository.countByDepartment(department);
    }

    // ---- Helper methods ----

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    private PagedResponse<EmployeeResponseDto> buildPagedResponse(Page<Employee> page) {
        List<EmployeeResponseDto> content = page.getContent().stream()
                .map(e -> modelMapper.map(e, EmployeeResponseDto.class))
                .collect(Collectors.toList());

        return PagedResponse.<EmployeeResponseDto>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
