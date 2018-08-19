import kotlinx.coroutines.experimental.launch
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.component.course
import org.flaxo.frontend.component.courseCard
import org.flaxo.frontend.component.navigationBar
import org.flaxo.frontend.component.services.codacyModal
import org.flaxo.frontend.component.services.githubModal
import org.flaxo.frontend.component.services.travisModal
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.User
import react.setState
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.p

fun RBuilder.courses(user: User, onLogout: () -> Unit) = child(Courses::class) {
    attrs {
        this.user = user
        this.onLogout = onLogout
    }
}

class CoursesProps(var user: User, var onLogout: () -> Unit) : RProps

class CoursesState(var courses: List<Course> = emptyList(),
                   var selectedCourse: Course? = null) : RState

class Courses(props: CoursesProps) : RComponent<CoursesProps, CoursesState>(props) {

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        state = CoursesState()
        launch { updateCoursesList() }
    }

    override fun RBuilder.render() {
        val selectedCourse = state.selectedCourse
        navigationBar(props.user, props.onLogout, ::deselectCourse)
        if (selectedCourse == null) {
            div(classes = "courses-list") {
                div(classes = "courses-list-container") {
                    state.courses
                            .takeIf { it.isNotEmpty() }
                            ?.forEach { courseCard(it, onSelect = ::selectCourse) }
                            ?: p { +"There are no courses yet." }
                }
                courseCreationModal(onCourseCreation = ::updateCoursesList)
            }
        } else {
            course(selectedCourse, onUpdate = ::updateCoursesList, onDelete = ::deselectCourse)
        }
        githubModal(props.user)
        travisModal(props.user)
        codacyModal(props.user)
    }

    private fun updateCoursesList() {
        credentials?.also { credentials ->
            try {
                val courses = flaxoClient.getUserCourses(credentials, props.user.nickname)
                val selectedCourse = state.selectedCourse?.let { previouslySelectedCourse ->
                    courses.find { it.name == previouslySelectedCourse.name }
                }
                setState {
                    this.selectedCourse = selectedCourse
                    this.courses = courses
                }
            } catch (e: Throwable) {
                // TODO 12.08.18: notify that courses list updating has failed
                console.log(e)
            }
        }
    }

    private fun deselectCourse() {
        setState { selectedCourse = null }
    }

    private fun selectCourse(course: Course) {
        setState { selectedCourse = course }
    }

}
