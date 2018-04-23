import React from 'react';
import {
    FormGroup,
    FormText,
    Input,
    Label
} from 'reactstrap';

export class DeadlineRule extends React.Component {

    constructor(props) {
        super(props);
        this.handleDeadlineChange = this.handleDeadlineChange.bind(this);
        this.removeDeadline = this.removeDeadline.bind(this);
    }

    handleDeadlineChange(event) {
        const newDeadline = event.target.value;
        this.props.onDeadlineChange(newDeadline);
    }

    removeDeadline(event) {
        event.preventDefault();
        document.querySelector('#task_deadline_input').value = '';
        this.props.onDeadlineChange('');
    }

    render() {
        return (
            <section className="task-deadline-rule">
                <FormGroup>
                    <Label for="task_deadline_input">Deadline</Label>
                    <Input type="date"
                           name="task_deadline_input"
                           id="task_deadline_input"
                           defaultValue={
                               this.props.task.deadline
                                   ? this.props.task.deadline.toISOString().substring(0, 10)
                                   : ''
                           }
                           onChange={this.handleDeadlineChange}/>
                    {
                        this.props.task.deadline
                            ? <FormText>You can <a href="#" onClick={this.removeDeadline}>remove</a> the deadline.</FormText>
                            : null
                    }
                </FormGroup>
            </section>
        );
    }
}