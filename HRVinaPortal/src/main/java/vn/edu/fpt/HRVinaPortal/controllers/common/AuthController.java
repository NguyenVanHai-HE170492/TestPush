package vn.edu.fpt.HRVinaPortal.controllers.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.HRVinaPortal.dto.common.LoginRequestDto;
import vn.edu.fpt.HRVinaPortal.services.impl.common.JwtService;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        // 1. Xác thực người dùng bằng email và password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCompanyEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Nếu xác thực thành công, tải thông tin user và tạo token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getCompanyEmail());
        final String jwt = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst() // Lấy quyền đầu tiên
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN"); // Giá trị mặc định nếu không có quyền

        // 4. Trả về một đối tượng chứa cả token và role
        return ResponseEntity.ok(Map.of(
                "token", jwt,
                "role", role
        ));
    }
}