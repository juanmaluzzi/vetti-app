package org.vetti.model.request;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "varchar(255) default '0'")
    private String role = "0";

    @Column(unique = true, nullable = false)
    private String dni;

    @Column
    private String address;

    @Column
    private String district;

    @Column
    @OneToMany(mappedBy = "userRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PetRequest> petRequests = new ArrayList<>();

    public void setPetRequests(List<PetRequest> petRequests) {
        this.petRequests = petRequests != null ? petRequests : new ArrayList<>();
        this.petRequests.forEach(pet -> pet.setUserRequest(this));
    }
}
