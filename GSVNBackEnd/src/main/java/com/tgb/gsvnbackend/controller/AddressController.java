package com.tgb.gsvnbackend.controller;

import com.tgb.gsvnbackend.model.entity.Address;
import com.tgb.gsvnbackend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Address>> searchByUserId(Principal principal) {
        List<Address> addresses = addressService.searchByUserId(principal);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/create")
    public ResponseEntity<Address> createAddress(@RequestBody Address address, Principal principal) {
        Address createdAddress = addressService.create(address, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable int id, @RequestBody Address address, Principal principal) {
        Address updatedAddress = addressService.update(id, address, principal);
        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable int id, Principal principal) {
        addressService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }
}
 