# Concepts

The main concepts of the Flaxo platform are **course**, **task** and **solution**. 
Each one of them are described in details later in the chapter.

*Course* is a basic educational unit that consists of several independent *tasks*. Each *task* can have a number of 
preconfigured validations to test corresponding *solutions* which are submitted by the *course*'s students.

## Git

Flaxo uses Git version control system as a backend for all the concept entities - *courses*, *tasks* and *solutions*.

### Course 

Course is a public **git repository** that tutor owns. 
All the data including description, documentation, configurations has to be stored in the corresponding git repository. 
Course's students should create a fork of the course git repository to add their solutions to.

### Task

Task is a **specific branch** in a course git repository. 
Tutor fills a task branch will all required scripts, build configurations and tests to validate the upcoming student 
solutions.

### Solution

Solution is a **pull request** created by a course student. 
A single solution should be created for a single task. 
The pull request *target branch* should be a task branch of the original course git repository and 
its *source branch* should be a task branch of a course repository fork created by a student.
