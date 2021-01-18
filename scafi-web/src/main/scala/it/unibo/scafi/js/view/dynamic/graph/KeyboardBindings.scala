package it.unibo.scafi.js.view.dynamic.graph

import it.unibo.scafi.js.controller.local.SimulationCommand.ToggleSensor
import it.unibo.scafi.js.controller.local.SupportConfiguration
import it.unibo.scafi.js.facade.phaser.Phaser
import it.unibo.scafi.js.facade.phaser.Phaser.Scene
import it.unibo.scafi.js.facade.phaser.namespaces.EventsNamespace.Handler1
import it.unibo.scafi.js.facade.phaser.namespaces.input.KeyboardNamespace.Events.{DOWN, UP}
import it.unibo.scafi.js.facade.phaser.namespaces.input.KeyboardNamespace.KeyCodes._
import it.unibo.scafi.js.utils.Nullable
import it.unibo.scafi.js.utils._
import it.unibo.scafi.js.view.dynamic.EventBus
import it.unibo.scafi.js.view.dynamic.graph.Interaction.{Pan, Selection}

import scala.scalajs.js

class KeyboardBindings(interaction : Interaction) {
  private var sensors : Seq[String] = Seq()
  private val keys = List(ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE)
  var scene : Nullable[Scene] = _
  EventBus.listen {
    case SupportConfiguration(_, _, deviceShape, _, _) => sensors = deviceShape.sensors
      .filter {
        case (_, _ : Boolean) => true
        case _ => false
      }
      .map { case (name, _) => name }.toSeq
      initSensorKeys()
  }

  def init(scene : Scene) : Unit = {
    this.scene = scene
    val altKey = scene.input.keyboard.get.addKey(Phaser.Input.Keyboard.KeyCodes.ALT)
    val ctrlKey = scene.input.keyboard.get.addKey(Phaser.Input.Keyboard.KeyCodes.CTRL)
    altKey.on(DOWN, (a : Any, _ : Any) => interaction.changeTo(Selection))
    ctrlKey.on(DOWN, (a : Any, _ : Any) => interaction.changeTo(Pan))
    initSensorKeys()
  }

  private def initSensorKeys() : Unit = {
    this.scene.foreach(scene => {
      keys.foreach(scene.input.keyboard.get.removeKey(_, destroy = true))
      keys.zip(sensors).
        map { case (key, name) => name -> scene.input.keyboard.get.addKey(key) }
        .foreach { case (sensor, key) => key.on(DOWN, onClickDown(sensor)) }
    })
  }
  private def onClickDown(sensor : String) : Handler1[Scene] = (obj, event : js.Object) => {
    val ids = interaction.selection.map(_.toSet).getOrElse(Set())
    interaction.commandInterpreter.execute(ToggleSensor(sensor, ids))
  }
}
