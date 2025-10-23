package in.neelesh.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.neelesh.common.entity.Address;
import in.neelesh.common.entity.User;
import in.neelesh.common.enums.AddressType;

@Repository
public interface AddressRepository extends JpaRepository<Address, String>{

	Optional<Address> findByUserAndType(User user, AddressType type);
	
	List<Address> findByUser(User user);
}
