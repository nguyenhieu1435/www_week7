package vn.edu.iuh.fit.backend.repositories;

import java.time.LocalDateTime;

public interface IOrderCount {
    LocalDateTime getOrderDate();
    Integer getTotalOrderNumber();
}
