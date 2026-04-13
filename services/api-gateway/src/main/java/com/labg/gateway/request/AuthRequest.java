package com.labg.gateway.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
