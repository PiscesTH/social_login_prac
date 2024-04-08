package com.sociallogin.sociallogin.entity;

import com.sociallogin.sociallogin.Enum.ProviderTypeEnum;
import com.sociallogin.sociallogin.Enum.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table
public class User {
    @Id
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long iuser;

    @Column(name = "provider_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'LOCAL'")
    private ProviderTypeEnum providerType;

    @NotNull
    @Column(length = 16)
    private String uid;

    @NotNull
    @Column(length = 16)
    private String upw;

    @NotNull
    @Column(length = 8)
    private String name;

    @Column(length = 1000)
    private String pic;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault("'USER'")
    private RoleEnum role;
}
