package control.field

import control.Timer
import control.counter.{ClickCounter, MineCounter}
import control.field.GameField._
import control.grid.Grid
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Black, Blue, DarkBlue, DarkGray, DarkRed, Gray, Green, LightGray, LightGreen, Red, Teal, White}
import scalafx.scene.text.Text
import scene.GameScene

object GameField {
  val size = Field.size
  val colorempty = Field.colorempty
  val colorMine = Field.colorMine
  val colorHint = Field.colorHint
  val color = Field.color
  val strokeColor = Field.strokeColor

  def numColor(n: Int): Color = n match {
    case 1 => Blue
    case 2 => Green
    case 3 => Red
    case 4 => DarkBlue
    case 5 => DarkRed
    case 6 => Teal
    case 7 => Black
    case _ => White
  }
}

class GameField(var mine: Boolean = false, cl: Boolean = false, fl: Boolean = false) extends Field {
//  var row = 0
//  var col = 0
  var checkMines: (Int, Int) => Int = (x, y) => x + y
  var mineClicked: () => Unit = () => {}
  var flagged: Boolean = fl
  var clicked: Boolean = false

  val text: Text = new Text {
    text = "1"
    fill = Black
    x = size / 3
    y = size / 3 * 2
  }

  var image = new ImageView()

  children.add(rectangle)

  if (fl) flagField()

  /** Poziva se nakon load-ovanja igre.*/
  def checkAfterLoad(): Unit = {
    if (cl) {
      rectangle.fill = colorempty
      checkMines(row, col)
    }
  }

  def hasMine = mine

  override def toString() = if (hasMine) "#" else "-"

//  def handleClick(e: MouseEvent) = {
//    if (Timer.stopped && Timer.time == 0) Timer.start()
//    if (e.isPrimaryButtonDown) {
//      leftClick()
//    } else if (e.isSecondaryButtonDown) {
//      rightClick()
//    }
//  }

  def leftClick(): Unit = {
    if (!flagged && !clicked) {
      if (Timer.stopped && Timer.time == 0) Timer.start()
      ClickCounter.increaseCnt()
      rectangle.fill = if (hasMine) colorMine else colorempty
      // ne proverava se da li je bomba ako jos nije zapoceto, jer ce se ona ukloniti u chechMines
      if (!hasMine || !Grid.started) {
        //Sequencer.addToSequence((row.toString, col.toString))
        checkMines(row, col)
      } else {
        GameScene.finishGame()
        mineClicked()
        clicked = true
      }
    }
  }

  def rightClick(): Unit = {
      if (!clicked && ClickCounter.count > 0){
        ClickCounter.increaseCnt()
        if (!flagged){
          MineCounter.decreaseCnt()
          flagField()
        } else {
          MineCounter.increaseCnt()
          children.clear()
          children.add(rectangle)
        }
        flagged = !flagged
      }
  }

  def flagField(): Unit = {
//    val flagUrl = getClass.getResource("/images/Flag.png")
//    val flagImg = new Image(flagUrl.toString)
//    image = new ImageView(flagImg) {
//      fitWidth = size
//      fitHeight = size
//      preserveRatio = true
//    }
//
//    children.add(image)
    setImage("/images/Flag.png")
  }

  // Poziva se kada je polje pogresno obelezeno
  def flagFieldWrong(): Unit = {
    setImage("/images/Flag2.png")
  }

  def setImage(imageUrl: String): Unit = {
    children.remove(image)
    val flagUrl = getClass.getResource(imageUrl)
    val flagImg = new Image(flagUrl.toString)
    image = new ImageView(flagImg) {
      fitWidth = size
      fitHeight = size
      preserveRatio = true
    }
    children.add(image)
  }

//  def setFill(col: Color) = rectangle.fill = col
}
