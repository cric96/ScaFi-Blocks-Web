package it.unibo.scafi.js.view.dynamic

import org.scalatest.funspec.{AnyFunSpec, AsyncFunSpec}
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Promise

class PageBusTest extends AsyncFunSpec with Matchers {

  describe("The event bus") {
    it("able to listen and publish event") {
      val event = "Ciao"
      val promise = Promise[String]()
      PageBus.listen {
        case word : String => promise.success(event)
        case _ => promise.failure(new IllegalArgumentException)
      }
      PageBus.publish(event)
      promise.future.map { case `event` => succeed }
    }
  }
}
