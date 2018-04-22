import React from 'react';
import {
    BuildReport,
    PlagiarismReport,
    DeadlineReport,
    CodeStyleReport,
    ReportScoreSuggestion
} from './reports';
import {Table} from 'reactstrap';

export class TaskStatistics extends React.Component {
    render() {
        const results =
            this.props.course
                .students
                .map((student, studentIndex) => {
                    const solution =
                        this.props.task
                            .solutions
                            .find(solution => solution.student === student);

                    return (
                        <tr>
                            <th scope="row">{studentIndex + 1}</th>
                            <td>{student}</td>
                            <td>
                                <BuildReport solution={solution}/>
                            </td>
                            <td>
                                <CodeStyleReport solution={solution}/>
                            </td>
                            <td>
                                <PlagiarismReport task={this.props.task} solution={solution}/>
                            </td>
                            <td>
                                <DeadlineReport task={this.props.task} solution={solution}/>
                            </td>
                            <td>
                                <ReportScoreSuggestion task={this.props.task} solution={solution}/>
                            </td>
                        </tr>
                    )
                });

        return (
            <section className="task-results">
                <Table hover size="sm">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>Student</th>
                        <th>Build</th>
                        <th>Code style</th>
                        <th>Plagiarism detection</th>
                        <th>Deadline</th>
                        <th>Result</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        results.size > 0
                            ? results
                            : <td colSpan="7">There are no students on the course yet.</td>
                    }
                    </tbody>
                </Table>
            </section>
        );
    }
}