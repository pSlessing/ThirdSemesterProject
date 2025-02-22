package com.mailmak.time_registration_system.classes;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity //mapping class to a table in DB
@Data //generating getters, setters
@Builder
@AllArgsConstructor //generating constructor with all fields
@NoArgsConstructor //for every class no arg constructor. Useful for creating object without setting their fields.
@Table( //config. to the DB tables
        name = "projects", //table name in the DB
        uniqueConstraints = {
                @UniqueConstraint(name = "project_name_unique_for_customer", columnNames = {"name", "customer_id"}) //project name must be unique for each customer
        }
)
public class Project {
    @Id //primary key of Project entity.
    @GeneratedValue(strategy = GenerationType.AUTO) //generating unique values for this field (i.e. UUID).
    private UUID id;

    @Column( //Every field will be a column
            name = "name",
            nullable = false, //field mandatory!
            columnDefinition = "text" //datatype = "text"
    )
    private String name;

    @Column(
            name = "description",
            columnDefinition = "text"
    )

    private String description;

    public Project(String name, String description, Customer customer) { //constructor to initialize project obj.
        this.name = name;
        this.description = description;
        this.customer = customer;
    }

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER) // many project => one customer
    private Customer customer;

    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true) //one project => many tasks
    private List<Task> tasks;
}
