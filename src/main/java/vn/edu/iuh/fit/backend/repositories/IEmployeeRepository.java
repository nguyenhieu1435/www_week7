package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.iuh.fit.backend.enums.EmployeeStatus;
import vn.edu.iuh.fit.backend.models.Employee;

import java.util.List;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> {
    public List<Employee> findAllByStatusNot(EmployeeStatus employeeStatus);
}