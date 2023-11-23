package vn.edu.iuh.fit.frontend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.iuh.fit.backend.models.Employee;
import vn.edu.iuh.fit.backend.repositories.IEmployeeRepository;
import vn.edu.iuh.fit.backend.repositories.IOrderCount;
import vn.edu.iuh.fit.backend.repositories.IOrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminControllers {
    private IOrderRepository orderRepository;
    private IEmployeeRepository employeeRepository;
    @GetMapping("")
    public String showAdminPage(){
        return "/admin/admin-page";
    }
    @GetMapping("/statictisOrderByDay")
    public String getStatisticOrderByDay(Model model){
        HashMap<LocalDateTime, Integer> statictis = new HashMap<>();
        List<IOrderCount> orderStatistic = orderRepository.statictisOrderByDay();
        for (IOrderCount i : orderStatistic){

            statictis.put(i.getOrderDate(), i.getTotalOrderNumber());
        }

        model.addAttribute("labels", new ArrayList<>(statictis.keySet()));
        model.addAttribute("data", new ArrayList<>(statictis.values()));
        return "admin/statictis/statisticByDay";
    }
    @GetMapping("/statisticTimeSpace")
    public String getstatisticTimeSpace(){
        return "admin/statictis/statictisTimeSpace";
    }
    @PostMapping("/postStatisticTimeSpace")
    public String statisticTimeSpace(Model model, @RequestParam("beginDate") LocalDateTime beginDate,
         @RequestParam("endDate") LocalDateTime endDate){
        if (beginDate.isAfter(endDate)){
            model.addAttribute("errForm", "Ngày bắt đầu phải trước ngày hiện tại!");
            return "admin/statictis/statictisTimeSpace";
        }
        double totalPrice = orderRepository.getTotalPriceByBeginDateAndEndDate(beginDate, endDate);
        model.addAttribute("totalPrice", totalPrice);
        return "admin/statictis/statictisTimeSpace";
    }
    @GetMapping("/statisticByEmployeeID")
    public String getStatisticByEmployeeID(){
        return "admin/statictis/statictisByEmployeeID";
    }
    @PostMapping("/postStatisticByEmployee")
    public String postStatisticByEmployee(Model model, @RequestParam("employeeID") long employeeID){
        Employee employee = employeeRepository.findById(employeeID).orElse(null);
        if (employee == null){
            model.addAttribute("errForm", "Employee ID không tồn tại");
            return "admin/statictis/statictisByEmployeeID";
        }
        double totalPrice = orderRepository.getTotalPriceByEmployeeID(employeeID);
        model.addAttribute("totalPrice", totalPrice);
        return "admin/statictis/statictisByEmployeeID";
    }
}
