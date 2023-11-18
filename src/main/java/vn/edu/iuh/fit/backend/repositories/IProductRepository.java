package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.backend.enums.ProductStatus;
import vn.edu.iuh.fit.backend.models.Product;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Long> {
    public List<Product> findAllByStatusNot(ProductStatus productStatus);
}