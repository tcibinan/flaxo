import React from 'react';
import ReactDOM from 'react-dom';
import Immutable from 'immutable';
import {credentials} from '../scripts';
import {Api} from '../Api';
import {Notification} from './Notification';
import {CourseCreationModal} from './CourseCreationModal';
import {Course} from './Course';
import {CourseCard} from './CourseCard';

export class Courses extends React.Component {

    constructor(props) {
        super(props);

        this.updateCoursesList = this.updateCoursesList.bind(this);
        this.selectCourse = this.selectCourse.bind(this);
        this.deleteCourse = this.deleteCourse.bind(this);

        this.state = {
            account: props.account,
            courses: [],
            selectedCourse: null
        };

        this.updateCoursesList();
    }

    render() {
        if (this.state.selectedCourse == null) {
            const courses = Immutable.List(this.state.courses)
                .map(value => <CourseCard data={value} onSelect={this.selectCourse}/>);

            return (
                <article className="courses-list">
                    <CourseCreationModal onCourseCreation={this.updateCoursesList}/>
                    <div className="courses-list-container">
                        {courses.size > 0 ? courses : <p>There are no courses yet.</p>}
                    </div>
                </article>
            );
        } else {
            return (
                <Course data={this.state.selectedCourse}
                        onUpdate={this.updateCoursesList}
                        onDelete={this.deleteCourse}/>
            )
        }
    }

    updateCoursesList() {
        Api.retrieveCourses(
            credentials(),
            this.props.account.nickname,
            courses => {
                const selectedCourse =
                    this.state.selectedCourse != null
                        ? courses.find(course => course.name === this.state.selectedCourse.name)
                        : null;

                this.setState({courses, selectedCourse});
            },
            response => ReactDOM.render(
                <Notification title="Courses retrieving failed" message={response}/>,
                document.getElementById('notifications')
            )
        );
    }

    deleteCourse(course) {
        this.setState({selectedCourse: null});
        this.updateCoursesList();
    }

    selectCourse(selectedCourse) {
        this.setState({selectedCourse});
    }

}

