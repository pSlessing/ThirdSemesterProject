# Project Title: Work session registration system

This project helps our case company with the time/shift registration of its employees. It serves as a "punch-clock" application. 

# Contact 

Kasper V. - [@kasperverner](https://github.com/kasperverner) - mail@kasperverner.com

Project Link: [https://github.com/cs-24-sw-03-12/project](https://github.com/cs-24-sw-03-12/project)

## About the project:

- This software is meant to help our case company help comply with the new EU Directive from 2024, regarding manadatory tracking of worked hours. It also enables better project management and communication amongs employees.
- The program is coded in Java using the Springboot framework, as well as JUnit5  
- A list of all dependencies can be found in the /pom.xml file 
- /Dockerfile is used to build a docker image to ensure platform independence

## How to use it:
- Sign in with your account credentials to "https://syncr.dev"

## Project structure
- `/main`
  - `/java/com/mailmak/time_registration_system`
      - `/classes`
      - `/configurations`
      - `/controller`
      - `/dto`
      - `/exceptions`
      - `/mappers`
      - `/repository`
      - `/scheduled tasks`
      - `/security`
      - `/service`
      - `/specification`
      - `/TimeRegistrationSystemApplication.java`
  - `/resources`
- `/test`
  - `/java/com/mailmak/time_registration_system`
      -`/test`
      -`/TimeRegistrationSystemApplication.java`
  - `/resources`

This project uses a layered architecture. The test folder starts up a test version of the springboot application and contains usability test cases as well as integration test cases. The 'main' folder contains the service layer, the dto layer and the controller layer along with the classes.

### Naming

- **File naming**: `camelCase`
- **Variable naming**: `camelCase`
- **Function naming**: `camelCase`
- **Component naming**: `PascalCase`
- **Constants**: `UPPER_CASE`
- **Folder naming**: `camelCase`

## Git
Branch naming: feature/<feature-name>, fix/<bug-name>
Commit message: feature: <feature-name> added, fix: <bug-name>
Pull request title: Feature: <feature-name>, Fix: <bug-name>
Pull request description: Describe the changes made in the pull request

***IMPORTANT***
Always create a new branch for your changes and open a pull request for approval before merging your changes into the main branch.

Never push directly to the main branch.

## Start contributing

### Clone the repository

```bash
cd <path-to-your-projects-folder>
git clone https://github.com/kasperverner/e-vote.git
```

### Create a new branch

The main branch is protected, so you need to create a new branch for your changes. The branch name should be `feature/<feature-name>` or `fix/<bug-name>`. You can create a new branch with the following command:

```bash
git checkout -b feature/<feature-name>
```

### Commit your changes

Commit your changes with the following command:

```bash
git add .
git commit -m "feature: <feature-name> added"
```

### Merge the main branch

Before you push your changes, you need to merge the main branch into your branch to avoid conflicts.

```bash
git switch main
git pull
git switch feature/<feature-name>
git merge main
```

### Push your changes

Push your changes to the remote repository with the following command:

```bash
git push origin feature/<feature-name>
```

### Create a pull request

Open a pull request on GitHub and describe the changes made in the pull request.

Assign a reviewer to the pull request for approval to have your changes merged into the main branch.

