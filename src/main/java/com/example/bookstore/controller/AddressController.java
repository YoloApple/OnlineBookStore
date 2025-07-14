package com.example.bookstore.controller;

import com.example.bookstore.entity.Address;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.AddressRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addAddress(@RequestBody Address address, Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        address.setUser(user);
        addressRepository.save(address);
        return ResponseEntity.ok("Đã thêm địa chỉ thành công");
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public List<Address> getUserAddresses(Authentication auth) {
        return addressRepository.findByUserUsername(auth.getName());
    }
}

