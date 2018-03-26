/*
 * Copyright (C) 2016-2017, Roberto Casadei, Mirko Viroli, and contributors.
 * See the LICENCE.txt file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package it.unibo.scafi.lib

import scala.concurrent.duration._

trait StdLib_TimeUtils {
  self: StandardLibrary.Subcomponent =>

  trait BlockT {
    self: FieldCalculusSyntax =>

    def T[V](initial: V, floor: V, decay: V => V)
            (implicit ev: Numeric[V]): V =
      rep(initial) { v =>
        ev.min(initial, ev.max(floor, decay(v)))
      }

    def T[V](initial: V, decay: V => V)
            (implicit ev: Numeric[V]): V =
      T(initial, ev.zero, decay)

    def T[V](initial: V)
            (implicit ev: Numeric[V]): V =
      T(initial, (t: V) => ev.minus(t, ev.one))

    def T[V](initial: V, dt: V)
            (implicit ev: Numeric[V]): V =
      T(initial, (t: V) => ev.minus(t, dt))

    def timer[V: Numeric](length: V): V =
      T(length)

    def limitedMemory[V, T](value: V, expValue: V, timeout: T)
                           (implicit ev: Numeric[T]): (V, T) = {
      val t = timer[T](timeout)
      (if (ev.gt(t, ev.zero)) value else expValue, t)
    }

    def timer(dur: Duration): Long = {
      val ct = System.nanoTime()
      // Current time
      val et = ct + dur.toNanos // Time-to-expire (bootstrap)

      rep((et, dur.toNanos)) { case (expTime, remaining) =>
        if (remaining <= 0) (et, 0) else (expTime, expTime - ct)
      }._2 // Selects the component expressing remaining time
    }

    def recentlyTrue(dur: Duration, cond: => Boolean): Boolean =
      rep(false) { happened =>
        branch(cond) {
          true
        } {
          branch(!happened) {
            false
          } {
            timer(dur) > 0
          }
        }
      }

    /*
    * Returns an value representing the current clock
    */
    def sharedTimerWithDecay[T](period: T, dt: T)(implicit ev: Numeric[T]): T =
      rep(ev.zero) { clock =>
        val clockPerceived = foldhood(clock)(ev.max)(nbr(clock))
        if (ev.compare(clockPerceived, clock) <= 0) {
          // I'm currently as fast as the fastest device in the neighborhood, so keep on counting time
          ev.plus(clock, (if(cyclicTimerWithDecay(period, dt)) { ev.one } else { ev.zero }))
        } else {
          // Someone else's faster, take his time, and restart counting
          clockPerceived
        }
      }

    /**
      * Cyclic timer.
      *
      * @param length timeout
      * @param decay  decay rate
      * @return       true if the timeout is expired, false otherwise
      */
    def cyclicTimerWithDecay[T](length: T, decay: T)(implicit ev: Numeric[T]): Boolean =
      rep(length){ left =>
        if (left == ev.zero) {
          length
        } else {
          T(length, decay)
        }
      } == length

    def impulsesEvery[T : Numeric](d: T): Boolean =
      rep(false){ impulse =>
        branch(impulse) { false } { timer(d)==0 }
      }

    def impulsesEvery(d: FiniteDuration): Boolean =
      rep(false){ impulse =>
        branch(impulse) { false } { timer(d)==0 }
      }

    /**
      * It is a simple building block which returns the same values
      *  it receives in input delayed by one computation round.
      */
    def delay[T](value: T): T = {
      var res = value
      rep(value){ old => res = old; value }
      res
    }
  }

  trait TimeUtils extends BlockT { self: FieldCalculusSyntax with StandardSensors =>
    def sharedTimer(period: FiniteDuration): FiniteDuration =
      sharedTimerWithDecay(period.toMillis, deltaTime().toMillis) seconds
  }

}