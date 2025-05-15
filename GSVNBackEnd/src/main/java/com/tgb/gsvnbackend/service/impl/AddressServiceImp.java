package com.tgb.gsvnbackend.service.impl;

import com.tgb.gsvnbackend.exc.NotFoundException;
import static com.tgb.gsvnbackend.lib.Oath2UtilsConverter.getUserId;
import com.tgb.gsvnbackend.model.entity.Address;
import com.tgb.gsvnbackend.repository.jpaRepository.AddressRepository;
import com.tgb.gsvnbackend.service.AddressService;
import com.tgb.gsvnbackend.service.CachingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
public class AddressServiceImp implements AddressService {
    private final AddressRepository addressRepository;
    //private final AddressMapper addressMapper;  // Remove AddressMapper
    private final CachingService cachingService;
    private static  final String CacheKey="address";
    @Autowired
    public AddressServiceImp(AddressRepository addressRepository, CachingService cachingService) { // Removed AddressMapper
        this.addressRepository = addressRepository;
        this.cachingService = cachingService;
        log.info("AddressServiceImp initialized.");
    }
    private Address findEntity(int id)
    {
        log.info("Finding address with ID: {}", id);
        return addressRepository.findById(id).orElseThrow(()->{
            log.error("Address with ID {} not found.", id);
            return new NotFoundException("Address with id " + id + " not found.");
        });
    }

    public List<Address> searchByUserId(Principal principal) {
        String userId=getUserId(principal);
        log.info("Searching addresses for user ID: {}", userId);
        List<Address> addressList = cachingService.getListBySearchValue(CacheKey, userId, Address.class);
        if (addressList != null) {
            log.info("Addresses found in cache for user ID: {}", userId);
            return addressList;
        }
        log.info("Addresses not found in cache for user ID: {}. Fetching from database.", userId);
        addressList = addressRepository.findByUserId(userId);
        cachingService.saveListBySearchValue(CacheKey, userId, addressList, Address.class);
        log.info("Addresses for user ID {} saved to cache.", userId);
        return addressList;
    }
    @Transactional
    public Address create(Address address,Principal principal) {
        String userId=getUserId(principal);
        log.info("Creating a new address for user ID: {}", userId);
        address.setUserId(userId);
        Address savedAddress = addressRepository.save(address);
        cachingService.saveById(CacheKey, savedAddress.getId(), savedAddress, Address.class);
        log.info("New address created with ID {} for user ID {} and saved to cache.", savedAddress.getId(), userId);
        return savedAddress;
    }

    @Transactional
    public Address update(int id, Address address,Principal principal) {
        log.info("Updating address with ID: {}", id);
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
        log.info("Address with ID {} updated for user ID {} and saved to cache.", id, userId);
        return updatedAddress;
    }

    @Transactional
    public void delete(int id, Principal principal) {
        String userId=getUserId(principal);
        log.info("Deleting address with ID: {}", id);
        Address address = findEntity(id);
        if(address.getUserId().equals(userId))
        {
            addressRepository.delete(address);
            cachingService.deleteById(CacheKey, id);
            log.info("Address with ID {} deleted for user ID {}.", id, userId);
        }
        else {
            log.warn("Attempted to delete address with ID {} by user ID {} who is not the owner.", id, userId);
            throw new NotFoundException("Address not found");
        }
    }
}