import React from 'react';

export function DeadlineReport(props) {
    return (
        <section className={"report " + (props.deadline ? "valid-deadline-report" : "invalid-deadline-report")}>
            {
                props.deadline
                    ? <i className="material-icons">alarm_on</i>
                    : <i className="material-icons">alarm_off</i>
            }
        </section>
    )
}