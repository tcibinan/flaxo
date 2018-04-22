import React from 'react';

export function PlagiarismReport(props) {
    const latestPlagiarismReport =
        props.task
            .plagiarismReports
            .last();

    if (latestPlagiarismReport) {
        const student = props.solution.student;

        const matches =
            latestPlagiarismReport
                .matches
                .filter(match => match.student1 === student || match.student2 === student)
                .cacheResult();

        const valid =
            matches
                .every(match => match.percentage < 80);

        const percentages =
            matches
                .map(match => match.percentage + '%')
                .join(', ');

        return (
            <section
                className={'report plagiarism-report ' + (valid ? 'valid-plagiarism-report' : 'invalid-plagiarism-report')}>
                {
                    matches.size > 0
                        ? <div>
                            {matches.size}
                            <i className="material-icons plagiarism-marker">remove_red_eye</i>{' '}
                            {percentages}
                        </div>
                        : <i className="material-icons plagiarism-marker">visibility_off</i>
                }
            </section>
        );
    } else {
        return (
            <section className="report plagiarism-report"/>
        );
    }
}