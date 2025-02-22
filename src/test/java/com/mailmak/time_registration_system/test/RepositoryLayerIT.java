package com.mailmak.time_registration_system.test;

import com.mailmak.time_registration_system.TimeRegistrationSystemApplicationTests;
import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.exceptions.CustomerNotFound;
import com.mailmak.time_registration_system.repository.CustomerRepository;
import com.mailmak.time_registration_system.repository.ProjectRepository;

import com.mailmak.time_registration_system.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.Exception;


@SpringBootTest   //starting a test version of our application
@ExtendWith(SpringExtension.class) //extending Junit5
@Import(TimeRegistrationSystemApplicationTests.TestSecurityConfig.class)
//@DataJpaTest //"@DataJpaTest is limited to the JPA repository layer of the application. It doesn’t load the entire application context, which can make testing faster and more focused." IT sets up the H2 DB, and checks for the JPA repositories
@Transactional //clean DB after each test
public class RepositoryLayerIT {

    @Autowired
    private ProjectRepository projectRepository;                                //this repo is connected to the DB. We inject here to interact with the DB
    @Autowired
    private CustomerRepository customerRepository;                               //this repo is connected to the DB. We inject here to interact with the DB

    private Customer dummyCustomer;
    @Autowired
    private CustomerService customerService;

    @BeforeEach                                                                    //@BeforeEach methods run before every test method in the class.
                                                                                        //Set up a dummy customer and save it to the DB - so that we can query project based by customers tooo
        void set(){
            dummyCustomer = Customer.builder()
                            .name("Jack Ma")
                            .build();
            customerRepository.save(dummyCustomer); //save it to DB
        }


        @Test
        public void testCREATE() {                            //Testing the CRUD operations in the DB

            //CREATE a project object with the constructor from the class

            Project createfirstproject = Project.builder()
                    .customer(dummyCustomer)
                    .name("First project - create")
                    .description("This is the first project where we run an integration test on 'create' operation")
                    .build();

                    //SAVE THE PROJECT
            Project saveProject = projectRepository.save(createfirstproject);


                    //Check if exists
            boolean isPresent = projectRepository.existsById(saveProject.getId());
            assertTrue(isPresent, "Project with given ID exists");  //No need for assertFalse. If condition is false the test fails automatically.



        }


        @Test
        public void testREAD() {

            Project readproject = Project.builder()
                    .customer(dummyCustomer)
                    .name("First project - read")
                    .description("This is the first project where we run an integration test on 'read' operation")
                    .build();

            Project saveProject = projectRepository.save(readproject);

            Optional <Project> foundProject = projectRepository.findById(saveProject.getId());
            assertTrue(foundProject.isPresent(), "Project found in the database");

        }


    @Test
    public void testUPDATE() {

        Project updatefirstproject = Project.builder()
                .customer(dummyCustomer)
                .name("First project - update")
                .description("This is the first project where we run an integration test on 'update' operation")
                .build();


        Project saveProject = projectRepository.save(updatefirstproject);
        //Let us update the saved project
        saveProject.setName("Bla bla updated name");

        Project updateProject = projectRepository.save(saveProject);

        Optional <Project> updatedProject = projectRepository.findById(updateProject.getId());
        assertTrue(updatedProject.isPresent(), "Updated project is in the database");

    }


    @Test
    public void testDELETE() {
        Project deleteproject = Project.builder()
                .customer(dummyCustomer)
                .name("First project - delete")
                .description("This is the first project where we run an integration test on 'delete' operation")
                .build();

        Project saveProject = projectRepository.save(deleteproject);
        UUID id = saveProject.getId();

        projectRepository.deleteById(id);

        boolean isPresent = projectRepository.existsById(id);
        assertFalse(isPresent, "Project no longer in database");


    }



    @Test
    public void testCustomerProjectsquery() {

            //Create 3 projects and see they belong to same customer ID

        Project firstproject = Project.builder()
                .customer(dummyCustomer)
                .name("First project - customer relation")
                .description("Can this project be found under the 'dummy customer' customer object")
                .build();
        projectRepository.save(firstproject);

        Project secondproject = Project.builder()
                .customer(dummyCustomer)
                .name("Second project - customer relation")
                .description("Can this project be found under the 'dummy customer' customer object")
                .build();
        projectRepository.save(secondproject);

        Project thirdproject = Project.builder()
                .customer(dummyCustomer)
                .name("Third project - customer relation")
                .description("Can this project be found under the 'dummy customer' customer object")
                .build();
        projectRepository.save(thirdproject);


        //return the three projects belonging to customer
        ArrayList<Project> projectlist = projectRepository.findByCustomer(dummyCustomer);
        assertTrue(projectlist.size() == 3, "The projects linked to customer " + dummyCustomer);


        }

        @Test
        public void testNewCustomerwithnoProjects(){
            Customer newcustomer = Customer.builder()
                    .name("New customer - customer relation")
                    .build();
            customerRepository.save(newcustomer);

            ArrayList<Project> noProjectsyet= projectRepository.findByCustomer(newcustomer);
            assertTrue(noProjectsyet.isEmpty(), "New customer has no projects yet");

        }


        @Test
        public void nonexistenCustomerquery(){
            //Generate a nonexistent ID and check if it exists in the DB
            UUID nonexistingUUID = UUID.randomUUID();

            // JPA repository does not throw an exception if customer is not found by ID, it returns an Optional. We need to check whether that Optional is empty
            Optional<Customer> nonexistencustomer = customerRepository.findById(nonexistingUUID);

            //is the customer present in the DB or not?
            assertTrue(nonexistencustomer.isEmpty(), "Customer not found"); //if Optional container is empty, then it means that the customer doesnt exist.
        }


}
