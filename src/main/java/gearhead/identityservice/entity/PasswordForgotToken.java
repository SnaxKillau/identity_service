package gearhead.identityservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "passwordToken")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordForgotToken {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserCredential user; // Assuming User is your User class
    private String token;
    private LocalDateTime expiryDate;
}
