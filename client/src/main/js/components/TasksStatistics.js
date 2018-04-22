import React from 'react';
import {Button, Table} from 'reactstrap';

export class TasksStatistics extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const tasksNames = this.props.course.tasks.sort();

        let studentIndex = 0;
        const studentsResults =
            this.props.tasks
                .flatMap(task => task.solutions)
                .groupBy(solution => solution.student)
                .map((solutions, student) =>
                    <tr>
                        <th scope="row">{studentIndex += 1}</th>
                        <td>{student}</td>
                        {
                            tasksNames
                                .map(task => {
                                    const solution = solutions.find(solution => solution.task === task);
                                    if (solution) {
                                        return (<td>{solution.score}</td>)
                                    } else {
                                        return (<td/>);
                                    }
                                })
                        }
                        <td>0</td>
                    </tr>
                )
                .valueSeq();

        return (
            <dev>
                <Button href={this.props.course.url} color="link" size="sm">Git repository</Button>
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
                                : <td colSpan={tasksNames.size + 2}>There are no students on the course yet.</td>
                        }
                        </tbody>
                    </Table>
                </section>
            </dev>
        )
    }
}