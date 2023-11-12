package vn.edu.iuh.fit.frontend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.backend.enums.EmployeeStatus;
import vn.edu.iuh.fit.backend.models.Employee;
import vn.edu.iuh.fit.backend.repositories.IEmployeeRepository;
import vn.edu.iuh.fit.backend.services.EmployeeService;

import java.util.List;

@Controller
@RequestMapping("/admin/employees")
@AllArgsConstructor
public class EmployeeController {
    private IEmployeeRepository employeeRepository;
    private EmployeeService employeeService;

    @GetMapping("")
    public String getAll(Model model){
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        return "admin/employees/crudEmployee";
    }
    @GetMapping("/add")
    public ModelAndView openAddEmployeePage(){
        ModelAndView modelAndView = new ModelAndView();
        Employee employee = new Employee();
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("statusList", EmployeeStatus.values());
        modelAndView.setViewName("admin/employees/addEmployee");
        return modelAndView;
    }
    @PostMapping("/add")
    public String handleAddEmployee(@ModelAttribute("employee") Employee employee, Model model){
        employee.setStatus(EmployeeStatus.ACTIVE);
        try {
            employeeRepository.save(employee);

        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errAddEmpl", "Email đã tồn tại!");
            return "admin/employees/addEmployee";
        }
        return "redirect:/admin/employees";
    }
    @GetMapping("/delete/{id}")
    public String handleDeleteEmployee(@PathVariable("id") long employeeID){
        employeeRepository.deleteById(employeeID);
        return "redirect:/admin/employees";
    }
}
