package scene

import control.PopupMenu
import control.grid.{EditorGrid, Grid}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import main.MainApp.{ConstEditor, ConstMainMenu, setStageBounds}
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ComboBox, TextField}
import scalafx.scene.layout.{Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, StackPane, VBox}
import scalafx.scene.paint.Color.{Black, Gray, Red, Transparent}
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.scene.transform.{Scale, Translate}
import scene.Editor.{BegConstr, ExpConstr, IntConstr, constraintLabelVal, curConstr, enablePivot, selectedCnt, selectorEnabled}
import scene.GameScene.gameOverText.onKeyPressed
import util.Difficulty.Intermediate
import util.{Difficulty, FileReaderWriter, Generator}

object Editor {
  // selecti sluze samo kao indikator koje dugme je pritisnuto (s za select, p za pivot), a ne da li postoji
  // selektovan sektor ili selektovan pivot
  var selectorEnabled = false
  var selectPivotEnabled = false
  var selectedCnt: Int = 0

  // diff, minCol/minRow, maxCol/maxCol, minMines, maxMines,
  val BegConstr: (String, Int, Int, Int, Int) = ("beginner", 8, 15, 10, 30)
  val IntConstr: (String, Int, Int, Int, Int) = ("intermediate", 15, 20, 30, 80)
  val ExpConstr: (String, Int, Int, Int, Int) = ("expert", 16, 30, 80, 150)

  var curConstr = BegConstr
  def constraintLabelVal(constr: (String, Int, Int, Int, Int)): String = {
    curConstr = constr
    s"min rows/cols: ${constr._2}  max row/cols: ${constr._3}  min mines: ${constr._4}  max mines: ${constr._5}"
  }

  def enablePivot(): Unit = {
    clearSelectors()
    selectPivotEnabled = true
  }

  def enableSelector(): Unit = {
    clearSelectors()
    selectorEnabled = true
  }

  def clearSelectors(): Unit = {
    selectorEnabled = false
    selectPivotEnabled = false
  }
}

class Editor {
  val root = new BorderPane
  val buttonBack = new Button("Back")
  private val constraintLabel = new Text(constraintLabelVal(BegConstr))
  private val errorLabel = new Text("")
  private val buttonSave = new Button("Save")
  private val buttonNewRow = new Button("Add First Row")
  private val buttonNewRowLast = new Button("Add Last Row")
  private val buttonNewCol = new Button("Add First Col")
  private val buttonNewColLast = new Button("Add Last Col")
  private val buttonDeleteRow = new Button("Delete First Row")
  private val buttonDeleteRowLast = new Button("Delete Last Row")
  private val buttonDeleteCol = new Button("Delete First Col")
  private val buttonDeleteColLast = new Button("Delete Last Col")
  private val buttonSelector = new Button("Selector(S)")
  private val buttonPivot = new Button("Pivot(P)")
  private val buttonRotate = new Button("Rotate CW")
  private val buttonRotate2 = new Button("Rotate CCW")
  private val buttonReflectHorizontal = new Button("Reflect H")
  private val buttonReflectVertical = new Button("Reflect V")
  private val buttonReflectDiagonal = new Button("Diagonal")
  private val buttonReflectDiagonal2 = new Button("Diagonal2")
  private val grid = new EditorGrid(Generator.generateEmpty(Difficulty.Beginner))
  private val stackPane = new StackPane()
  stackPane.children.add(grid)

  var backClick: () => Unit = () => {
  }

  //comboBox
  var levels: Array[String] = Array("beginner", "intermediate", "expert")
  val comboBox: ComboBox[String] = new ComboBox[String](for (l <- levels) yield l) {
    //    levels(0)
  }
  var chosen: String = "beginner"
  comboBox.promptText = chosen
  comboBox.onAction = _ => {
    chosen = comboBox.value.value
    constraintLabel.text = chosen match {
      case BegConstr._1 => constraintLabelVal(BegConstr)
      case IntConstr._1 => constraintLabelVal(IntConstr)
      case ExpConstr._1 => constraintLabelVal(ExpConstr)
      case _ => ""
    }
  }

  // popup menu
  private val popupMenu = new PopupMenu(200, 150, 10)
  private val textEnter = new Text("Enter level name:")
  private val textField = new TextField()
  private val buttonEnter = new Button("Enter")
  private val buttonCancel = new Button("Cancel")
  popupMenu.children.addAll(textEnter, textField, buttonEnter, buttonCancel)

  // selector
  private val selector: Rectangle = new Rectangle {
    this.width = 40
    this.height = 40
    this.fill = Transparent
    this.stroke = Red
    this.strokeWidth = 2
  }
  private val translate = new Translate(0, 0)
  private val scale = new Scale(1, 1)
  private var posX : Double = 0
  private var posXStart : Double = 0
  private var posY : Double = 0
  private var posYStart : Double = 0
  selector.transforms.addAll(translate, scale)
//  stackPane.children.add(selector)
//  stackPane.border = new Border(new BorderStroke(Gray, BorderStrokeStyle.Solid, new CornerRadii(0), new BorderWidths(2)))

  root.onMousePressed = (e: MouseEvent) => {

    if (selectorEnabled){
      stackPane.children.add(selector)
      selector.setWidth(0)
      selector.setHeight(0)
      posX = e.getX
      posY = e.getY
      posXStart = -(stackPane.getWidth - selector.getWidth) / 2 + e.getX
      posYStart = -(stackPane.getHeight - selector.getHeight) / 2 + e.getY - stackPane.getBoundsInParent.getMinY
      translate.setX(posXStart)
      translate.setY(posYStart)
    }
  }
  root.onMouseDragged = (e: MouseEvent) => {
    if (selectorEnabled){
      scale.setX(if (e.getX -posX < 0) -1 else 1)
      scale.setY(if (e.getY -posY < 0) -1 else 1)

      selector.setWidth(Math.abs(e.getX - posX))
      selector.setHeight(Math.abs(e.getY - posY))

      translate.setX(posXStart + selector.getWidth / 2)
      translate.setY(posYStart + selector.getHeight / 2)
    }
  }
  root.onMouseReleased = (e: MouseEvent) => {
    if (selectorEnabled) {
      stackPane.children.remove(selector)
      selectorEnabled = false
      //selectFields()
    }
  }

  root.top = new VBox(10) {
    children.addAll(new HBox(10) {
      children.addAll(comboBox, constraintLabel)
    },
      new HBox(10) {
      children.addAll(buttonSave, buttonSelector, buttonPivot)
    },
      new HBox(10) {
      children.addAll(buttonNewRow, buttonNewRowLast, buttonNewCol, buttonNewColLast)
    },
      new HBox(10) {
      children.addAll(buttonDeleteRow, buttonDeleteRowLast, buttonDeleteCol, buttonDeleteColLast)
    },
      new HBox(10) {
      children.addAll(buttonRotate, buttonRotate2, buttonReflectHorizontal, buttonReflectVertical)
    },
      new HBox(10) {
      children.addAll(buttonReflectDiagonal, buttonReflectDiagonal2)
    })
  }

  root.center = stackPane
  root.bottom = new VBox(10) {
    children.addAll(errorLabel, buttonBack)
  }

  buttonSave.onMouseClicked = (e: MouseEvent) => {
    stackPane.children.add(popupMenu)
    if (checkConstraints()) {
      textEnter.text = "Enter level name:"
      textEnter.fill = Black
      textField.disable = false
      buttonEnter.disable = false
    } else {
      textEnter.text = "Invalid row, column or mine count!"
      textEnter.fill = Red
      textField.disable = true
      buttonEnter.disable = true
    }
  }
  buttonNewRow.onMouseClicked = (e: MouseEvent) => {
    grid.addRowFront()
  }
  buttonNewRowLast.onMouseClicked = (e: MouseEvent) => {
    grid.addRowBack()
  }
  buttonNewCol.onMouseClicked = (e: MouseEvent) => {
    grid.addColFront()
  }
  buttonNewColLast.onMouseClicked = (e: MouseEvent) => {
    grid.addColBack()
  }
  buttonDeleteRow.onMouseClicked = (e: MouseEvent) => {
    grid.deleteRow(0)
  }
  buttonDeleteRowLast.onMouseClicked = (e: MouseEvent) => {
    grid.deleteRow(grid.cols - 1)
  }
  buttonDeleteCol.onMouseClicked = (e: MouseEvent) => {
    grid.deleteCol(0)
  }
  buttonDeleteColLast.onMouseClicked = (e: MouseEvent) => {
    grid.deleteCol(grid.rows - 1)
  }
  buttonEnter.onMouseClicked = (e: MouseEvent) => {
    saveLevel(textField.text.value)
    stackPane.children.remove(popupMenu)
  }
  buttonCancel.onMouseClicked = (e: MouseEvent) => {
    stackPane.children.remove(popupMenu)
  }
  buttonSelector.onMouseClicked = (e: MouseEvent) => {
    selectorEnabled = true
  }
  buttonPivot.onMouseClicked = (e: MouseEvent) => {
    enablePivot()
  }
  buttonRotate.onMouseClicked = (e: MouseEvent) => {
    grid.rotateSelected()
  }
  buttonRotate2.onMouseClicked = (e: MouseEvent) => {
    grid.rotateSelected(false)
  }
  buttonReflectHorizontal.onMouseClicked = (e: MouseEvent) => {
    grid.reflectSelected(1)
  }
  buttonReflectVertical.onMouseClicked = (e: MouseEvent) => {
    grid.reflectSelected(2)
  }
  buttonReflectDiagonal.onMouseClicked = (e: MouseEvent) => {
    grid.reflectSelected(3)
  }
  buttonReflectDiagonal2.onMouseClicked = (e: MouseEvent) => {
    grid.reflectSelected(4)
  }
  buttonBack.onMouseClicked = (e: MouseEvent) => {
    backClick()
    setStageBounds(ConstMainMenu)
  }

  setStageBounds(ConstEditor)

  def saveLevel(filename: String): Unit = {
      FileReaderWriter.saveLevelRes(filename, grid.matrix, chosen)
  }

  def checkConstraints(): Boolean = {
    val cnt = grid.countMines()

    grid.rows >= curConstr._2 && grid.cols >= curConstr._2 &&
      grid.rows <= curConstr._3 && grid.cols <= curConstr._3 &&
      cnt >= curConstr._4 && cnt <= curConstr._5
  }

  def deselectFields(): Unit = {
    if (grid != null) {
      Editor.clearSelectors()
      grid.deselectFields()
      grid.deselectPivot()
    }
  }

  def clearSelected(): Unit = {
    if (grid != null) {
      grid.clearSelected()
    }
  }

}

// beg   9 x 9 10    81   50 - 100  8 - 15   10 - 30
// inter 16 x 16 40  256            15 - 20  30 - 80
// exp   16 x 30 99  480            16 - 30  80 - 120