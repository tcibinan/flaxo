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

export function CourseCard(props) {
    return (
        <section className="course-item" onClick={() => props.onSelect(props.course)}>
            <Card>
                <CardBody>
                    <CardTitle>
                        {props.course.name}
                        <small><CourseLabels course={props.course}/></small>
                    </CardTitle>
                    <CardSubtitle>{props.course.tasks.size} tasks, {props.course.students.size} students</CardSubtitle>
                    {
                        props.course.description
                            ? <CardText>
                                <p>{props.course.description}</p>
                            </CardText>
                            : <CardText/>
                    }
                    <CardText>
                        <small className="text-muted">Created at {props.course.createdDate.toDateString()}</small>
                    </CardText>
                    <CardLink href={props.course.url}>Repository</CardLink>
                </CardBody>
            </Card>
        </section>
    );
}