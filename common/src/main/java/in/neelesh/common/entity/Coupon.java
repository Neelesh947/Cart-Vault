package in.neelesh.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity{

	private static final long serialVersionUID = 1L;

	private String code;

    private String description;

    private BigDecimal discountAmount;

    private Float discountPercent;

    private Instant validFrom;

    private Instant validUntil;

    private Boolean active;

    @OneToMany(mappedBy = "coupon")
    private List<CouponUsage> couponUsages = new ArrayList<>();
}
