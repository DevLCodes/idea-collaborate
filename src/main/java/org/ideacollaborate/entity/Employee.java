package org.ideacollaborate.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Employee( Long id,String name ){
        this.name = name;
        this.id = id;
    }
}
