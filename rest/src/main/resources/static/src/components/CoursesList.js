import React from 'react';
import {Badge, Label, Panel} from "react-bootstrap";
import Immutable from 'immutable';
import {Api, credentials} from "../scripts";

export {CoursesList}

class CoursesList extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            account: props.account,
            courses: []
        };

        Api.retrieveCourses(
            credentials(),
            props.account.nickname,
            courses => {
                this.setState({courses: courses});
            },
            response => {
                console.log('courses retrieving failed');
                console.log(response);
            }
        );
    }

    render() {
        const courses = Immutable.List(this.state.courses)
            .map(value => <Course data={value}/>);

        return (
            <section className="courses-list">
                {courses.size > 0 ? courses : <p>There are no courses yet.</p>}
            </section>
        )
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