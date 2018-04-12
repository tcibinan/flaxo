import React from 'react';

export function BuildReport(props) {
    return (
        <section className={"report " + (props.succeed ? "successful-build-report" : "failed-build-report")}>
            {
                props.succeed
                    ? <i className="material-icons">done</i>
                    : <i className="material-icons">clear</i>
            }
        </section>
    )
}