import '../styles/style.css';
import React from 'react';
import ReactDOM from 'react-dom';
import {
    Alert,
    Badge,
    Button,
    ControlLabel,
    FormControl,
    FormGroup,
    HelpBlock,
    Label,
    Modal,
    Panel
} from "react-bootstrap";
import Immutable from 'immutable';
import {credentials} from "../scripts";
import {Api} from "../Api";

export {CoursesList}

class CoursesList extends React.Component {

    constructor(props) {
        super(props);

        this.updateCoursesList = this.updateCoursesList.bind(this);

        this.state = {
            account: props.account,
            courses: []
        };

        this.updateCoursesList();
    }

    render() {
        const courses = Immutable.List(this.state.courses)
            .map(value => <Course data={value}/>);

        return (
            <section className="courses-list">
                <CourseCreation onCourseCreation={this.updateCoursesList}/>
                {courses.size > 0 ? courses : <p>There are no courses yet.</p>}
            </section>
        )
    }

    updateCoursesList() {
        Api.retrieveCourses(
            credentials(),
            this.props.account.nickname,
            courses => {
                this.setState({courses: courses});
            },
            response => {
                console.log('courses retrieving failed');
                console.log(response);
            }
        );
    }

}

class Course extends React.Component {

    constructor(props) {
        super(props);

        this.state = props.data;
    }

    render() {
        const statusLabel = <Label bsStyle="primary">{this.state.status}</Label>;

        const techLabels = Immutable.Set([this.state.language, this.state.testingLanguage, this.state.testingFramework])
            .map(value => <Label bsStyle="info">{value}</Label>);

        return (
            <Panel>
                <Panel.Heading>
                    {this.state.name}
                    <Badge>{this.state.tasks.length} tasks</Badge>
                    <Badge>{this.state.students.length} students</Badge>
                </Panel.Heading>
                <Panel.Body>
                    <p>Some ambiguous course description.</p>
                    {statusLabel}
                    {techLabels}
                </Panel.Body>
            </Panel>
        )
    }
}

class CourseCreation extends React.Component {

    constructor(props) {
        super(props);

        this.handleShow = this.handleShow.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleCourseNameChange = this.handleCourseNameChange.bind(this);
        this.handleLanguageChange = this.handleLanguageChange.bind(this);
        this.handleTestingLanguageChange = this.handleTestingLanguageChange.bind(this);
        this.handleTestingFrameworkChange = this.handleTestingFrameworkChange.bind(this);
        this.handleNumberOfTasksChange = this.handleNumberOfTasksChange.bind(this);
        this.createCourse = this.createCourse.bind(this);

        this.state = {
            show: false,
            courseName: null,
            language: null,
            testLanguage: null,
            testingFramework: null,
            numberOfTasks: null
        }
    }

    render() {
        const supportedLanguages = [<option value="java">java</option>, <option value="kotlin">kotlin</option>];
        const supportedTestingFrameworks = [<option value="junit">junit</option>, <option value="spek">spek</option>];

        return (
            <div>
                <Button onClick={this.handleShow}>
                    Create course
                </Button>
                <Modal show={this.state.show} onHide={this.handleClose}>
                    <form>
                        <Modal.Header>
                            <Modal.Title>Create course from scratch</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <FormGroup>
                                <ControlLabel>Course name</ControlLabel>
                                <FormControl
                                    type="text"
                                    placeholder="Course name"
                                    onChange={this.handleCourseNameChange}/>
                                <HelpBlock>Course name should be a valid git repository name</HelpBlock>
                            </FormGroup>
                            <FormGroup>
                                <ControlLabel>language</ControlLabel>
                                <FormControl
                                    componentClass="select"
                                    placeholder="language"
                                    onChange={this.handleLanguageChange}>
                                    {supportedLanguages}
                                </FormControl>
                                <HelpBlock>Language solutions will be written on</HelpBlock>
                            </FormGroup>
                            <FormGroup>
                                <ControlLabel>testing language</ControlLabel>
                                <FormControl
                                    componentClass="select"
                                    placeholder="testing language"
                                    onChange={this.handleTestingLanguageChange}>
                                    {supportedLanguages}
                                </FormControl>
                                <HelpBlock>Language tests will be written on</HelpBlock>
                            </FormGroup>
                            <FormGroup>
                                <ControlLabel>testing framework</ControlLabel>
                                <FormControl
                                    componentClass="select"
                                    placeholder="testing framework"
                                    onChange={this.handleTestingFrameworkChange}>
                                    {supportedTestingFrameworks}
                                </FormControl>
                                <HelpBlock>Test framework to use in course</HelpBlock>
                            </FormGroup>
                            <FormGroup>
                                <ControlLabel>Number of tasks</ControlLabel>
                                <FormControl
                                    type="text"
                                    placeholder="Number of tasks"
                                    onChange={this.handleNumberOfTasksChange}/>
                                <HelpBlock>Number of git branches for tasks</HelpBlock>
                            </FormGroup>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button onClick={this.handleClose}>Close</Button>
                            <Button bsStyle="primary" onClick={this.createCourse}>Create</Button>
                        </Modal.Footer>
                    </form>
                </Modal>
            </div>
        );
    }

    handleClose() {
        this.setState({show: false});
    }

    handleShow() {
        this.setState({show: true});
    }

    handleCourseNameChange(event) {
        this.setState({courseName: event.target.value});
    }

    handleLanguageChange(event) {
        this.setState({language: event.target.value});
    }

    handleTestingLanguageChange(event) {
        this.setState({testLanguage: event.target.value});
    }

    handleTestingFrameworkChange(event) {
        this.setState({testingFramework: event.target.value});
    }

    handleNumberOfTasksChange(event) {
        this.setState({numberOfTasks: event.target.value});
    }

    createCourse() {
        Api.createCourse(credentials(),
            {
                courseName: this.state.courseName,
                language: this.state.language,
                testLanguage: this.state.testLanguage,
                testingFramework: this.state.testingFramework,
                numberOfTasks: parseInt(this.state.numberOfTasks)
            },
            course => {
                console.log('course has been created successfully');
                console.log(course);

                ReactDOM.render(
                    <CourseCreationMessage message={`Course ${this.state.courseName} has been created`} succeed/>,
                    document.getElementById('notifications')
                );

                this.props.onCourseCreation();
            },
            response => {
                console.log('course creation failed');
                console.log(response);

                ReactDOM.render(
                    <CourseCreationMessage message={`Course ${this.state.courseName} creation failed`}/>,
                    document.getElementById('notifications')
                )
            }
        );

        this.handleClose();
    }
}

function CourseCreationMessage(props) {
    return (
        <Alert bsStyle={props.succeed ? "success" : "danger"}>
            {props.message}
        </Alert>
    );
}