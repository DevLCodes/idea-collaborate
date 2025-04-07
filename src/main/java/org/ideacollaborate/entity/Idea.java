package org.ideacollaborate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
public class Idea {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank(message="Idea Title should not be empty")
    private String title;

    @NotBlank(message = "Idea description should not be empty")
    private String description;

    @ElementCollection
    @NotEmpty(message = "Tags should not be empty")
    private List<String> tags = new ArrayList<>();

    @ManyToOne
    private Employee owner;

    @CreationTimestamp
    private Date createdDate;

    private int upvotes;

    private int downvotes;

    @ManyToMany
    @JoinTable(
            name = "idea_interested_users",
            joinColumns = @JoinColumn(name = "idea_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> interestedCollaborators = new ArrayList<>();

}
