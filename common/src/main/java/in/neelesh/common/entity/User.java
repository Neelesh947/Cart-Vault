package in.neelesh.common.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	private String phone;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Address> addresses = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Order> orders = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Cart> carts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<CouponUsage> couponUsages = new ArrayList<>();

	@OneToMany(mappedBy = "createdBy")
	private List<Product> createdProducts = new ArrayList<>();
}
