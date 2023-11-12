package vn.edu.iuh.fit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.backend.models.ProductImage;
import vn.edu.iuh.fit.backend.repositories.IProductImageRepository;

@Service
public class ProductImageService {
    private IProductImageRepository productImageRepository;
    @Autowired
    public ProductImageService(IProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }
    public Page<ProductImage> findAll(int pageNo, int sizeNo, String sortBy, String sortDirection){
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, sizeNo, sort);
        return productImageRepository.findAll(pageable);
    }
}
