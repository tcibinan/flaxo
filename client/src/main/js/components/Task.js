import React from 'react';
import {Button} from 'reactstrap';
import {TaskStatistics} from './TaskStatistics';

export class Task extends React.Component {
    render() {
        const gitLink =
            `https://github.com/${this.props.user}/${this.props.courseName}/tree/${this.props.name}`;

        const mossLink =
            this.props.mossResultUrl
                ? this.props.mossResultUrl
                : null;

        return (
            <section className="course-task">
                <Button href={gitLink} color="link" size="sm">Git branch</Button>
                {
                    mossLink
                        ? <Button href={mossLink} color="link" size="sm">Moss analysis results</Button>
                        : <Button color="link" size="sm" disabled>Moss analysis results</Button>
                }
                <TaskStatistics mossPlagiarismMatches={this.props.mossPlagiarismMatches}
                                studentTasks={this.props.studentTasks}/>
            </section>
        );
    }
}