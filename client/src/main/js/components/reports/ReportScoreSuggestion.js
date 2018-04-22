import React from 'react';
import {
    Button,
    Form,
    FormGroup,
    Input
} from 'reactstrap';
import {suggestScore} from '../../scripts';

export function ReportScoreSuggestion(props) {
    const latestBuildReport =
        props.solution
            .buildReports
            .last();

    const latestCodeStyleReport =
        props.solution
            .codeStyleReports
            .last();

    const suggestedResult = suggestScore(
        latestBuildReport,
        latestCodeStyleReport,
        props.solution.date,
        props.task.deadline
    );

    return (
        <section className="suggesting-result">
            <Form inline>
                <FormGroup controlId="student-result-non-unique-id">
                    <Input type="number" placeholder={suggestedResult}/>{' '}
                    <Button color="link">suggesting {suggestedResult}</Button>
                </FormGroup>
            </Form>
        </section>
    );
}