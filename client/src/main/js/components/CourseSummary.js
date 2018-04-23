import React from 'react';
import {
    Button,
    Card,
    CardBody,
    CardLink,
    CardTitle,
    Table
} from 'reactstrap';

export class CourseSummary extends React.Component {

    constructor(props) {
        super(props);
        this.saveResults = this.saveResults.bind(this);
    }

    saveResults() {
        //todo: Implement results aggregation and saving
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
            <section className="course-summary">
                <Card>
                    <CardBody>
                        <CardTitle>Course summary</CardTitle>
                        <CardLink href={this.props.course.url}>Git repository</CardLink>
                        <Button className="save-results-btn"
                                onClick={this.saveResults}
                                color="primary"
                                outline
                        >
                            Save results
                        </Button>
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
                    </CardBody>
                </Card>
            </section>
        )
    }
}