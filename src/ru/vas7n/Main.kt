/*
Этот файл предназначен для запуска приложения
Вносить изменения в этот файл нежелательно.
 */
import javafx.application.Application
import javafx.stage.Stage

class Main : Application() {
    private lateinit var controller: Controller

    override fun start(primaryStage: Stage) {
        controller = MainController.start(primaryStage)
    }

    override fun stop() {
        controller.onStop()
        super.stop()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {//  <<< Запустите программу с этой строки
            launch(Main::class.java)
        }
    }
}