import React from 'react';
import {
    Button,
    Card,
    CardBody,
    CardLink,
    CardTitle,
    Collapse,
} from 'reactstrap';
import {Rules} from './Rules';
import {TaskStatistics} from './TaskStatistics';

export class Task extends React.Component {

    constructor(props) {
        super(props);

        this.rulesToggle = this.rulesToggle.bind(this);
        this.rulesAdditionToggle = this.rulesAdditionToggle.bind(this);
        this.saveResults = this.saveResults(this);

        this.state = {
            rulesCollapse: false,
            rulesAdditionDropdownOpen: false
        };
    }

    rulesToggle() {
        this.setState({rulesCollapse: !this.state.rulesCollapse});
    }

    rulesAdditionToggle() {
        this.setState({rulesAdditionDropdownOpen: !this.state.rulesAdditionDropdownOpen});
    }

    saveResults() {
        //todo: Implement results aggregation and saving
    }

    render() {
        const latestPlagiarismReport =
            this.props.task
                .plagiarismReports
                .last();

        return (
            <section className="task-card">
                <Card>
                    <CardBody>
                        <CardTitle>{this.props.task.branch}</CardTitle>
                        <CardLink href={this.props.task.url}>Git branch</CardLink>
                        {
                            latestPlagiarismReport
                                ? <CardLink href={latestPlagiarismReport.url}>Plagiarism report</CardLink>
                                : <CardLink disabled>Plagiarism report</CardLink>
                        }
                        <Button className="save-results-btn"
                                onClick={this.saveResults}
                                color="primary"
                                outline
                        >
                            Save results
                        </Button>
                        <Button className="rules-toggle-btn"
                                onClick={this.rulesToggle}
                                color="secondary"
                                outline
                        >
                            Rules
                        </Button>
                        <Collapse isOpen={this.state.rulesCollapse}>
                            <hr/>
                            <Rules course={this.props.course} task={this.props.task}/>
                        </Collapse>
                        <TaskStatistics course={this.props.course} task={this.props.task}/>
                    </CardBody>
                </Card>
            </section>
        );
    }
}

