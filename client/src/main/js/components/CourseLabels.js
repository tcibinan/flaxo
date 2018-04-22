import React from 'react';
import {Badge} from 'reactstrap';
import {Seq} from 'immutable';

export function CourseLabels(props) {
    const statusLabel =
        <Badge className="course-label" color="primary">{props.course.state.lifecycle.toLowerCase()}</Badge>;

    const techLabels =
        Seq([
            props.course.language,
            props.course.testingLanguage,
            props.course.testingFramework
        ])
            .map(value => value.toLowerCase())
            .map(value => <Badge className="course-label" color="info">{value}</Badge>);

    const servicesLabels =
        props.course.state
            .activatedServices
            .map(value => value.toLowerCase())
            .map(service => <Badge className="course-label" color="warning">{service}</Badge>);

    return (
        <span className="course-labels">
            {statusLabel}
            {servicesLabels}
            {techLabels}
        </span>
    );
}