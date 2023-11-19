package vn.edu.iuh.fit.backend.pks;

import lombok.*;
import vn.edu.iuh.fit.backend.models.Order;
import vn.edu.iuh.fit.backend.models.Product;

import java.io.Serializable;

@Setter @Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailPK implements Serializable {
    private long order;
    private long product;
}
