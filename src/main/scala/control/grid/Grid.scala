package control.grid

import control.counter.{ClickCounter, EmptyFieldCounter}
import control.field.GameField
import control.field.GameField.{colorMine, colorempty, numColor}
import control.grid.Grid.{countMinesAndFields, initMat, started}
import scalafx.scene.layout.GridPane
import util.Sequencer

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

class Grid(m: Array[Array[GameField]]) extends GridPane {
  val matrix: Array[Array[GameField]] = m

  val rowCnt: Int = matrix.length
  val colCnt: Int = matrix(0).length

//  var minesAndFields: (Int, Int) = countMinesAndFields(matrix)
  var mines: Int = countMinesAndFields(matrix)._1
  var fields: Int = countMinesAndFields(matrix)._2

  started = false
  fillGrid()

  def this(matrixString: String) = {
    this(initMat(matrixString))
  }

  def fillGrid(): Unit = {
    for ((arr, ind1) <- matrix.zipWithIndex; (elem, ind2) <- arr.zipWithIndex) {
      addElem(elem, ind1, ind2)
    }
  }
  def addElem(elem: GameField, ind1: Int, ind2: Int): Unit = {
    add(elem, ind2, ind1)
    elem.row = ind1
    elem.col = ind2
    elem.checkMines = checkMine
    elem.mineClicked = mineClicked
  }

  /** Za zadate koordinate od Field-a racuna broj okruzujucih mina.*/
  def countMines(row: Int, col: Int): Int = {
    var c = 0
    for (i <- row - 1 to row + 1; j <- col - 1 to col + 1) {
      if (i >= 0 && i < matrix.length && j >= 0 && j < matrix(i).length) {
        if (matrix(i)(j).hasMine) {
          c += 1
        }
      }
    }
    c
  }

  def checkMine(row: Int, col: Int): Int = {
    @tailrec
    def checkMineTail(row: Int, col: Int, acc: ListBuffer[GameField]): Int = {
      var n = 0
      val field = matrix(row)(col)

      if (!field.clicked) {
        n = countMines(row, col)
        // Mora prvi klik na ostrvo
        if (n != 0 && ClickCounter.count == 1 && !started){
          shiftMines(row, col)
          n = 0
        }
        started = true
        //
        field.setFill (if (field.hasMine) colorMine else colorempty)
        field.clicked = true
        field.text.text = if (n > 0) n.toString else ""
        field.text.fill = numColor(n)
        field.children.remove(field.image)
        field.children.add(field.text)
        EmptyFieldCounter.decreaseCnt()
        if (n == 0) {
          for (i <- row - 1 to row + 1; j <- col - 1 to col + 1) {
            if (i >= 0 && i < matrix.length && j >= 0 && j < matrix(i).length) {
              if (!matrix(i)(j).clicked)
                acc.addOne(matrix(i)(j))
            }
          }
        }
      }
      if (!acc.isEmpty) {
        val field = acc.remove(0)
        checkMineTail(field.row, field.col, acc)
      } else n
    }

    checkMineTail(row, col, ListBuffer[GameField]())
  }

  def shiftMines(row: Int, col: Int): Unit = {
    for (i <- row - 1 to row + 1; j <- col - 1 to col + 1) {
      try {
        while (matrix(i)(j).hasMine) {
          val indrow = (Math.random() * rowCnt).toInt
          val indcol = (Math.random() * colCnt).toInt

          if ((indrow < row - 1 || indrow > row + 1) && (indcol < col - 1 || indcol > col + 1)) {
            if (!matrix(indrow)(indcol).hasMine) {
              matrix(indrow)(indcol).mine = true
              matrix(i)(j).mine = false
            }
          }
        }
      } catch {
        case e: IndexOutOfBoundsException =>
      }
    }
  }

  /** Poziva se kada se klikne na minu da se otkriju sve preostale.*/
  def mineClicked(): Unit = {
    for (r <- matrix; c <- r){
      if (c.flagged && !c.hasMine) c.flagFieldWrong()
      else if (c.hasMine) c.setFill(GameField.colorMine)
      c.clicked = true
    }
  }

  def showHint(noClicks: Boolean): Unit = {
    for ((r,row) <- matrix.zipWithIndex; (c, col) <- r.zipWithIndex) {
      if (noClicks && !c.hasMine) {
        c.rectangle.fill = GameField.colorHint
        return
      }
      if (c.clicked) {
        if (c.text.text.value != ""){
          for (i <- row - 1 to row + 1; j <- col - 1 to col + 1) {
            if (i >= 0 && i < matrix.length && j >= 0 && j < matrix(i).length) {
              if (!matrix(i)(j).clicked && !matrix(i)(j).hasMine) {
                matrix(i)(j).rectangle.fill = GameField.colorHint
                return
              }
            }
          }
        }
      }
    }
  }

  /** Poziva se nakog load-ovanja sacuvane igre. */
  def updateLoadedGrid(): Unit = {
    for (arr <- matrix; elem <- arr) {
      elem.checkAfterLoad()
      if (elem.flagged) mines -=1
      if (elem.clicked) fields -=1
    }
  }

  def playSequence(): Unit = {
    for (tuple <- Sequencer.sequence){
      if (tuple._1 == "D") matrix(tuple._2.toInt)(tuple._3.toInt).rightClick()
      else  matrix(tuple._2.toInt)(tuple._3.toInt).leftClick()
    }
//    val t = new java.util.Timer()
//    val task = new java.util.TimerTask {
//      def run() = {
//        if (Sequencer.sequence.nonEmpty){
//          var tuple = Sequencer.sequence.remove(0)
//          if (tuple._1 == "D") matrix(tuple._2.toInt)(tuple._3.toInt).rightClick()
//          else matrix(tuple._2.toInt)(tuple._3.toInt).leftClick()
//        }
//      }
//    }
//    t.schedule(task, 0L, 100L)
  }
}

object Grid {
  val charMine: Char = '#'    // # -> neotvoreno polje sa minom
  val charEmpty: Char = '-'   // - -> neotvoreno polje prazno
  val charClicked: Char = 'x' // x -> otvoreno polje
  val charMineFlagged: Char = 'y' // z -> obelezeno polje sa minom zastavom
  val charEmptyFlagged: Char = 'z' // z -> obelezeno polje prazno zastavom

  // Koristi se da se proveri prvi klik
  var started: Boolean = false

  def countColsAndRows(matrixString: String): (Int, Int) = {
    var maxCol = 0
    var row = 1
    var col = 0

    for (i <- 0 until matrixString.length) {
      if (matrixString(i) != charMine &
        matrixString(i) != charEmpty &
        matrixString(i) != charClicked &
        matrixString(i) != charMineFlagged &
        matrixString(i) != charEmptyFlagged &
        matrixString(i) != '\n') throw new IllegalArgumentException()
      else if (matrixString(i) == '\n') {
        if (maxCol == 0) maxCol = col
        else if (maxCol != col) throw new IllegalArgumentException()
        col = 0
        row += 1
      } else {
        col += 1
      }
    }

    (row, maxCol)
  }

  def countMinesAndFields(matrixString: String): (Int, Int) = {
    var mines = 0
    var fields = 0
    for (i <- 0 until matrixString.length) {
      if (matrixString(i)== '#') mines += 1
      if (matrixString(i)== '-') fields += 1
    }

    (mines, fields)
  }

  def countMinesAndFields(matrix: Array[Array[GameField]]): (Int, Int) = {
    var mines = 0
    var fields = 0
    for ((arr, ind1) <- matrix.zipWithIndex; (elem, ind2) <- arr.zipWithIndex) {
      if (matrix(ind1)(ind2).hasMine) mines += 1
      else fields += 1
    }

    (mines, fields)
  }

  def initMat(matrixString: String): Array[Array[GameField]] = {
    val colsRows = countColsAndRows(matrixString)
    val rows = colsRows._1
    val cols = colsRows._2

    val arr = for (i <- 0 until matrixString.length if matrixString(i) != '\n')
      yield matrixString(i) match {
        case `charMine` => new GameField(true)
        case `charEmpty` => new GameField(false)
        case `charClicked` => new GameField(false, true, false)
        case `charEmptyFlagged` => new GameField(false, false, true)
        case `charMineFlagged` => new GameField(true, false, true)
        case _ => new GameField(false)
      }
    //if (matrixString(i) == '-') new Field(false) else new Field(true)

    val mat: Array[Array[GameField]] = Array.ofDim[GameField](rows, cols)

    for (i <- 0 until rows) {
      for (j <- 0 until cols) {
        mat(i)(j) = arr(i * cols + j)
        mat(i)(j).row = i
        mat(i)(j).col = i
      }
    }

    mat
  }
}