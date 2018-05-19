import React from 'react';

export function DeadlineReport(props) {
    const lastCommit = props.solution.commits.last();
    const lastCommitDate = lastCommit ? lastCommit.date : null;
    if (lastCommitDate) {
        if (props.task.deadline) {
            if (props.task.deadline > lastCommitDate) {
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