package vn.edu.iuh.fit.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.backend.models.ProductPrice;
import vn.edu.iuh.fit.backend.repositories.IProductPriceRepository;

@Service
public class ProductPriceService {
    private IProductPriceRepository productPriceRepository;
    @Autowired
    public ProductPriceService(IProductPriceRepository productPriceRepository) {
        this.productPriceRepository = productPriceRepository;
    }
    public Page<ProductPrice> findAll(int pageNo, int sizeNo, String sortBy, String sortDirection){
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNo, sizeNo, sort);
        return productPriceRepository.findAll(pageable);
    }
}
