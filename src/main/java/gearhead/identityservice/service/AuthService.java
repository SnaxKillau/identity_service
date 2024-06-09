package gearhead.identityservice.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gearhead.identityservice.dto.UserResponse;
import gearhead.identityservice.entity.PasswordForgotToken;
import gearhead.identityservice.entity.Role;
import gearhead.identityservice.entity.UserCredential;
import gearhead.identityservice.repository.PasswordForgotRepository;
import gearhead.identityservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Base64;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordForgotRepository passwordForgotRepository;
    public String saveUser(UserCredential credential) {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }

    public String generateToken(String username , Role role) {
        return jwtService.generateToken(username , role);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
    public Integer getCurrentUser(String token){
        String username = jwtService.extractUsername(token);
        Optional<UserCredential> userCredential = repository.findByName(username);
        if(userCredential.isPresent()){
            return userCredential.get().getId();
        }
        return null ;
    }

    public String encodeUserId(Integer userId) {
        return Base64.getEncoder().encodeToString(userId.toString().getBytes());
    }

    public Integer decodeUserId(String encodedUserId) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedUserId);
        String decodedString = new String(decodedBytes);
        return Integer.parseInt(decodedString);
    }

    public UserResponse getUser(Integer id){
        Optional<UserCredential> userCredential = repository.findById(id);
        if(userCredential.isPresent()){
            UserResponse userResponse = new UserResponse();
            userResponse.setEmail(userCredential.get().getEmail());
            userResponse.setName(userCredential.get().getName());
            return userResponse;
        }
        return null;
    }
    public String resetPassword(String token , String password){
        PasswordForgotToken passwordResetToken = passwordForgotRepository.findByToken(token).orElse(null);
        if(passwordResetToken != null){
            int comparison = LocalDateTime.now().compareTo(passwordResetToken.getExpiryDate());
            if(comparison <= 0){
                UserCredential user = passwordResetToken.getUser();
                ObjectMapper objectMapper = new ObjectMapper();
                try{
                    JsonNode jsonNode = objectMapper.readTree(password);
                    String restPassword = jsonNode.get("password").asText();
                    user.setId(user.getId());
                    user.setName(user.getName());
                    user.setRole(user.getRole());
                    user.setEmail(user.getEmail());
                    user.setPassword(passwordEncoder.encode(restPassword));
                    repository.save(user);
                    passwordForgotRepository.delete(passwordResetToken);
                    return "Successfully";
                }
                catch (Exception e){
                    return "Password is invalid";
                }

            }
            else{
                return "Reset Token is expired";
            }
        }
        return "Reset Token is invalid";
    }

}
