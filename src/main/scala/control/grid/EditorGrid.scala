package control.grid

import control.counter.EmptyFieldCounter
import control.field.{EditorField, Field, GameField}
import control.field.GameField.{colorMine, colorempty, numColor}
import javafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane
import scene.Editor

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

class EditorGrid(m: Array[Array[EditorField]]) extends GridPane {
  var matrix: Array[Array[EditorField]] = m
  var pressedField: (Int, Int) = (0, 0)
  var releasedField: (Int, Int) = (0, 0)
  var pivotField: (Int, Int) = (0, 0)
  var pivotSelected = false

  fillGrid()

  def rows: Int = matrix.length

  def cols: Int = matrix(0).length

  onMousePressed = (e: MouseEvent) => handlePress(e)
  onMouseReleased = (e: MouseEvent) => handleRelease(e)

  private def handlePress(e: MouseEvent) = {
    if (e.isPrimaryButtonDown) {
      if (Editor.selectorEnabled) {
        pressedField = ((e.getX / Field.size).toInt, (e.getY / Field.size).toInt)
      } else if (Editor.selectPivotEnabled) {
        if (matrix(pivotField._1)(pivotField._2).pivot) matrix(pivotField._1)(pivotField._2).deselectPivot()
        pivotField = ((e.getX / Field.size).toInt, (e.getY / Field.size).toInt)
        matrix(pivotField._1)(pivotField._2).setPivot()
        Editor.clearSelectors()
        pivotSelected = true
      }
    }
  }

  private def handleRelease(e: MouseEvent) = {
    if (Editor.selectorEnabled) {
      releasedField = (Math.floor(e.getX / Field.size).toInt, Math.floor(e.getY / Field.size).toInt)
      //      println("pressed: " + pressedField)
      //      println("released: " + releasedField)
      //      println("released" + (e.getY, e.getX))
      deselectFields()
      selectFields()
    }
  }

  def fillGrid(): Unit = {
    this.children.clear()
    for ((arr, ind1) <- matrix.zipWithIndex; (elem, ind2) <- arr.zipWithIndex) {
      //      elem.rectangle.fill = colorempty
      addElem(elem, ind1, ind2)
    }
  }

  def addElem(elem: EditorField, ind1: Int, ind2: Int): Unit = {
    add(elem, ind1, ind2)
    elem.col = ind1
    elem.row = ind2
  }

  def addColBack(): Unit = {
    val newRow = Array.fill(matrix(0).length)(new EditorField)
    this.matrix = this.matrix :+ newRow
    fillGrid()
  }

  def addColFront(): Unit = {
    val newRow = Array.fill(matrix(0).length)(new EditorField)
    this.matrix = newRow +: this.matrix
    fillGrid()
  }

  def addRowBack(): Unit = {
    matrix = for (arr <- matrix) yield arr :+ new EditorField
    fillGrid()
  }

  def addRowFront(): Unit = {
    matrix = for (arr <- matrix) yield new EditorField +: arr
    fillGrid()
  }

  def deleteCol(row: Int): Unit = {
    matrix = (for (i <- matrix.indices if i != row) yield matrix(i)).toArray
    fillGrid()
  }

  def deleteRow(col: Int): Unit = {
    matrix = (for (i <- matrix.indices) yield (for (j <- matrix(i).indices; if j != col) yield matrix(i)(j)).toArray).toArray
    fillGrid()
  }

  private def selectFields(): Unit = {
    println(pressedField + " " + releasedField)
    val startX = if (pressedField._1 > releasedField._1) releasedField._1 else pressedField._1
    val endX = if (pressedField._1 > releasedField._1) pressedField._1 else releasedField._1
    val startY = if (pressedField._2 > releasedField._2) releasedField._2 else pressedField._2
    val endY = if (pressedField._2 > releasedField._2) pressedField._2 else releasedField._2
    for (i <- startX until endX; j <- startY until endY) {
      try {
        matrix(i)(j).select()
      } catch {
        case e: IndexOutOfBoundsException =>
      }
    }
  }

  //TODO: moze da se optimizuje
  def deselectFields(): Unit = {
    for (arr <- matrix; elem <- arr) {
      if (elem.selected)
        elem.deselect()
    }
  }

  def deselectPivot(): Unit = {
    pivotSelected = false
    val (x, y) = pivotField
    matrix(x)(y).pivot = false
    matrix(x)(y).setMine(matrix(x)(y).mine)
  }

  def clearSelected(): Unit = {
    for (arr <- matrix; elem <- arr) {
      if (elem.selected) {
        elem.mine = false
        elem.deselect()
      }
    }
  }

  def rotateSelected(clockwise: Boolean = true): Unit = {
    if (pivotSelected) {
      val list: ListBuffer[EditorField] = new ListBuffer[EditorField]()
      if (matrix(pivotField._1)(pivotField._2).pivot) {
        for (i <- pressedField._1 until releasedField._1; j <- releasedField._2 - 1 until pressedField._2 - 1 by -1) {
          list.addOne(matrix(i)(j).copy())
          matrix(i)(j).setMine(false)
        }

        deselectFields()

        var minX = matrix(0).length
        var minY = matrix.length
        var maxX = 0
        var maxY = 0

        for (l <- list) {
          val x = if (clockwise) {
            pivotField._1 + pivotField._2 - l.row
          } else {
            pivotField._1 - pivotField._2 + l.row
          }
          val y = if (clockwise) {
            -pivotField._1 + pivotField._2 + l.col
          } else {
            pivotField._1 + pivotField._2 - l.col
          }

          maxX = if (x > maxX) x else maxX
          maxY = if (y > maxY) y else maxY
          minX = if (x < minX) x else minX
          minY = if (y < minY) y else minY
          matrix(x)(y).setMine(l.mine)
          matrix(x)(y).select()
        }

        pressedField = (minX, minY)
        releasedField = (maxX + 1, maxY + 1)
      }
    }
  }

  // direction:
  // horizontal = 1
  // vertical = 2
  // diagonal = 3
  // diagonal2 = 4
  def reflectSelected(direction: Int = 1): Unit = {
    if (pivotSelected) {
      val list: ListBuffer[EditorField] = new ListBuffer[EditorField]()
      if (matrix(pivotField._1)(pivotField._2).pivot) {
        for (i <- pressedField._1 until releasedField._1; j <- releasedField._2 - 1 until pressedField._2 - 1 by -1) {
          list.addOne(matrix(i)(j).copy())
          matrix(i)(j).setMine(false)
        }

        deselectFields()

        var minX = matrix(0).length
        var minY = matrix.length
        var maxX = 0
        var maxY = 0

        for (l <- list) {
          val x = if (direction == 1) {
            pivotField._1 * 2 - l.col
          } else if (direction == 2) {
            l.col
          } else if (direction == 3) {
            pivotField._1 - pivotField._2 + l.row
          } else /*if (direction == 4) */{
            pivotField._1 + pivotField._2 - l.row
          }
          val y = if (direction == 1) {
            l.row
          } else if (direction == 2) {
            pivotField._2 * 2 - l.row
          } else if (direction == 3){
            - pivotField._1 + pivotField._2 + l.col
          } else /*if (direction == 4) */{
            pivotField._1 + pivotField._2 - l.col
          }

          maxX = if (x > maxX) x else maxX
          maxY = if (y > maxY) y else maxY
          minX = if (x < minX) x else minX
          minY = if (y < minY) y else minY
          matrix(x)(y).setMine(l.mine)
          matrix(x)(y).select()
        }

        pressedField = (minX, minY)
        releasedField = (maxX + 1, maxY + 1)
      }
    }
  }


  def countMines(): Int = {
    @tailrec
    def countMinesTail(c: Int, r: Int, accCol: Int, accRow: Int, accCnt: Int): Int = {
      (accCol < r, accRow < c) match {
        case (false, false) => accCnt
        case (false, _) => countMinesTail(c, r, 0, accRow + 1, if (matrix(accCol)(accRow).mine) accCnt + 1 else accCnt)
        case _ => countMinesTail(c, r, accCol + 1, accRow, if (matrix(accCol)(accRow).mine) accCnt + 1 else accCnt)
      }
    }

    countMinesTail(this.cols - 1, this.rows - 1, 0, 0, 0)
  }



}

// 1 2 3
// 4 5 6
// 7 8 9

// 1 2 3 4 5 6 7 8 9
// 7 4 1 8 5 2 9 6 3


