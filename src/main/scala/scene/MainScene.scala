package scene

import control.grid.Grid
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.FlowPane
import scalafx.scene.text.Font
import scene.GameScene.gameOverText.scene
import util.{Difficulty, FileReaderWriter, Generator}

object MainScene {
  val sceneWidthMain = 250
  val sceneHeightMain = 250
  val sceneWidthEditor = 550
  val sceneHeightEditor = 650
  val sceneWidthLeaderBoard = 300
  val sceneHeightLeaderBoard = 400
  val fontTitle = new Font(20)
  val fontMedium = new Font(15)
}

class MainScene extends Scene {
  val group: FlowPane = new FlowPane(10, 10)
  this.root = group

  showMainMenu()

  private def showGame(level: String, randomLevel: Boolean, loadedLevel: Boolean = false): Unit = {
    val grid = if (loadedLevel) new Grid(FileReaderWriter.readfRes("savedLevel.txt"))
    else if (randomLevel) new Grid(Generator.generateByDifficulty(Difficulty.difficultyByName(level)))
    else new Grid(FileReaderWriter.readfResEditor(level))

    if (!randomLevel) Grid.started = true

    val game = new GameScene(grid)
    game.showLeaderBoard = showLeaderBoard
    game.mainMenuClick = showMainMenu
    GameScene.currentLevel = if (!loadedLevel) level.toLowerCase() else level
    if (loadedLevel) grid.updateLoadedGrid()

    group.children.clear()
    group.children.add(game.group)
  }

  private def showLeaderBoard(): Unit = {
    val leaderboard = new Leaderboard()
    leaderboard.backClick = showMainMenu
    group.children.clear()
    group.children.add(leaderboard.root)
  }

  private def showMainMenu(): Unit = {
    val mainMenu = new MainMenu()
    mainMenu.buttonNewGame.onMouseClicked = (e: MouseEvent) => {
      showNewGame()
    }
    mainMenu.buttonContinue.onMouseClicked = (e: MouseEvent) => {
      showGame("", false, true)
    }
    mainMenu.buttonEditor.onMouseClicked = (e: MouseEvent) => {
      showEditor()
    }
    mainMenu.buttonLeaderboard.onMouseClicked = (e: MouseEvent) => {
      showLeaderBoard()
    }
    group.children.clear()
    group.children.add(mainMenu.root)
  }

  private def showNewGame(): Unit = {
    val newGame = new NewGame()
    newGame.buttonBeginner.onMouseClicked = (e: MouseEvent) => {
      showLevelChoose("Beginner")
    }
    newGame.buttonIntermediate.onMouseClicked = (e: MouseEvent) => {
      showLevelChoose("Intermediate")
    }
    newGame.buttonExpert.onMouseClicked = (e: MouseEvent) => {
      showLevelChoose("Expert")
    }
    newGame.buttonBack.onMouseClicked = (e: MouseEvent) => {
      showMainMenu()
    }
    group.children.clear()
    group.children.add(newGame.root)
  }

  private def showLevelChoose(title: String): Unit = {
    Difficulty.currentDifficulty = Difficulty.difficultyByName(title)
    val levelChoose = new LevelChoose()
    levelChoose.title.text = title + " difficulty"
    levelChoose.buttonRandom.onMouseClicked = (e: MouseEvent) => {
      showGame(title, randomLevel = true)
    }
    levelChoose.buttonPlayChosen.onMouseClicked = (e: MouseEvent) => {
      showGame(levelChoose.chosen, randomLevel = false)
    }
    levelChoose.buttonBack.onMouseClicked = (e: MouseEvent) => {
      showMainMenu()
    }
    group.children.clear()
    group.children.add(levelChoose.root)
  }

  private def showEditor(): Unit = {
    val editor = new Editor()
    editor.backClick = showMainMenu

    group.children.clear()
    group.children.add(editor.root)
    onKeyPressed = (event: KeyEvent) => {
      event.getCode match {
        case KeyCode.S => Editor.enableSelector()
        case KeyCode.D => editor.clearSelected()
        case KeyCode.X => editor.clearSelected()
        case KeyCode.P => Editor.enablePivot()
        case KeyCode.ESCAPE => editor.deselectFields()
        case _ =>
      }
    }
  }
}
