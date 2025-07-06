package com.example.carsellingshop.Services;

import com.example.carsellingshop.Model.Order;
import com.example.carsellingshop.Repositories.OrderRepository;

import java.util.List;

public class OrderService {
    private OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public long placeOrder(Order order) {
        if (order.getUserId() <= 0 || order.getCarId() <= 0 || order.getOrderDate() == null ||
                order.getStatus() == null) {
            throw new IllegalArgumentException("Valid user ID, car ID, date, and status are required");
        }
        return orderRepository.insertOrder(order);
    }

    public List<Order> getOrdersByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return orderRepository.getOrdersByUserId(userId);
    }

    public Order getOrderById(int id) {
        Order order = orderRepository.getOrderById(id);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        return order;
    }

    public void updateOrder(Order order) {
        if (order.getId() <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        orderRepository.updateOrder(order);
    }

    public void cancelOrder(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }
        Order order = orderRepository.getOrderById(id);
        if (order != null) {
            order.setStatus("cancelled");
            orderRepository.updateOrder(order);
        }
    }
}