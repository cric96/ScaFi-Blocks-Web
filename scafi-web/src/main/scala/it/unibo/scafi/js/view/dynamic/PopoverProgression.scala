package it.unibo.scafi.js.view.dynamic

import it.unibo.scafi.js.view.dynamic.Popover.Direction
import scalatags.JsDom.all._

import scala.scalajs.js

trait PopoverProgression {

  /** Show next [[Popover]]. */
  def stepForward()
}

object PopoverProgression {

  trait Builder {
    /**
      * Add a step popover.
      *
      * @param attachTo  the id of the element to attach the popover to
      * @param title     the title of the popover modal
      * @param text      the body of the popover modal
      * @param direction the direction of the popover arrow
      */
    def addNextPopover(attachTo: String, title: String, text: String, direction: Direction = Popover.Bottom): Builder

    def andFinally(action: () => Unit): Builder

    /** Start the tour. */
    def start(): PopoverProgression
  }

  object Builder {
    def apply(): Builder = new PopoverProgressionBuilder()

    private sealed class PopoverProgressionBuilder extends Builder {
      private val popoverTour: PopoverTour = PopoverTour(Seq.empty)

      /** @inheritdoc */
      override def addNextPopover(attachTo: String, title: String, text: String, direction: Direction = Popover.Bottom): Builder = {
        val nextBtn = button(cls := "btn btn-primary btn-sm ml-1 mr-1", `type` := "button", "OK").render
        nextBtn.onclick = _ => popoverTour.stepForward()
        popoverTour.popovers = popoverTour.popovers :+ Popover(attachTo, data = div(
          p(text),
          nextBtn
        ).render, title, direction)
        this
      }

      /** @inheritdoc */
      override def start(): PopoverProgression = popoverTour

      override def andFinally(action: () => Unit): Builder = {
        popoverTour.doFinally = Some(action)
        this
      }
    }

    private sealed case class PopoverTour(var popovers: Seq[Popover]) extends PopoverProgression {
      private var iterator: Option[Iterator[Popover]] = None
      private var current: Option[Popover] = None
      var doFinally: Option[() => Unit] = None

      /** Show next [[Popover]]. */
      override def stepForward(): Unit = {
        (iterator, current) match {
          case (None, _) | (_, None) =>
            val iter = popovers.iterator
            iterator = Some(iter)
            val cur = iter.next()
            current = Some(cur)
            cur.show()
          case (Some(iter), Some(curr)) if iter.hasNext =>
            curr.hide()
            val next = iter.next()
            current = Some(next)
            next.show()
          case (Some(iter), Some(curr)) if !iter.hasNext =>
            curr.hide()
            current = None
            doFinally match {
              case Some(action) => action()
            }
        }
      }
    }
  }
}