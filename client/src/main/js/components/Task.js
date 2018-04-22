import React from 'react';
import {Button} from 'reactstrap';
import {TaskStatistics} from './TaskStatistics';

export class Task extends React.Component {
    render() {
        const latestPlagiarismReport =
            this.props.task
                .plagiarismReports
                .last();

        return (
            <section className="course-task">
                <Button href={this.props.task.url} color="link" size="sm">Git branch</Button>
                {
                    latestPlagiarismReport
                        ? <Button href={latestPlagiarismReport.url} color="link" size="sm">Plagiarism analysis results</Button>
                        : <Button color="link" size="sm" disabled>Plagiarism analysis results</Button>
                }
                <TaskStatistics course={this.props.course} task={this.props.task}/>
            </section>
        );
    }
}