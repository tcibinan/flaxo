import React from 'react';
import Immutable from 'immutable';
import {
    BuildReport,
    PlagiarismReport,
    DeadlineReport,
    CodeStyleReport,
    ReportScoreSuggestion
} from './reports';
import {Table} from 'reactstrap';
import {gradeToNum} from '../scripts';

export class TaskStatistics extends React.Component {
    render() {
        const results =
            Immutable.List(this.props.studentTasks)
                .map((studentTask, studentIndex) => {
                    const matches =
                        Immutable.List(this.props.mossPlagiarismMatches)
                            .filter(match => studentTask.student === match.students.first
                                || studentTask.student === match.students.second)
                            .map(match => {
                                return {
                                    thisStudent:
                                        match.students.first === studentTask.student
                                            ? match.students.first
                                            : match.students.second,
                                    otherStudent:
                                        match.students.first !== studentTask.student
                                            ? match.students.first
                                            : match.students.second,
                                    lines: match.lines,
                                    link: match.link,
                                    percentage: match.percentage
                                }
                            });

                    const resultSuggestion =
                        60 * studentTask.succeed + 5 * gradeToNum(studentTask.grade) + 10 * studentTask.deadline;

                    return (
                        <tr>
                            <th scope="row">{studentIndex + 1}</th>
                            <td>{studentTask.student}</td>
                            <td>
                                <BuildReport built={studentTask.built} succeed={studentTask.succeed}/>
                            </td>
                            <td>
                                <CodeStyleReport grade={studentTask.grade}/>
                            </td>
                            <td>
                                <PlagiarismReport matches={matches}/>
                            </td>
                            <td>
                                <DeadlineReport deadline={studentTask.deadline}/>
                            </td>
                            <td>
                                <ReportScoreSuggestion suggestion={resultSuggestion}/>
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