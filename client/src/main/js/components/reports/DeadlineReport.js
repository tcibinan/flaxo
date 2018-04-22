import React from 'react';

export function DeadlineReport(props) {
    if (props.solution.date) {
        if (props.task.deadline) {
            if (props.task.deadline > props.solution.date) {
                return (
                    <section className="report valid-deadline-report">
                        <i className="material-icons">alarm_on</i>
                    </section>
                );
            } else {
                return (
                    <section className="report invalid-deadline-report">
                        <i className="material-icons">alarm_off</i>
                    </section>
                );
            }
        } else {
            return (
                <section className="report valid-deadline-report">
                    <i className="material-icons">alarm_on</i>
                </section>
            );
        }
    } else {
        return (
            <section className="report"/>
        )
    }
}