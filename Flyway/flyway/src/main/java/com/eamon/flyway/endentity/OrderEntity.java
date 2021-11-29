package com.eamon.flyway.endentity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author eamonzzz
 * @date 2021-11-28 21:32
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Table(name = "order_entity")
@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, columnDefinition = "varchar(255)")
    private String type;

    @Column(name = "description", nullable = false, columnDefinition = "varchar(255)")
    private String description;

}
