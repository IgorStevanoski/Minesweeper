package control.counter

import scalafx.scene.text.Text

trait Counter {
  var count: Int = 0
  var counter = new Text
  val labelText = "Label: "
//  updateText()

  def increaseCnt() = {
    count += 1
    updateText()
  }

  def decreaseCnt() = {
    count -= 1
    updateText()
  }

  def resectCnt() = {
    count = 0
    updateText()
  }

  def setCnt(cnt: Int): Unit = {
    count = cnt
    updateText()
  }

  protected def updateText() = counter.text = labelText + count
}
