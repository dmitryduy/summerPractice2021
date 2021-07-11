package com.example.layout


import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Layout {
    private val file = File("file.log")
    init {
        file.writeText("")
    }

    fun stylizeTextField(textField: TextField) {
        textField.isEditable = false
        textField.style ="-fx-background-color: transparent;" +
                " -fx-background-insets: 0; " +
                "-fx-background-radius: 0; " +
                "-fx-padding: 0;"
    }

    fun setButtonAnimation(button: Button) {
        button.setOnMousePressed {
            button.setPrefSize(button.width - 2, button.height - 2)
            button.layoutX = button.layoutX + 1
            button.layoutY = button.layoutY + 1
        }
        button.setOnMouseReleased {
            button.setPrefSize(button.width + 2, button.height + 2)
            button.layoutX = button.layoutX - 1
            button.layoutY = button.layoutY - 1
        }
    }

    fun stylizeTextArea(textArea: TextArea) {
        textArea.isEditable = false
        textArea.isWrapText = true
        textArea.style = "-fx-background-color: transparent;" +
                " -fx-background-insets: 0; " +
                "-fx-background-radius: 0; " +
                "-fx-padding: 0;" +
                "-fx-control-inner-background:#f4f4f4;"


    }

    fun writeLogs(text: String) {
        val currentDate = SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(Date())
        file.appendText("$currentDate: $text\n")
    }

}
