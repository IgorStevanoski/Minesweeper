package scene

import control.counter.{ClickCounter, EmptyFieldCounter, HintCounter, MineCounter}
import control.{PopupMenu, Timer}
import control.grid.Grid
import control.grid.Grid.started
import scalafx.scene.{Group, Scene, SubScene}
import scalafx.scene.control.{Button, ComboBox, TextField}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import main.MainApp.{ConstMainMenu, setStageBounds}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos.Center
import scalafx.scene.layout.{Background, Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, FlowPane, HBox, StackPane, VBox}
import util.{Difficulty, FileReaderWriter, Generator, Sequencer}
import scalafx.scene.paint.Color.{DarkGray, Gray, Green, LightGray, Red}
import scalafx.scene.text.{Font, Text}
import scene.GameScene.{buttonSave, currentLevel, gameOverText, hidePopupMenu, popupMenu, stackPane}
import util.Difficulty.{Beginner, currentDifficulty}

object GameScene {
  var currentLevel = "level1"
  val gameOverText: Text = new Text("")
  val buttonSave = new Button("Save Level")
  gameOverText.font = new Font(20)
  gameOverText.fill = Green
  var gameOver: Boolean = false
  var gameLoaded: Boolean = false

  val popupMenu = new PopupMenu(200, 150, 10)
  val stackPane = new StackPane()

  def finishGame(): Unit = {
    gameOverText.text = "Kraj igre!"
    gameOver = true
    buttonSave.disable = true
    if (EmptyFieldCounter.count == 0) showPopupMenu()
    Timer.stop()
  }

  def startGame(): Unit = {
    buttonSave.disable = false
    gameOverText.text = ""
    gameOver = false
    if (!gameLoaded) {
      Timer.reset()
      ClickCounter.resectCnt()
      HintCounter.resectCnt()
    } else {
      Timer.start()
      gameLoaded = false
    }
    //    Timer.start()
  }

  def showPopupMenu(): Unit = {
    stackPane.children.add(popupMenu)
  }

  def hidePopupMenu(): Unit = {
    stackPane.children.remove(popupMenu)
  }
}

class GameScene(var grid: Grid) extends Scene {
  fill = Gray

  val group: FlowPane = new FlowPane(10, 10)
  var timer: Text = Timer.timer
  var clickCounter: Text = ClickCounter.counter
  var hintCounter: Text = HintCounter.counter
  var mineCounter: Text = MineCounter.counter
  var emptyFieldCounter: Text = EmptyFieldCounter.counter
  var showLeaderBoard: () => Unit = () => {
  }
  var mainMenuClick: () => Unit = () => {
  }
  this.root = group

  val buttonLoad = new Button("Load Level")
  val buttonBeginner = new Button("New Beginner")
  val buttonIntermediate = new Button("New Intermediate")
  val buttonExpert = new Button("New Expert")
  val buttonHint = new Button("Hint")
  val buttonLB = new Button("LeaderBoard")
  val buttonSeq = new Button("Play Sequence")
  val buttonMenu = new Button("Main Menu")

  // popup menu
  val textEnter = new Text("Enter your name:")
  val textField = new TextField()
  val buttonEnter = new Button("Enter")
  val buttonPlayAgain = new Button("Play again")

  popupMenu.children.clear()
  popupMenu.children.addAll(textEnter, textField, buttonEnter, buttonPlayAgain)

  buttonEnter.onMouseClicked = (e: MouseEvent) => {
    Leaderboard.addNewScore(textField.text.value)
    showLeaderBoard()
  }
  buttonPlayAgain.onMouseClicked = (e: MouseEvent) => {
    hidePopupMenu()
  }

  buttonSave.onMouseClicked = (e: MouseEvent) => {
    FileReaderWriter.saveGameRes("savedLevel.txt", grid.matrix)
  }
  buttonLoad.onMouseClicked = (e: MouseEvent) => {
    grid = new Grid(FileReaderWriter.readfRes("savedLevel.txt"))
    grid.updateLoadedGrid()
    started = true
    Timer.stop()
    resetGroup()
  }
  buttonBeginner.onMouseClicked = (e: MouseEvent) => {
    grid = new Grid(Generator.generateByDifficulty(Difficulty.Beginner))
    Difficulty.currentDifficulty = Beginner
    currentLevel = "beginner"
    Timer.stop()
    resetGroup()
    setStageBounds()
  }
  buttonIntermediate.onMouseClicked = (e: MouseEvent) => {
    grid = new Grid(Generator.generateByDifficulty(Difficulty.Intermediate))
    currentLevel = "intermediate"
    Timer.stop()
    resetGroup()
    setStageBounds()
  }
  buttonExpert.onMouseClicked = (e: MouseEvent) => {
    grid = new Grid(Generator.generateByDifficulty(Difficulty.Expert))
    currentLevel = "expert"
    resetGroup()
    setStageBounds()
  }
  buttonHint.onMouseClicked = (e: MouseEvent) => {
    showHint()
  }
  buttonSeq.onMouseClicked = (e: MouseEvent) => {
    playSequenceFromFile("sequence.txt")
  }

  buttonMenu.onMouseClicked = (e: MouseEvent) => {
    mainMenuClick()
    setStageBounds(ConstMainMenu)
  }

  setStageBounds()
  resetGroup()

  private def resetGroup(): Unit = {
    group.children.clear()
    stackPane.children.clear()
    //    Sequencer.resetSequence()
    setGroup()
  }

  private def setGroup(): Unit = {
    stackPane.children.addAll(grid)
    gameOverText.text = ""

    group.children.add(new VBox(10) {
      children.addAll(
        new HBox(10) {
          children.addAll(buttonBeginner, buttonIntermediate, buttonExpert)
        },
        new HBox(10) {
          children.addAll(buttonSave, buttonLoad)
        },
        new HBox(10) {
          children.addAll(buttonLB, buttonHint, buttonSeq)
        },
        stackPane,
        new HBox(10) {
          children.addAll(timer, clickCounter, hintCounter, mineCounter, emptyFieldCounter)
        },
        new HBox(10) {
          children.addAll(buttonMenu, gameOverText)
        },
      )
    })

//    group.children.addAll(buttonBeginner, buttonIntermediate, buttonExpert, /*comboBox,
//      buttonLoadEditorLevel,*/ buttonSave, buttonLoad, buttonLB, buttonHint, buttonSeq /*buttonEditor,*/)
//    group.children.add(stackPane)
//    group.children.add(timer)
//    group.children.addAll(clickCounter, hintCounter, mineCounter, emptyFieldCounter, gameOverText, buttonMenu)
    MineCounter.setCnt(grid.mines)
    EmptyFieldCounter.setCnt(grid.fields)
    GameScene.startGame()
  }

  private def showHint(): Unit = {
    //    if (Timer.time > 0)
    HintCounter.increaseCnt()
    grid.showHint(ClickCounter.count == 0)
  }

  private def playSequenceFromFile(filename: String): Unit = {
    Sequencer.loadSequence(filename)
    grid.playSequence()
  }

}
