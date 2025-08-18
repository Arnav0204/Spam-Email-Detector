package org.example.Controller;


import org.example.Helper.JwtHelper;
import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${app.secret-key}")
    private String secretKey;
    @Autowired
    private UserRepository _repository;
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User requestUser){
        if(!ValidateUser(requestUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        User registeredUser = this._repository.findByUsername(requestUser.getUsername());

        if(authenticateUser(requestUser,registeredUser)){
            String jwt = GenerateJwtToken(registeredUser);

            Map<String, String> response = new HashMap<>();
            response.put("token", jwt);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return null;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user){
        if(!ValidateUser(user)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if(CheckUserExists(user)){
            return ResponseEntity.status(409).body(null);
        }
        user.setPassword(hashPassword(user.getPassword()));
        User savedUser= this._repository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/isvalid")
    public ResponseEntity<Object> isvalid(@RequestHeader("Authorization") String authHeader){
        String token = authHeader
                .replaceFirst("(?i)^Bearer\\s+", "") // case-insensitive, removes "Bearer " at start
                .trim();


        boolean isValid = JwtHelper.IsValidToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);

        return ResponseEntity.ok(response);
    }

    private boolean ValidateUser(User user){
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return false;
        }
        if(user.getUsername()==null || user.getUsername().isEmpty()){
            return false;
        }
        if(user.getPassword()==null|| user.getPassword().isEmpty()){
            return false;
        }
        return true;
    }

    private boolean CheckUserExists(User user){
       User registeredUser =null;
       registeredUser= this._repository.findByUsername(user.getUsername());
        return registeredUser!=null;
    }

    private boolean authenticateUser(User requestUser,User registeredUser){
        String requestUserPassword = requestUser.getPassword();
        String registeredUserPassword = registeredUser.getPassword();
        if(registeredUserPassword.equals(hashPassword(requestUserPassword))){
            return true;
        }
        return false;
    }

    private String hashPassword(String password) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] hashedBytes = mac.doFinal(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private String GenerateJwtToken(User user) {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        return Jwts.builder()
                .claim("userId", user.getUserId())
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(key)
                .compact();
    }
}
