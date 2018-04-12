import React from 'react';
import {Button, Form, FormGroup, Input} from 'reactstrap';

export function ReportScoreSuggestion(props) {
    return (
        <section className="suggesting-result">
            <Form inline>
                <FormGroup controlId="student-result-non-unique-id">
                    <Input type="number" placeholder={props.suggestion}/>{' '}
                    {/*<ControlLabel>suggesting </ControlLabel>{' '}*/}
                    <Button color="link">suggesting {props.suggestion}</Button>
                </FormGroup>
            </Form>
        </section>
    )
}