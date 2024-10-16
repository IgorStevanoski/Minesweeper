package control

import scalafx.scene.text.Text
import scene.GameScene

object Timer{
  var time: Int = 0
  val timer = new Text()
  var stopped = true
  updateText()

  var t = new java.util.Timer()
  var task = new java.util.TimerTask {
    def run() = {}
  }

  def start(): Unit = {
    stop()
    if (!GameScene.gameLoaded) time = 0
    stopped = false
    updateText()
    t = new java.util.Timer()
    task = new java.util.TimerTask {
      def run() = {
        time += 1
        updateText()
      }
    }
    t.schedule(task, 1000L, 1000L)
  }

  def stop(): Unit = {
    if (!stopped) {
      stopped = true
      task.cancel()
    }
  }

  def reset(): Unit = {
    time = 0
    updateText()
  }

  def setTime(t: Int): Unit = {
    time = t
    updateText()
  }

  private def updateText() = timer.text = "Time: " + time
}
