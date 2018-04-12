import React from 'react';
import {Button, Table} from 'reactstrap';
import Immutable from 'immutable';

export class TasksStatistics extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const tasksNames =
            Immutable.List(this.props.course.tasks)
                .sort();

        const toStudentResultCell = (studentTasks, taskName) => {
            const studentTask =
                Immutable.List(studentTasks)
                    .find(studentTask => studentTask.task === taskName);

            return (
                <td className={
                    `${studentTask.built ? 'solution-built' : ''} ${studentTask.succeed ? 'solution-succeed' : ''}`
                }/>
            );
        };

        let studentIndex = 0;
        const studentsResults =
            Immutable.Map(this.props.perStudentStats)
                .map((studentTasks, student) =>
                    <tr>
                        <th scope="row">{studentIndex += 1}</th>
                        <td>{student}</td>
                        {tasksNames.map(taskName => toStudentResultCell(studentTasks, taskName))}
                        <td>0</td>
                    </tr>
                )
                .valueSeq();

        const gitRepositoryLink =
            `https://github.com/${this.props.course.userGithubId}/${this.props.course.name}`;

        return (
            <dev>
                <Button href={gitRepositoryLink} color="link" size="sm">Git repository</Button>
                <section className="course-stats">
                    <Table hover size="sm">
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Student</th>
                            {tasksNames.map(name => <th>{name}</th>)}
                            <th>Score</th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            studentsResults.size > 0
                                ? studentsResults
                                :
                                <td colSpan={tasksNames.size + 2}>There are no students on the course yet.</td>
                        }
                        </tbody>
                    </Table>
                </section>
            </dev>
        )
    }
}