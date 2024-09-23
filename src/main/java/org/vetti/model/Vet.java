package org.vetti.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "vets")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Vet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cuit;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "varchar(255) default '0'")
    private String role;
}
