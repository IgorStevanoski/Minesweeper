package scene

import control.counter.{ClickCounter, HintCounter}
import control.Timer
import javafx.scene.input.MouseEvent
import main.MainApp.{ConstLeaderBoard, ConstMainMenu, setStageBounds}
import scalafx.geometry.Pos
import scalafx.scene.Group
import scalafx.scene.control.{Button, ComboBox}
import scalafx.scene.layout.{BorderPane, GridPane, VBox}
import scalafx.scene.text.Text
import scalafx.scene.text.TextAlignment.Center
import scene.Leaderboard.maxChar
import scene.MainScene.{fontMedium, fontTitle, sceneWidthLeaderBoard}
import util.Difficulty.{Beginner, Intermediate}
import util.{Difficulty, FileReaderWriter, Generator}

import scala.collection.mutable.ListBuffer


object Leaderboard {
  val colCnt = 6
  val maxChar = 20

  def addNewScore(playerName: String): Unit = {
    val data = new ListBuffer[String]()
    data.addOne(Difficulty.currentDifficultyName()) // levelDifficulty
    data.addOne(GameScene.currentLevel) // levelName
    data.addOne(playerName) // playerName
    data.addOne(Timer.time.toString) // Time
    data.addOne(ClickCounter.count.toString) // Clicks
    data.addOne(HintCounter.count.toString) // Helps

    FileReaderWriter.insertIntoLeaderboard(
      "src/main/resources/files/leaderboard.txt",
      data.toArray
    )
  }
}

// format: levelDifficulty, levelName, playerName, time, clicks, helps
class Leaderboard() {
  val root = new BorderPane
  val errorLabel = new Text("")
  val vboxTop = new VBox(10)
  vboxTop.alignment = Pos.Center
  val title = new Text("Leaderboard")
  title.textAlignment = Center
  title.alignmentInParent = Pos.Center
  title.font = fontTitle
  val playerText = new Text("Player"){font = fontMedium}
  val timeText = new Text("Time"){font = fontMedium}
  val clicksText = new Text("Clicks"){font = fontMedium}
  val helpsText = new Text("Helps"){font = fontMedium}
  val lb : Array[Array[String]] = FileReaderWriter.readfLeaderboard("src/main/resources/files/leaderboard.txt")
  var levels: Array[String] = Array("")
  var chosen = ""
  val buttonBack = new Button("Back")

  var backClick: () => Unit = () => {
  }

  root.top = vboxTop
  vboxTop.children.add(title)

  root.bottom = new VBox(10) {
    alignment = Pos.Center
    minWidth = sceneWidthLeaderBoard
    children.addAll(errorLabel, buttonBack)
  }
  try {
    if (lb.isEmpty){
      errorLabel.text = "Leaderboard is empty."
    } else {
      levels = (for (elem <- lb) yield elem(1)).distinct
      val comboBox = new ComboBox[String](for (l <- levels) yield l) {
        levels(0)
      }
      chosen = levels(0)
      comboBox.promptText = chosen
      comboBox.onAction = _ => {
        chosen = comboBox.value.value
        scoresByLevel(chosen)
      }

      vboxTop.children.add(comboBox)
      scoresByLevel(chosen)
    }
  } catch {
    case e: IndexOutOfBoundsException =>  errorLabel.text = "Leaderboard file is corrupted."
  }

  buttonBack.onMouseClicked = (e: MouseEvent) => {
    backClick()
    setStageBounds(ConstMainMenu)
  }
  timeText.onMouseClicked = (e: MouseEvent) => {
    scoresByLevel(chosen)
  }
  clicksText.onMouseClicked = (e: MouseEvent) => {
    scoresByLevel(chosen, false)
  }

  setStageBounds(ConstLeaderBoard)

  def scoresByLevel(levelName: String, sortByTime: Boolean = true): Unit = {
    val sortedLb = if (sortByTime) lb.sortBy { case Array(_, _, _, time, _, _) => time.toInt }
    else lb.sortBy { case Array(_, _, _, _, clicks, _) => clicks.toInt }

    try {
      val grid = new GridPane(10, 10)

      grid.add(playerText, 2, 1) // playerName
      grid.add(timeText, 3, 1) // time
      grid.add(clicksText, 4, 1) // clicks
      grid.add(helpsText, 5, 1) // helps

      var index = 2
      for (l <- sortedLb) if (l(1) == levelName) {
        grid.add(new Text(l(2).padTo(maxChar, ' ')), 2, index) // playerName
        grid.add(new Text(l(3)), 3, index) // time
        grid.add(new Text(l(4)), 4, index) // clicks
        grid.add(new Text(l(5)), 5, index) // helps
        index += 1
      }
      root.center = grid
    } catch {
      case e: IndexOutOfBoundsException => errorLabel.text = "Leaderboard file is corrupted."
    }
  }


}
