package com.tgb.gsvnbackend.service;
import com.tgb.gsvnbackend.model.entity.Address;
import org.springframework.data.domain.Page;
import java.security.Principal;
import java.util.List;
public interface AddressService {
    List<Address> searchByUserId(Principal principal);
    Address create(Address address, Principal principal);
    Address update(int id, Address address, Principal principal);
    void delete(int id, Principal principal);
}
