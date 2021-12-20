/*
Контроллер главного окна приложения
Разметка для него лежит здесь: res/main.fxml
*/
import javafx.stage.Stage

class MainController : Controller() {
    //@FXML lateinit var edit: TextField
    //@FXML lateinit var label: Label

    init {
       //MaskField2().test()
    }

    companion object {  // В этой строке можно настроить текст заголовка окна и его значок
        fun start(stage: Stage) = start(stage, "main.fxml", "Приложение на Kotlin", "app_icon.png")
    }
}