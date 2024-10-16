package control.field

import control.field.Field.{color, colorMine, colorPivot, colorPivotMine, colorSelected, colorSelectedMine, colorempty}
import scene.Editor.{selectPivotEnabled, selectedCnt, selectorEnabled}

class EditorField extends Field {
  var mine = false
  var selected = false
  var pivot = false

  children.add(rectangle)
  setFill(colorempty)

  def this(row: Int, col: Int, mine: Boolean) = {
    this()
    this.row = row
    this.col = col
    this.mine = mine
  }

  override def leftClick(): Unit = {
    if (selectorEnabled || selectPivotEnabled) {
      //      pressedField = (row, col)
    } else {
      setMine(!mine)
    }
  }

  override def rightClick(): Unit = {}

  def select(): Unit = {
    //setFill(if (mine) colorSelectedMine else colorSelected)
    if (!selected) {
      selectedCnt += 1
      selected = true
    }
    setMine(mine)
  }

  def deselect(): Unit = {
    setFill(if (mine) colorMine else colorempty)
    if (selected) {
      selectedCnt -= 1
      selected = false
    }
  }

  def deselectPivot(): Unit = {
    pivot = false
    setMine(mine)
  }

  // Ako mine = true postavlja se mine, u suprotnom se postavlja prazno polje
  // TODO: moze switch
  def setMine(mine: Boolean): Unit = {
    this.mine = mine
    if (pivot) setFill(if (mine) colorPivotMine else colorPivot)
    else if (selected) setFill(if (mine) colorSelectedMine else colorSelected)
    else setFill(if (mine) colorMine else colorempty)
  }

  def setPivot(): Unit = {
    setFill(if (mine) colorPivotMine else colorPivot)
    pivot = true
  }

  override def toString() = super.toString() + (if (mine) "x" else "-")

  def copy(): EditorField = new EditorField(row, col, mine)
}
