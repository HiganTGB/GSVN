package com.tgb.gsvnbackend.repository.jpaRepository;



import com.tgb.gsvnbackend.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserId(String userId);
}
