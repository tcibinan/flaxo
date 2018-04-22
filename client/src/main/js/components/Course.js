import React from 'react';
import {Api} from '../Api';
import {credentials} from '../scripts';
import ReactDOM from 'react-dom';
import Immutable from 'immutable';
import {CourseStatistics} from './CourseStatistics';
import {CourseLabels} from './CourseLabels';
import {Button} from 'reactstrap';
import {CourseStatisticsDownloadMenu} from './CourseStatisticsDownloadMenu';
import {Notification} from './Notification';

export class Course extends React.Component {

    constructor(props) {
        super(props);

        this.startCourse = this.startCourse.bind(this);
        this.analysePlagiarism = this.analysePlagiarism.bind(this);
        this.deleteCourse = this.deleteCourse.bind(this);
    }

    render() {
        return (
            <section className="selected-course">
                <h2>
                    {this.props.course.name}
                    <CourseLabels course={this.props.course}/>
                </h2>
                <section className="course-controls">
                    <Button color="primary" outline className="course-control"
                            onClick={this.startCourse}
                            disabled={this.props.course.state.lifecycle === 'RUNNING' ? 'disabled' : ''}>
                        Start course
                    </Button>
                    <Button color="primary" outline className="course-control"
                            onClick={this.analysePlagiarism}
                            disabled={this.props.course.state.lifecycle !== 'RUNNING' ? 'disabled' : ''}>
                        Analyse plagiarism
                    </Button>
                    <Button color="danger" outline className="course-control"
                            onClick={this.deleteCourse}>Delete course</Button>
                    <CourseStatisticsDownloadMenu course={this.props.course}/>
                </section>
                <CourseStatistics course={this.props.course}/>
            </section>
        );
    }

    startCourse() {
        Api.startCourse(credentials(), this.props.name,
            () => {
                this.props.course.state.lifecycle = 'RUNNING';
                this.props.onUpdate();

                ReactDOM.render(
                    <Notification succeed message={`Course ${this.props.course.name} has started successful.`}/>,
                    document.getElementById('notifications')
                );
            },
            response => ReactDOM.render(
                <Notification message={`Course ${this.props.course.name} starting went bad due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        );
    }

    analysePlagiarism() {
        Api.analysePlagiarism(credentials(), this.props.course.name,
            scheduledTasks => {
                const tasks = Immutable.List(scheduledTasks).join(', ');

                if (tasks) {
                    ReactDOM.render(
                        <Notification succeed
                                      message={`Plagiarism analysis for course ${this.props.course.name}
                                                has been scheduled for ${tasks}.`}/>,
                        document.getElementById('notifications')
                    );
                } else {
                    ReactDOM.render(
                        <Notification info
                                      message={`Plagiarism analysis for course hasn't been scheduled.
                                                There aren't enough students solutions.`}/>,
                        document.getElementById('notifications')
                    );
                }
            },
            response => {
                ReactDOM.render(
                    <Notification succeed
                                  message={`Plagiarism analysis for course ${this.props.course.name}
                                            hasn't been scheduled due to: ${response}`}/>,
                    document.getElementById('notifications')
                );
            }
        );
    }

    deleteCourse() {
        Api.deleteCourse(credentials(), this.props.course.name,
            () => {
                this.props.onDelete(this.props.course.name);

                ReactDOM.render(
                    <Notification succeed message={`Course ${this.props.course.name} has been deleted.`}/>,
                    document.getElementById('notifications')
                );
            },
            response => ReactDOM.render(
                <Notification message={`Course ${this.props.course.name} deletion went bad due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        );
    }
}