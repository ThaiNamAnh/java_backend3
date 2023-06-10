package com.example.javaweb.controller;

import com.example.javaweb.entity.Customer;
import com.example.javaweb.entity.Order;
import com.example.javaweb.service.CustomerService;
import com.example.javaweb.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CustomerController {
    private CustomerService customerService;
    private OrderService orderService;

    public CustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }
    @CrossOrigin
    @GetMapping("/customers")
    public List<Customer> findAll(){
        return customerService.findAll();
    }
    @CrossOrigin
    @GetMapping("/customers/{customerID}")
    public Customer getCustomerByID(@PathVariable int customerID){
        Customer customer = customerService.findByID(customerID);
        if(customer == null) {
            throw new RuntimeException("Customer not found");
        }
        return customer;
    }

    @CrossOrigin
    @PostMapping("/customers")
    public Customer addCustomer(@RequestBody Customer customer){
        //merge: if id ==0 insert else update
        customer.setId(0);
        return customerService.save(customer);
    }
    @CrossOrigin
    @PutMapping("/customers")
    public Customer updateCustomer(@RequestBody Customer customer){
        return customerService.save(customer);
    }

    @CrossOrigin
    @DeleteMapping("/customers/{customerID}")
    public String deleteCustomer(@PathVariable int customerID){
        Customer customer = customerService.findByID(customerID);
        if(customer == null){
            throw new RuntimeException("Not found customer");
        }
        customerService.deleteByID(customerID);
        return "Delete customer ID " + customerID;
    }

    @CrossOrigin
    @GetMapping("/customers/{customerId}/orders")
    public Object getCustomerOrders(@PathVariable int customerId) {
        try {
            Customer customer = customerService.findByID(customerId);
            if (customer != null) {
                Set<Order> customerOrders = customer.getOrders();
                return customerOrders;
            } else {
                return "Không tìm thấy khách hàng.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi trong quá trình xử lý.";
        }
    }


    @CrossOrigin
    @PostMapping("/customers/{customerId}/orders")
    public Object createOrder(@PathVariable int customerId, @RequestBody Order order) {
        try {
            Customer customer = customerService.findByID(customerId);
            if (customer != null) {
                order.setId(0);
                order.setCustomer(customer);
                customer.getOrders().add(order);
                orderService.save(order);
                customerService.save(customer);
                return order;
            } else {
                return "Không tìm thấy khách hàng.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi trong quá trình xử lý.";
        }
    }


    @CrossOrigin
    @PutMapping("/customers/{customerId}/orders/{orderId}")
    public Object updateOrder(
            @PathVariable int customerId,
            @PathVariable int orderId,
            @RequestBody Order updatedOrder
    ) {
        try {
            Optional<Customer> optionalCustomer = Optional.ofNullable(customerService.findByID(customerId));
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                Optional<Order> optionalOrder = customer.getOrders().stream()
                        .filter(order -> order.getId() == (orderId))
                        .findFirst();
                if (optionalOrder.isPresent()) {
                    Order order = optionalOrder.get();
                    // Cập nhật thông tin của đơn hàng với dữ liệu mới
                    order.setTotalQuantity(updatedOrder.getTotalQuantity());
                    order.setTotalPrice(updatedOrder.getTotalPrice());
                    order.setDateCreated(updatedOrder.getDateCreated());
                    // Lưu đơn hàng vào CSDL
                    orderService.save(order);
                    return order;
                } else {
                    return "Không tìm thấy đơn hàng.";
                }
            } else {
                return "Không tìm thấy khách hàng.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Đã xảy ra lỗi trong quá trình xử lý.";
        }
    }
}
