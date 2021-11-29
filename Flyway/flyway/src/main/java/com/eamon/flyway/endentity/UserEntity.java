package com.eamon.flyway.endentity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author eamonzzz
 * @date 2021-11-28 20:59
 */
@Getter
@Setter
@Entity
@Table(name = "user_entity")
@DynamicInsert
@DynamicUpdate
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String test1;

}
