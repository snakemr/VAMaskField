/*
Этот файл предназначен для создания классов контроллеров окон на его основе.
Вносить изменения в этот файл нежелательно.
 */
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Stage

open class Controller {
    protected lateinit var stage: Stage
    protected open fun onShowing() {}
    protected open fun onShown() {}
    protected open fun onCloseRequest() = true
    protected open fun onHiding() {}
    protected open fun onHidden() {}
    open fun onStop() {}

    protected fun alert(
        header: String, content: String? = null, type: Alert.AlertType = Alert.AlertType.NONE,
        dialogTitle: String? = null, check: (ButtonBar.ButtonData) -> Unit
    ) {
        val buttons =
            if (type == Alert.AlertType.CONFIRMATION)
                arrayOf(ButtonType.OK, ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE))
            else
                arrayOf(ButtonType.OK)
        Alert(type, content, *buttons).apply {
            initOwner(stage)
            title = dialogTitle
            headerText = header
            showAndWait().ifPresent{ check(it.buttonData) }
        }
    }

    companion object {
        fun start(stage: Stage, fxml: String = "main.fxml", title: String? = null, icon: String? = null): Controller {
            stage.title = title
            icon?.let { stage.icons.add(Image(it)) }
            FXMLLoader(javaClass.getResource(fxml)).apply {
                val root = load<Pane>()
                val controller = getController<Controller>()
                controller.stage = stage
                stage.scene = Scene(root)
                stage.apply {
                    setOnShowing { controller.onShowing() }
                    setOnShown { controller.onShown() }
                    setOnCloseRequest { if (!controller.onCloseRequest()) it.consume(); }
                    setOnHiding { controller.onHiding() }
                    setOnHidden { controller.onHidden() }
                    show()
                }
                return controller
            }
        }
    }
}