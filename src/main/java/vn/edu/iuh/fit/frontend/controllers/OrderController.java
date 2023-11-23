package vn.edu.iuh.fit.frontend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.backend.dto.DTOAddNewOrder;
import vn.edu.iuh.fit.backend.dto.DTOOrderList;
import vn.edu.iuh.fit.backend.models.*;
import vn.edu.iuh.fit.backend.repositories.*;
import vn.edu.iuh.fit.backend.services.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/orders")
@AllArgsConstructor
public class OrderController {
    private IOrderRepository orderRepository;
    private OrderService orderService;
    private ICustomerRepository customerRepository;
    private IEmployeeRepository employeeRepository;
    private IProductRepository productRepository;
    private IProductPriceRepository productPriceRepository;
    private IOrderDetailRepository orderDetailRepository;

    @GetMapping("")
    public ModelAndView handleOpenOrderListPage(ModelAndView modelAndView
    , @RequestParam("pageNo")Optional<Integer> pageNo, @RequestParam("pageSize") Optional<Integer> pageSize){
        int pageNoFinal = pageNo.orElse(1);
        int pageSizeFinal = pageSize.orElse(5);
        Page<Order> orderPage = orderService.findAll(pageNoFinal-1, pageSizeFinal
                , "orderDate", "desc");

        List<DTOOrderList> orderLists = new ArrayList<>();
        for(Order order : orderPage.getContent()){
            double totalPrices = order.getOrderDetails().stream().mapToDouble(OrderDetail::getPrice).sum();
            orderLists.add(new DTOOrderList(order, totalPrices));
        }
        modelAndView.addObject("orderLists", orderLists);
        modelAndView.addObject("sizePage", orderPage.getSize());
        modelAndView.addObject("totalPages", orderPage.getTotalPages());
        modelAndView.addObject("numberPages", orderPage.getNumber());
        int totalPages = orderPage.getTotalPages();
        if (totalPages > 0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("orderPageNumbers", pageNumbers);
        }
        modelAndView.setViewName("admin/orders/crudOrder");
        return modelAndView;
    }
    @GetMapping("/add")
    public ModelAndView handleOpenAddOrder(){
        ModelAndView modelAndView = new ModelAndView();
        Order order = new Order();
        OrderDetail orderDetail = new OrderDetail();
        DTOAddNewOrder addNewOrder =  new DTOAddNewOrder(order, orderDetail);
        modelAndView.addObject("addNewOrderDTO", addNewOrder);
        modelAndView.setViewName("admin/orders/addOrder");
        return modelAndView;
    }
    @PostMapping("/add")
    public String handleAddOrder(@ModelAttribute("addNewOrderDTO") DTOAddNewOrder dtoAddNewOrder, Model model){
        Customer customer = customerRepository.findById(dtoAddNewOrder.getOrder()
                .getCustomer().getId()).orElse(null);
        Employee employee = employeeRepository.findById(dtoAddNewOrder.getOrder()
                .getEmployee().getId()).orElse(null);
        Product product = productRepository.findById(dtoAddNewOrder.getOrderDetail()
                .getProduct().getProduct_id()).orElse(null);
        if (employee == null || customer == null || product == null){
            model.addAttribute("errAddOrder", "CustomerID hoặc EmployeeID hoặc ProductID không tồn tại!");
            return "admin/orders/addOrder";
        }
        Order order = dtoAddNewOrder.getOrder();
        orderRepository.save(order);
        ProductPrice productPrice = productPriceRepository
                .findProductPriceNewestByProductID(product.getProduct_id()).orElse(null);
        OrderDetail orderDetail = dtoAddNewOrder.getOrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setPrice(productPrice.getPrice());
        orderDetailRepository.save(orderDetail);
        return "redirect:/admin/orders";
    }
    @GetMapping("/edit/{id}")
    public ModelAndView handleOpenEditOrder(@PathVariable("id") long orderID){
        ModelAndView modelAndView = new ModelAndView();
        Order order = orderRepository.findById(orderID).orElse(null);
        if (order == null){
            return new ModelAndView("redirect:/admin/orders");
        }
        modelAndView.addObject("order", order);
        modelAndView.setViewName("admin/orders/editOrder");
        return modelAndView;
    }
    @PostMapping("/edit")
    public String handleEditOrder(@ModelAttribute("order") Order order, Model model){
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }
    @GetMapping("/delete/{id}")
    public String handleDeleteOrder(@PathVariable("id") long orderID){
        orderRepository.deleteById(orderID);
        return "redirect:/admin/orders";
    }

}
