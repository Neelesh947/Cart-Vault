package in.neelesh.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity{

	private static final long serialVersionUID = 1L;

	private String name;

    @OneToMany(mappedBy = "brand")
    private List<Product> products = new ArrayList<>();
    
    public Brand(String id) {
    	this.setId(id);
    }
}
