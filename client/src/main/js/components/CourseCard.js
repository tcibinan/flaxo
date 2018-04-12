import React from 'react';
import {
    Card,
    CardBody,
    CardTitle,
    CardSubtitle,
    CardText,
    CardLink
} from 'reactstrap';
import {CourseLabels} from './CourseLabels';

export class CourseCard extends React.Component {

    constructor(props) {
        super(props);

        this.state = props.data;
    }

    render() {
        return (
            <section className="course-item" onClick={() => this.props.onSelect(this.state)}>
                <Card>
                    <CardBody>
                        <CardTitle>
                            {this.state.name}
                            <small><CourseLabels course={this.state}/></small>
                        </CardTitle>
                        <CardSubtitle>{this.state.tasks.length} tasks, {this.state.students.length} students</CardSubtitle>
                        <CardText>
                            <p>
                                Ambiguous course description. Ambiguous course description. Ambiguous course description.
                                Ambiguous course description. Ambiguous course description. Ambiguous course description.
                            </p>
                        </CardText>
                        <CardText>
                            <small className="text-muted">Last updated 3 mins ago</small>
                        </CardText>
                        <CardLink href="#">Repository</CardLink>
                    </CardBody>
                </Card>
            </section>
        );
    }
}