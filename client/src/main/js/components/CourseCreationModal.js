import React from 'react';
import {Api} from '../Api';
import Immutable from 'immutable';
import {credentials} from '../scripts';
import ReactDOM from 'react-dom';
import {Notification} from './Notification';
import {
    Button,
    Form,
    FormGroup,
    FormText,
    Input,
    Label,
    Modal,
    ModalBody,
    ModalFooter,
    ModalHeader
} from 'reactstrap';

export class CourseCreationModal extends React.Component {

    constructor(props) {
        super(props);

        this.getLanguages = this.getLanguages.bind(this);
        this.getTestLanguages = this.getTestLanguages.bind(this);
        this.getTestingFrameworks = this.getTestingFrameworks.bind(this);
        this.toggle = this.toggle.bind(this);
        this.handleCourseNameChange = this.handleCourseNameChange.bind(this);
        this.handleCourseDescriptionChange = this.handleCourseDescriptionChange.bind(this);
        this.handleLanguageChange = this.handleLanguageChange.bind(this);
        this.handleTestingLanguageChange = this.handleTestingLanguageChange.bind(this);
        this.handleTestingFrameworkChange = this.handleTestingFrameworkChange.bind(this);
        this.handleNumberOfTasksChange = this.handleNumberOfTasksChange.bind(this);
        this.createCourse = this.createCourse.bind(this);

        this.state = {
            show: false,
            courseName: null,
            courseDescription: null,
            language: null,
            testLanguage: null,
            testingFramework: null,
            numberOfTasks: null,
            flaxoLanguages: []
        };

        Api.retrieveLanguages(
            languages => {
                this.setState(
                    {
                        flaxoLanguages: languages,
                        language: languages[0].name,
                        testLanguage: languages[0].compatibleTestingLanguages[0],
                        testingFramework: languages[0].compatibleTestingFrameworks[0]
                    }
                )
            }
        );
    }

    toggle() {
        this.setState({show: !this.state.show});
    }

    getLanguages() {
        return (
            Immutable.List(this.state.flaxoLanguages)
                .map(language => language.name)
                .map(name => <option value={name}
                                     selected={name === this.state.language ? "selected" : ""}>{name}</option>
                )
        );
    }

    getTestLanguages() {
        return (
            Immutable.List(this.state.flaxoLanguages)
                .filter(language => language.name === this.state.language)
                .flatMap(language => language.compatibleTestingLanguages)
                .map(name => <option value={name}
                                     selected={name === this.state.testLanguage ? "selected" : ""}>{name}</option>
                )
        );
    }

    getTestingFrameworks() {
        return (
            Immutable.List(this.state.flaxoLanguages)
                .filter(language => language.name === this.state.testLanguage)
                .flatMap(language => language.compatibleTestingFrameworks)
                .map(name => <option value={name}
                                     selected={name === this.state.testingFramework ? "selected" : ""}>{name}</option>
                )
        );
    }

    handleCourseNameChange(event) {
        this.setState({courseName: event.target.value});
    }

    handleCourseDescriptionChange(event) {
        this.setState({courseDescription: event.target.value});
    }

    handleLanguageChange(event) {
        const selectedLanguage = event.target.value;

        const defaultTestLanguage =
            Immutable.List(this.state.flaxoLanguages)
                .find(language => language.name === selectedLanguage)
                .compatibleTestingLanguages[0];

        const defaultTestingFramework =
            Immutable.List(this.state.flaxoLanguages)
                .find(language => language.name === defaultTestLanguage)
                .compatibleTestingFrameworks[0];

        this.setState({
            language: selectedLanguage,
            testLanguage: defaultTestLanguage,
            testingFramework: defaultTestingFramework
        });
    }

    handleTestingLanguageChange(event) {
        const selectedTestLanguage = event.target.value;

        const defaultTestingFramework =
            Immutable.List(this.state.flaxoLanguages)
                .find(language => language.name === selectedTestLanguage)
                .compatibleTestingFrameworks[0];

        this.setState({
            testLanguage: selectedTestLanguage,
            testingFramework: defaultTestingFramework
        });
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
                description: this.state.courseDescription,
                language: this.state.language,
                testingLanguage: this.state.testLanguage,
                testingFramework: this.state.testingFramework,
                numberOfTasks: parseInt(this.state.numberOfTasks)
            },
            () => {
                ReactDOM.render(
                    <Notification succeed
                                  message={`Course ${this.state.courseName} has been created`}/>,
                    document.getElementById('notifications')
                );

                this.props.onCourseCreation();
            },
            response => ReactDOM.render(
                <Notification message={`Course ${this.state.courseName} creation failed due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        );

        this.toggle();
    }

    render() {
        return (
            <div>
                <Button color="primary" outline
                        onClick={this.toggle}>Create course</Button>
                <Modal isOpen={this.state.show} toggle={this.toggle}>
                    <ModalHeader toggle={this.toggle}>Create course</ModalHeader>
                    <ModalBody>
                        <Form>
                            <FormGroup>
                                <Label for="courseName">Course name</Label>
                                <Input type="text" id="courseName" onChange={this.handleCourseNameChange}/>
                                <FormText color="muted">Course name should be a valid git repository name</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="courseDescription">Course description <small className="text-muted">optional</small></Label>
                                <Input type="text" id="courseDescription" onChange={this.handleCourseDescriptionChange}/>
                                <FormText color="muted">Course description won't be visible for students</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="language">Language</Label>
                                <Input type="select" id="language" onChange={this.handleLanguageChange}>
                                    {this.getLanguages()}
                                </Input>
                                <FormText color="muted">Language solutions will be written on</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="testingLanguage">Testing language</Label>
                                <Input type="select" id="testingLanguage" onChange={this.handleTestingLanguageChange}>
                                    {this.getTestLanguages()}
                                </Input>
                                <FormText color="muted">Language tests will be written on</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="testingFramework">Testing framework</Label>
                                <Input type="select" id="testingFramework" onChange={this.handleTestingFrameworkChange}>
                                    {this.getTestingFrameworks()}
                                </Input>
                                <FormText color="muted">Test framework to use in course</FormText>
                            </FormGroup>
                            <FormGroup>
                                <Label for="testingFramework">Number of tasks</Label>
                                <Input type="text" id="testingFramework" onChange={this.handleNumberOfTasksChange}/>
                                <FormText color="muted">Number of git branches for tasks</FormText>
                            </FormGroup>
                        </Form>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="primary" onClick={this.createCourse}>Create</Button>{' '}
                        <Button color="secondary" onClick={this.toggle}>Cancel</Button>
                    </ModalFooter>
                </Modal>
            </div>
        );
    }

}