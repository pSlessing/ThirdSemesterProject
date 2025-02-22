package com.mailmak.time_registration_system.dto.projects;  //File: Defining the CreateProjectRequest DTO

import com.mailmak.time_registration_system.classes.Customer;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CreateProjectRequest {                         //public = accessible in other parts of the hierarchy-3
    private String name;                                        //making fields private to avoid unintended bugs, easier to modify later
    private String description;
    private UUID customerId;
}

//purpose of file: creating DTO layer of the app. Helps with getting data from API request (when manager wants to create a Project in this case)