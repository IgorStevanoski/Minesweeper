package control

import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.{Background, Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, StackPane, VBox}
import scalafx.scene.paint.Color.{Gray, LightGray}
import scalafx.geometry.Pos.Center
import javafx.scene.Node

class PopupMenu (width: Int, height: Int, space: Int) extends VBox(space){

  // Inicijalno se ne vidi popupMenu, mora da se pozove showPopupMenu
//  val popupMenu = new VBox(space)
//  popupMenu.maxHeight = width
//  popupMenu.maxWidth = height
//  popupMenu.background = Background.fill(LightGray)
//  popupMenu.alignment = Center
//  popupMenu.border = new Border(new BorderStroke(Gray, BorderStrokeStyle.Solid, new CornerRadii(5), new BorderWidths(2)))
    this.maxHeight = width
    this.maxWidth = height
    this.background = Background.fill(LightGray)
    this.alignment = Center
    this.border = new Border(new BorderStroke(Gray, BorderStrokeStyle.Solid, new CornerRadii(5), new BorderWidths(2)))

//  def showPopupMenu(): Unit = {
//    this.children.add(popupMenu)
//  }
//
//  def hidePopupMenu(): Unit = {
//    this.children.remove(popupMenu)
//  }
//
//  def addChildren(children: Node*): Unit = {
//    popupMenu.children.addAll(children)
//  }
}
