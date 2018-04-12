import React from 'react';
import Immutable from 'immutable';
import {Badge} from 'reactstrap';

export function CourseLabels(props) {
    const statusLabel = <Badge color="primary">{props.course.status}</Badge>;

    const techLabels =
        Immutable
            .Set([
                props.course.language,
                props.course.testingLanguage,
                props.course.testingFramework
            ])
            .map(value => <span><Badge color="info">{value}</Badge>{' '}</span>);

    return (
        <span className="course-labels">
            {statusLabel}{' '}{techLabels}
        </span>
    );
}