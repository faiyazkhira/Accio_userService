package com.accio.userService.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.accio.userService.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtAuthenticationHelper {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long JWT_TOKEN_VALIDITY;

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 * Retrieves the username from the JWT token.
	 *
	 * @param token the JWT token
	 * @return the username contained in the token
	 */
	public String getUsernameFromToken(String token) {
		logger.info("Extracting username from token");
		Claims claims = getClaimsFromToken(token);
		return claims.getSubject();
	}

	/**
	 * Retrieves the claims from the JWT token.
	 *
	 * @param token the JWT token
	 * @return the claims contained in the token
	 */
	private Claims getClaimsFromToken(String token) {
		logger.info("Extracting Claims from token");
		Claims claims = Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody();
		return claims;
	}

	/**
	 * Checks whether the JWT token has expired.
	 *
	 * @param token the JWT token
	 * @return true if the token has expired, otherwise false
	 */
	public Boolean isTokenExpired(String token) {
		logger.info("Validating token expiry");
		Claims claims = getClaimsFromToken(token);
		Date expDate = claims.getExpiration();
		return expDate.before(new Date());
	}

	/**
	 * Generates a JWT token based on the provided user details.
	 *
	 * @param userDetails the user details
	 * @return the generated JWT token
	 */
	public String generateToken(UserDetails userDetails) {
		logger.info("Generating token");
		Map<String, Object> claims = new HashMap<>();
		User user = (User) userDetails;
		String email = user.getEmail();
		claims.put("email", email);

		return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName()),
						SignatureAlgorithm.HS512)
				.compact();
	}
}
