package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.dto.projects.*;
import com.mailmak.time_registration_system.repository.CustomerRepository;
import com.mailmak.time_registration_system.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class ProjectService implements ProjectServiceInterface{

    private final ProjectRepository projectRepository;                                                  //only accessible in the ProjectService, ref to ProjectRespository object cannot eb reassigned after being initialized with constructor, Repo interface to interact with DB
    private final CustomerRepository customerRepository;

    @Autowired  //dependency injection of Projectrepository
    public ProjectService(ProjectRepository projectRepository, CustomerRepository customerRepository) {             //constructor for when ProjectService instance is created,creating projectRepository class fields and assigning the value of the params to them
        this.projectRepository = projectRepository;
        this.customerRepository = customerRepository;
    }

    //GET PROJECTS METHOD
    @Override
    public ArrayList<Project> getProjects(GetProjectRequest request) {                //one parameter of name request and type GetProjectRequest. GetProjectrequest defines input criteria for retrieving projects. Returntype LIST
        if (request.getCustomerId() == null) {                                        //If customer ID is not provided, return all projects
            return (ArrayList<Project>) this.projectRepository.findAll();
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID: " + request.getCustomerId() + " cannot be found"));        //Look for customer in DB

        return this.projectRepository.findByCustomer(customer);             //Filtering all projects associated with customer ID. SHOULD IT BE A LIST CONTAINING ALL PROJECTS THO?
    }

    //GET PROJECT BY ID method
    @Override
    public Project getProject(UUID id) {
        //Fetch project
        return this.projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID: " + id + " cannot be found"));
    }



    //CREATE PROJECT METHOD
    @Override
    public Project createProject(CreateProjectRequest request) {               //create project method, responsible for creating a new project for a specific. Input of the customer_id required, as well as details on the project defined in the CreateProjectRequest class
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID: " + request.getCustomerId() + " cannot be found"));                        //look for customer in DB

        Project project = Project.builder()                                                                                   //Create a new project object using the Project.builder() method
                .name(request.getName())                                                                                         //Set the name of the project to the name defined in the CreateProjectRequest object
                .description(request.getDescription())                                                                           //Set the description of the project to the description defined in the CreateProjectRequest object
                .customer(customer)//Set the customer of the project to the customer defined in the CreateProjectRequest object
                .build();                                                                                                        //Build the project object

        return projectRepository.save(project);                                                  //Save it to the project repository that in turn interacts with DB
    }


    //UPDATE PROJECT METHOD
    @Override
    public void updateProject(UpdateProjectRequest request, UUID projectId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID : " + projectId + " cannot be found"));        //Look if project exists using the UpdateProjectRequest getId fields

        if(request.getName()!= null && !request.getName().isBlank()){                                               //Change Name
            project.setName(request.getName());
        }

        project.setDescription(request.getDescription());

        if(request.getCustomerId() != null){                                //Change customer,b y first querying the current customer and then setting the new one
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer with ID: " + request.getCustomerId() + " cannot be found"));
            project.setCustomer(customer);
        }

        this.projectRepository.save(project);
    }


    //DELETE PROJECT METHOD
    @Override
    public void deleteProject(UUID project_id) {
        if(this.projectRepository.findById(project_id).isPresent()){
            this.projectRepository.deleteById(project_id);
        } else {
            throw new RuntimeException("Project with ID : " + project_id + " cannot be found");
        }
    };
}


