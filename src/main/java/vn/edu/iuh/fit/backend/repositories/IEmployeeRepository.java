package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.backend.models.Employee;

public interface IEmployeeRepository extends JpaRepository<Employee, Long> {
}