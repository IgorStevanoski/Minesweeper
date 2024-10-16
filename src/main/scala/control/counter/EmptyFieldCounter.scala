package control.counter

import scene.GameScene

object EmptyFieldCounter extends Counter {
  override val labelText: String = "Empty fields: "
  updateText()

  override def decreaseCnt() = {
    super.decreaseCnt()
    if (count == 0) GameScene.finishGame()
  }
}