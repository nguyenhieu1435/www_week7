package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import vn.edu.iuh.fit.backend.models.Customer;

public interface ICustomerRepository extends CrudRepository<Customer, Long>
        , PagingAndSortingRepository<Customer, Long> {

    
}
