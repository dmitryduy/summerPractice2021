package com.example.layout


import javafx.scene.control.Button
import javafx.scene.control.TextField


class Layout {
    private var currentStep = -1

    fun getStep(): Int {
        return currentStep
    }

    fun incrementStep() {
        currentStep++
    }

    fun decrementStep() {
        currentStep--
    }

    fun resetStep() {
        currentStep = -1
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

}