package com.example.javaweb.controller;

import com.example.javaweb.entity.Order;
import com.example.javaweb.entity.OrderItem;
import com.example.javaweb.service.OrderItemService;
import com.example.javaweb.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class OrderController {
    private OrderService orderService;
    private OrderItemService orderItemService;

    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @CrossOrigin
    @GetMapping("/orders")
    public List<Order> findAll(){
        return orderService.findAll();
    }

    @CrossOrigin
    @GetMapping("/orders/{orderID}")
    public Order getOrderByID(@PathVariable int orderID){
        Order order = orderService.findByID(orderID);
        if(order == null) {
            throw new RuntimeException("Order not found");
        }
        return order;
    }

    @CrossOrigin
    @PostMapping("/orders")
    public Order addOrder(@RequestBody Order order){
        //merge: if id ==0 insert else update
        order.setId(0);
        return orderService.save(order);
    }

    @CrossOrigin
    @PutMapping("/orders")
    public Order updateOrder(@RequestBody Order order){
        return orderService.save(order);
    }

    @CrossOrigin
    @DeleteMapping("/orders/{orderID}")
    public String deleteOrder(@PathVariable int orderID){
        Order order = orderService.findByID(orderID);
        if(order == null){
            throw new RuntimeException("Not found order");
        }
        orderService.deleteByID(orderID);
        return "Delete order id " + orderID;
    }

    @CrossOrigin
    @GetMapping("/orders/{orderId}/orderItems")
    public Object getOrderItemOfOrder(@PathVariable int orderId) {
        try {
            Order order = orderService.findByID(orderId);
            if (order != null) {
                Set<OrderItem> orderItems = order.getOrderItems();
                return orderItems;
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ tại đây
            // Ví dụ: ghi log ngoại lệ và trả về thông báo lỗi
            e.printStackTrace();
            return("An error occurred while retrieving order items");
        }
    }


    @CrossOrigin
    @PostMapping("/orders/{orderId}/orderItems")
    public Object createOrderItem(@PathVariable int orderId, @RequestBody OrderItem orderItem) {
        try {
            Order order = orderService.findByID(orderId);
            if (order != null) {
                orderItem.setOrder(order);
                order.getOrderItems().add(orderItem);
                orderItemService.save(orderItem);
                orderService.save(order);
                return orderItem;
            } else {
                return "Order not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while creating the order item";
        }
    }


    @CrossOrigin
    @PutMapping("/orders/{orderId}/orderItems/{orderItemId}")
    public Object updateOrderItem(
            @PathVariable int orderId,
            @PathVariable int orderItemId,
            @RequestBody OrderItem updatedOrderItem
    ) {
        try {
            Order order = orderService.findByID(orderId);
            if (order != null) {
                Optional<OrderItem> optionalOrderItem = order.getOrderItems().stream()
                        .filter(orderItem -> orderItem.getId() == orderItemId)
                        .findFirst();
                if (optionalOrderItem.isPresent()) {
                    OrderItem orderItem = optionalOrderItem.get();
                    // Cập nhật thông tin của orderItem với dữ liệu mới
                    orderItem.setName(updatedOrderItem.getName());
                    orderItem.setImageUrl(updatedOrderItem.getImageUrl());
                    orderItem.setPrice(updatedOrderItem.getPrice());
                    // Lưu orderItem vào CSDL
                    orderItemService.save(orderItem);
                    return orderItem;
                } else {
                    throw new RuntimeException("Order item not found");
                }
            } else {
                throw new RuntimeException("Order not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while updating the order item";
        }
    }

}
