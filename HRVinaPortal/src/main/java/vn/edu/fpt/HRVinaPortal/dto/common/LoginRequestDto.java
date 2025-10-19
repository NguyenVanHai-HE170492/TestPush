package vn.edu.fpt.HRVinaPortal.dto.common;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String companyEmail;
    private String password;
}