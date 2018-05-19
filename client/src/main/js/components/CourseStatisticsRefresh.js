import React from 'react';
import {Button} from 'reactstrap';
import ReactDOM from 'react-dom';
import {credentials} from '../scripts';
import {Api} from '../Api';
import {Notification} from './Notification';

export class CourseStatisticsRefresh extends React.Component {

    constructor(props) {
        super(props);

        this.synchronize = this.synchronize.bind(this);
    }

    synchronize() {
        Api.syncCourse(credentials(), this.props.course.name,
            () => ReactDOM.render(
                <Notification succeed
                              message={`Course ${this.props.course.name} synchronization
                                                has finished.`}/>,
                document.getElementById('notifications')
            ),
            response => ReactDOM.render(
                <Notification failed
                              message={`Course ${this.props.course.name} synchronization
                                                has failed due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }

    render() {
        return (
            <Button color="info" outline className="icon-btn"
                    onClick={this.synchronize}
                    disabled={this.props.course.state.lifecycle !== 'RUNNING' ? 'disabled' : ''}>
                <i className="material-icons">refresh</i>
            </Button>
        );
    }
}