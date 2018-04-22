import React from 'react';

export function CodeStyleReport(props) {
    const latestCodeStyleReport = props.solution.codeStyleReports.last();

    if (latestCodeStyleReport) {
        return (
            <section className="report">
                <div className={'code-style-grade code-style-grade-' + latestCodeStyleReport.grade}/>
            </section>
        );
    } else {
        return <section className="report"/>;
    }
}