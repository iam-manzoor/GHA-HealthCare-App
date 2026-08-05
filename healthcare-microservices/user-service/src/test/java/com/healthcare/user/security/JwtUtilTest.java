package com.healthcare.user.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

class JwtUtilTest {
    @Test
    void generatedTokenContainsUsernameAndRole() {
        JwtUtil jwtUtil = new JwtUtil();

        String token = jwtUtil.generateToken("doctor1", "DOCTOR");
        Claims claims = jwtUtil.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("doctor1");
        assertThat(claims.get("role", String.class)).isEqualTo("DOCTOR");
    }
}
