package com.example.bookstore.service;

import com.example.bookstore.dto.JwtResponse;
import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class AuthService {
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private EmailService emailService;
    public JwtResponse authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return new JwtResponse(jwt, userDetails.getUsername(), roles);
    }

    public String registerUser(RegisterRequest signUpRequest){
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return "Tên người dùng đã tồn tại!";
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        // ✅ Tạo Cart và liên kết với User
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        user.setCart(cart);

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null){
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(userRole);
        }else {
            for (String role : strRoles) {
                switch (role) {
                    case "admin":
                        roles.add(roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                        break;
                    case "seller":
                        roles.add(roleRepository.findByName(Role.ERole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                        break;
                    default:
                        roles.add(roleRepository.findByName(Role.ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found.")));
                }
            }
        }
        user.setRoles(roles);
        userRepository.save(user);
        return "Đăng ký thành công!";
    }
    public void sendResetCodeToEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
        user.setResetToken(otp);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOrderConfirmation(user.getEmail(), "Mã đặt lại mật khẩu",
                "Mã xác thực để đặt lại mật khẩu là: " + otp + "\nMã có hiệu lực trong 10 phút.");
    }

    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (user.getResetToken() == null || user.getTokenExpiration() == null) {
            throw new RuntimeException("Bạn chưa yêu cầu mã xác thực");
        }

        if (!user.getResetToken().equals(otp)) {
            throw new RuntimeException("Mã xác thực không đúng");
        }

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã xác thực đã hết hạn");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);
    }

}
