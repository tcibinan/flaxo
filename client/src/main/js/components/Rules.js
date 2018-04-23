import React from 'react';
import {DeadlineRule} from './DeadlineRule';
import {Button, FormGroup} from 'reactstrap';
import {Api} from '../Api';
import {Notification} from './Notification';
import {credentials} from '../scripts';
import ReactDOM from 'react-dom';

export class Rules extends React.Component {

    constructor(props) {
        super(props);
        this.onDeadlineChange = this.onDeadlineChange.bind(this);
        this.submitRulesChanges = this.submitRulesChanges.bind(this);

        this.state = {
            deadline: props.task.deadline,
            saveDisabled: true,
        }
    }

    onDeadlineChange(newDeadline) {
        this.setState({deadline: newDeadline, saveDisabled: false});
    }

    submitRulesChanges() {
        Api.updateRules(credentials(),
            this.props.course.name,
            this.props.task.branch,
            this.state.deadline,
            () => ReactDOM.render(
                <Notification succeed message={`${this.props.task.branch} rules was updated!`}/>,
                document.getElementById('notifications')
            ),
            response => ReactDOM.render(
                <Notification message={`${this.props.task.branch} rules was not updated due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }

    render() {
        return (
            <section className="rules-list">
                <DeadlineRule task={this.props.task} onDeadlineChange={this.onDeadlineChange}/>
                <FormGroup>
                    <Button color="primary"
                            onClick={this.submitRulesChanges}
                            disabled={this.state.saveDisabled ? 'disabled' : ''}
                    >Save rules</Button>
                </FormGroup>
            </section>
        )
    }
}

