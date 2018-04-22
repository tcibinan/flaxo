import React from 'react';

export function BuildReport(props) {
    const latestBuildReport = props.solution.buildReports.last();

    if (latestBuildReport) {
        if (latestBuildReport.succeed) {
            return <section className="report successful-build-report"><i className="material-icons">done</i></section>;
        } else {
            return <section className="report failed-build-report"><i className="material-icons">clear</i></section>;
        }
    } else {
        return <section className="report"/>
    }
}