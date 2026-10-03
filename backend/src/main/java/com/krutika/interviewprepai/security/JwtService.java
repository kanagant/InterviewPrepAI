package com.krutika.interviewprepai.security;

import com.krutika.interviewprepai.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final MACSigner signer;
    private final MACVerifier verifier;

    public JwtService(JwtProperties properties) throws JOSEException {
        this.properties = properties;
        byte[] key = properties.secret().getBytes(StandardCharsets.UTF_8);
        this.signer = new MACSigner(key);
        this.verifier = new MACVerifier(key);
    }

    public String issueToken(User user) {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(properties.expirationMinutes(), ChronoUnit.MINUTES)))
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
        return jwt.serialize();
    }

    public Optional<AuthenticatedUser> parseToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier)) {
                return Optional.empty();
            }
            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())) {
                return Optional.empty();
            }
            return Optional.of(new AuthenticatedUser(
                    Long.valueOf(claims.getSubject()),
                    claims.getStringClaim("email"),
                    claims.getStringClaim("name")));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
