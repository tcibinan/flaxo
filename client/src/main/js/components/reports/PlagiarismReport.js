import React from 'react';

export function PlagiarismReport(props) {
    const valid = props.matches
        .every(match => match.percentage < 80);

    const percentages = props.matches
        .map(match => match.percentage + "%")
        .join(", ");

    return (
        <section
            className={"report plagiarism-report " + (valid ? "valid-plagiarism-report" : "invalid-plagiarism-report")}>
            {
                props.matches.size > 0
                    ? <div>{props.matches.size} <i
                        className="material-icons plagiarism-marker">remove_red_eye</i>{' '}{percentages}</div>
                    : <i className="material-icons plagiarism-marker">visibility_off</i>
            }
        </section>
    );
}