package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import static com.tgb.gsvnbackend.lib.Oath2UtilsConverter.getUserId;
import com.tgb.gsvnbackend.model.entity.Address;
import com.tgb.gsvnbackend.repository.AddressRepository;
import com.tgb.gsvnbackend.service.AddressService;
import com.tgb.gsvnbackend.service.CachingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.util.List;

@Service
public class AddressServiceImp implements AddressService {
    private final AddressRepository addressRepository;
    //private final AddressMapper addressMapper;  // Remove AddressMapper
    private final CachingService cachingService;
    private static  final String CacheKey="address";
    @Autowired
    public AddressServiceImp(AddressRepository addressRepository, CachingService cachingService) { // Removed AddressMapper
        this.addressRepository = addressRepository;
        this.cachingService = cachingService;
    }
    private Address findEntity(int id)
    {
        return addressRepository.findById(id).orElseThrow(()->new NotFoundException("Address with id " + id + " not found."));
    }

    public List<Address> searchByUserId(Principal principal) {
        String userId=getUserId(principal);
        List<Address> addressList = cachingService.getListBySearchValue(CacheKey, userId, Address.class);
        if (addressList != null) {
            return addressList;
        }
        addressList = addressRepository.findByUserId(userId);
        cachingService.saveListBySearchValue(CacheKey, userId, addressList, Address.class);
        return addressList;
    }
    @Transactional
    public Address create(Address address,Principal principal) {
        String userId=getUserId(principal);
        address.setUserId(userId);
        Address savedAddress = addressRepository.save(address);
        cachingService.saveById(CacheKey, savedAddress.getId(), savedAddress, Address.class);
        return savedAddress;
    }

    @Transactional
    public Address update(int id, Address address,Principal principal) {
        Address existingAddress = findEntity(id);
        String userId=getUserId(principal);
        existingAddress.setUserId(userId);
        existingAddress.setReceiver(address.getReceiver());
        existingAddress.setStreet(address.getStreet());
        existingAddress.setCity(address.getCity());
        existingAddress.setState(address.getState());
        existingAddress.setZip(address.getZip());
        existingAddress.setPhone(address.getPhone());
        Address updatedAddress = addressRepository.save(existingAddress);
        cachingService.saveById(CacheKey, id, updatedAddress, Address.class);
        return updatedAddress;
    }

    @Transactional
    public void delete(int id, Principal principal) {
        String userId=getUserId(principal);
        Address address = findEntity(id);
        if(address.getUserId().equals(userId))
        {
            addressRepository.delete(address);
            cachingService.deleteById(CacheKey, id);
        }
        else throw new NotFoundException("Address not found");
    }
}
