package control.field

import control.field.Field.size
import control.field.GameField.{color, strokeColor}
import javafx.scene.input.MouseEvent
import scalafx.scene.Group
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.{Blue, DarkGray, DeepSkyBlue, Gray, Green, LightBlue, LightGray, LightGreen, Red}
import scalafx.scene.shape.Rectangle

object Field {
  val size = 30
  val colorempty = LightGray
  val colorMine = Red
  val colorHint = LightGreen
  val color = DarkGray
  val colorSelected = LightGreen
  val colorSelectedMine = Green
  val colorPivot = DeepSkyBlue
  val colorPivotMine = Blue
  val strokeColor = Gray
}

trait Field extends Group{
  var row = 0
  var col = 0

  val rectangle: Rectangle = new Rectangle {
    this.width = size
    this.height = size
    this.fill = color
    this.stroke = strokeColor
  }

  onMousePressed = (e: MouseEvent) => handleClick(e)

  def handleClick(e: MouseEvent) = {
    if (e.isPrimaryButtonDown) {
      leftClick()
    } else if (e.isSecondaryButtonDown) {
      rightClick()
    }
  }

  def leftClick()

  def rightClick()

  def setFill(col: Color) = rectangle.fill = col

  override def toString() = "(" + col + ", " + row + ")"
}
