import React from 'react';
import {Api} from '../Api';
import {credentials} from '../scripts';
import ReactDOM from 'react-dom';
import {Seq} from 'immutable';
import {Notification} from './Notification';
import {
    Nav,
    NavItem,
    NavLink,
    TabContent,
    TabPane
} from 'reactstrap';
import {TasksStatistics} from './TasksStatistics';
import {Task} from './Task';

export class CourseStatistics extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);

        this.state = {
            activeTab: '0',
            tasks: Seq()
        };
    }

    componentDidMount() {
        Api.retrieveCourseStatistics(credentials(),
            this.props.course.user,
            this.props.course.name,
            tasks => this.setState({tasks}),
            response => ReactDOM.render(
                <Notification message={`Course statistics retrieving failed due to: ${response}`}/>,
                document.getElementById('notifications')
            )
        )
    }

    toggle(tab) {
        this.setState({activeTab: tab});
    }

    render() {
        const tasksTabsNavItems =
            this.state.tasks.map((task, index) =>
                <NavItem>
                    <NavLink
                        onClick={() => {
                            this.toggle(index + 1);
                        }}
                        active={this.state.activeTab === index + 1 ? 'active' : ''}
                    >
                        {task.branch}
                    </NavLink>
                </NavItem>
            );

        const tasksTabs =
            this.state.tasks.map((task, index) => {
                return (
                    <TabPane tabId={index + 1}>
                        <Task course={this.props.course}
                              task={task}/>
                    </TabPane>
                )
            });

        return (
            <section className="course-tabs">
                <Nav tabs>
                    <NavItem>
                        <NavLink
                            active={this.state.activeTab === '0' ? 'active' : ''}
                            onClick={() => {
                                this.toggle('0');
                            }}
                        >
                            Course summary
                        </NavLink>
                    </NavItem>
                    {tasksTabsNavItems}
                </Nav>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId="0">
                        <TasksStatistics course={this.props.course}
                                         tasks={this.state.tasks}/>
                    </TabPane>
                    {tasksTabs}
                </TabContent>
            </section>
        );
    }
}

