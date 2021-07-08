package com.example.dijkstra

class DijkstraSteps {
    val dijkstraSteps: ArrayList<DijkstraStep> = ArrayList<DijkstraStep>()
    var curr: Int = 0

    fun addStep(step: DijkstraStep) {
        dijkstraSteps.add(step)
    }

    fun clear() {
        dijkstraSteps.clear()
    }

    //возращает последний степ
    fun getResult(): DijkstraStep {
        return dijkstraSteps[dijkstraSteps.size - 1]
    }

    //Возвращает step на текущем шаге и сдвигается вперед, когда дойдет до последнего на нем остановится
    fun next(): DijkstraStep {
        if (curr != dijkstraSteps.size) {
            curr += 1
        }
        return dijkstraSteps[curr - 1]
    }

    //Возвращает step на предыдущем шагу и сдвигается на него, когда дойдет до первого на нем остановится
    fun prev(): DijkstraStep {
        if (curr != 0) {
            curr -= 1
        }
        return dijkstraSteps[curr]
    }

    //Запуск сначала
    fun start() {
        curr = 0
    }
}