import React from 'react';
import {Api} from '../Api';
import {credentials} from '../scripts';
import ReactDOM from 'react-dom';
import Immutable from 'immutable';
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
            perStudentStats: {},
            perTaskStats: {}
        };
    }

    componentDidMount() {
        Api.retrieveCourseStatistics(credentials(), this.props.course.user, this.props.course.name,
            statistics => this.setState({
                perStudentStats: statistics.perStudentStats,
                perTaskStats: statistics.perTaskStats
            }),
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
        const tasks =
            Immutable.Map(this.state.perTaskStats)
                .map((taskData, taskName) => {
                    return {
                        name: taskName,
                        mossResultUrl: taskData.mossResultUrl,
                        mossPlagiarismMatches: taskData.mossPlagiarismMatches
                    }
                })
                .valueSeq()
                .sortBy(task => task.name);

        const tasksTabsNavItems =
            tasks.map((task, index) =>
                <NavItem>
                    <NavLink onClick={() => {
                        this.toggle(index + 1);
                    }}>
                        {task.name}
                    </NavLink>
                </NavItem>
            );

        const tasksTabs =
            tasks.map((task, index) => {
                const studentTasks =
                    Immutable.Map(this.state.perStudentStats)
                        .map(studentTasks =>
                            Immutable.List(studentTasks)
                                .find(studentTask => studentTask.task === task.name)
                        )
                        .toList();

                return (
                    <TabPane tabId={index + 1}>
                        <Task name={task.name}
                                    user={this.props.course.userGithubId}
                                    courseName={this.props.course.name}
                                    mossResultUrl={task.mossResultUrl}
                                    mossPlagiarismMatches={task.mossPlagiarismMatches}
                                    studentTasks={studentTasks}/>
                    </TabPane>
                )
            });

        return (
            <section className="course-tabs">
                <Nav tabs>
                    <NavItem>
                        <NavLink onClick={() => {
                            this.toggle('0');
                        }}>
                            Course summary
                        </NavLink>
                    </NavItem>
                    {tasksTabsNavItems}
                </Nav>
                <TabContent activeTab={this.state.activeTab}>
                    <TabPane tabId="0">
                        <TasksStatistics course={this.props.course}
                                         perStudentStats={this.state.perStudentStats}/>
                    </TabPane>
                    {tasksTabs}
                </TabContent>
            </section>
        );
    }
}

