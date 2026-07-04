package com.siddu.auth.entity;

import com.siddu.auth.Enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;



@Entity
@Table(
        name = "roles",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, updatable = false)
        private RoleName name;  // fixed master data

        @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
        @Builder.Default
        private Set<UserRoleEntity> users = new HashSet<>();
}

