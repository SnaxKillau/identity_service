package gearhead.identityservice.controller;



import gearhead.identityservice.dto.AuthRequest;
import gearhead.identityservice.entity.UserCredential;
import gearhead.identityservice.repository.UserCredentialRepository;
import gearhead.identityservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService service;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential user) {
        return service.saveUser(user);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            UserCredential userCredential = userCredentialRepository.findByName(authRequest.getUsername()).orElseThrow(() ->  new RuntimeException("invalid access"));
            System.out.println(userCredential.getRole());
                return service.generateToken(authRequest.getUsername() , userCredential.getRole());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        try{
            service.validateToken(token);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED , "Token is invalid");
        }

        return "Token is valid";
    }
    @GetMapping("/currentUser")
    public String getCurrentUser(@RequestParam("token") String token){
        Integer id = service.getCurrentUser(token);
        return service.encodeUserId(id);
    }
    @GetMapping("/userDetail")
    public Integer getUserDetail(@RequestParam("id") String id){
        Integer decodeUserId = service.decodeUserId(id);
        return decodeUserId;
    }
}
