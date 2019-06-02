# Course

Educational course is one of the core Flaxo concepts.
Each Flaxo course has a backing git repository where all tasks, tests and documentation are stored 
and there most of the magic happen.

## Creation

To create a new educational course follow the instructions below.

1. Go to the courses page.

2. Click on the **Create course** button. Course creation popup will appear.
*Note that if the button is inactive then you should 
[connect your GitHub account](https://flaxo.readthedocs.io/en/latest/getting-started/external-services/#connect-account)
first*.

3. In the appeared course creation popup fill the **Course name**, **Number of tasks** and **Language** fields.
Moreother you can enable 
[environment generation](https://flaxo.readthedocs.io/en/latest/getting-started/course/#environment-generation).
![course-creation-popup](../images/course-creation-popup.png)

4. To create course click on **Create** button in the course creation popup. 
The popup will be closed and after several seconds the course will appear on the courses page along with the 
corresponding notifications.
![created-course-notification](../images/created-course-notification.png)

### Environment generation

> **Experimental** feature.

Flaxo is able not only to a create a course repo but to generate a whole execution environment for a predefined set 
of configurations. Environment generation means creating and uploading all the sources, binaries and configs required 
for building the repository sources and running the tests.

| Language | Testing language | Testing framework | Build tool |
| -------- | ---------------- | ----------------- | ---------- |
| Java | Java / Kotlin | Junit / Spek | Gradle |
| Kotlin | Kotlin | Junit / Spek | Gradle |
| С++ | Bash | Bash IO* | Bash |

\* - Simple Bash IO testing framework for C++ sources. 
It was written specifically for the case and will be replaced with community-driven framework.
An example of using the framework is [here](https://github.com/tcibinan/simple-cpp-project).

To enable environment generation while creating a course click on **Generate environment** checkbox in a course 
creation popup. Fill **Testing language** and **Testing framework** to specify the environment to generate.

![course-creation-popup-environment](../images/course-creation-popup-environment.png)

## Details

Course is a dynamic entity which is hard to track.
Flaxo course dashboard is intended to show the information that requires tutor's attention in the first place.
To view a course details click on the course card on the courses page.

![course-summary](../images/course-summary.jpg)

### State

Course state describes what kind of interactions course is supposed to have at the moment.

![course-state-label](../images/course-state-label.jpg)

#### Init

Course has been just created and tutor is expected to clone the repository and create tasks.

#### Running

Course is ready for students to clone and solve the tasks.
 
### Labels
  
Each course has an associated set of labels which can be divided to either *service* or *technology* type.

#### Service labels

Service labels describes which kind of services current course uses to host and validate its solutions.

![course-integrated-service-labels](../images/course-integrated-service-labels.jpg)

#### Technology labels

Techonology labels describes what languages and frameworks are used in by the course tutor and students.

![course-tech-labels](../images/course-tech-labels.jpg)

## Summary

First tab in the course dashboard is course summary tab.
It contains the overall statistics for each student in the course.
Moreover it contains link to the corresponding git repository and course refresh button.

### Repository link

Course corresponding repository can be accessed via the *Git repository* button.

![course-repository-btn](../images/course-repository-btn.jpg)

### Refresh

Course integrated services validations can be refreshed using refresh button.

![course-refresh-btn](../images/course-refresh-btn.jpg)

### Settings

Course settings contains several configurable course properties such as language, testing language and testing
framework.
Currently, none of them has any effect on anything but course labels.

![course-settings](../images/course-settings.jpg)

Course settings can be accessed using the corresponding button.

![course-settings-btn](../images/course-settings-btn.jpg)
 
### Statistics

The most interesting part of the course summary tab is of course progress statistics table.
It contains scores that each student gained through out the course along with an average overall score.

![course-summary-statistics](../images/course-summary-statistics.jpg)

#### Download

Summary statistics can be downloaded in one of the supported formats using the corresponding button.

![course-download-as-btn](../images/course-download-as-btn.jpg)
