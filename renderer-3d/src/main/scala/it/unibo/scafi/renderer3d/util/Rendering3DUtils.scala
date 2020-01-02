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

package it.unibo.scafi.renderer3d.util

import it.unibo.scafi.renderer3d.util.RichScalaFx._
import org.fxyz3d.geometry.{Point3D => FxPoint3D}
import org.fxyz3d.shapes.primitives.FrustumMesh
import org.scalafx.extras._
import scalafx.geometry.Point3D
import scalafx.scene.paint.{Color, Material, PhongMaterial}
import scalafx.scene.shape.{Box, DrawMode, Sphere}
import scalafx.scene.text.{Font, Text}
import scalafx.scene.{AmbientLight, CacheHint, Node}

/** This contains methods to create the elements of the 3d JavaFx scene such as labels, cubes, spheres, lines, etc. */
object Rendering3DUtils {
  private var materialCache: Map[Color, Material] = Map()

  /** Creates a light that illuminates the whole scene with a constant illumination, without any low light parts.
   * @return the ambient light */
  def createAmbientLight: AmbientLight = new AmbientLight()

  /** Creates a 2d text label that can also be used and rotated in a 3d scene.
   * @param textString the text that should be displayed
   * @param fontSize the font size to be used
   * @param position the position where the text label should be placed
   * @return the text label */
  def createText(textString: String, fontSize: Int, position: Point3D): Text = {
    val label = new Text(){
      font = new Font(fontSize)
      text = textString
    }
    label.moveTo(position)
    optimize(label) match {case label: Text => label}
  }

  /** Creates a 3d cube.
   * @param size the length of the side of the cube
   * @param color the color of the cube
   * @param position the position where the cube should be placed
   * @return the cube */
  def createCube(size: Double, color: Color, position: Point3D = Point3D.Zero): Box = {
    val box = new Box(size, size, size)
    box.setColor(color)
    box.moveTo(position)
    optimize(box) match {case box: Box => box}
  }

  /** Creates a sphere that is rendered as a wireframe, with lines linking consecutive vertices, colored with a half
   * transparent gray.
   * @param radius the desired radius of the sphere
   * @param position the position where the sphere should be placed
   * @return the sphere */
  def createOutlinedSphere(radius: Double, position: Point3D): Sphere = {
    val SPHERE_BRIGHTNESS = 100 //out of 255
    val SPHERE_OPACITY = 0.5
    val color = Color.rgb(SPHERE_BRIGHTNESS, SPHERE_BRIGHTNESS, SPHERE_BRIGHTNESS, SPHERE_OPACITY)
    createSphere(radius, color, position, drawOutlineOnly = true)
  }

  /** Creates a black sphere where the polygonal faces are rendered as solid surfaces.
   * @param radius the desired radius of the sphere
   * @param position the position where the sphere should be placed
   * @param highQuality whether the sphere should have lots of divisions or not
   * @return the sphere */
  def createFilledSphere(radius: Double, position: Point3D, highQuality: Boolean = false): Sphere =
    if(highQuality) {
      val SPHERE_DIVISIONS = 32
      val sphere = new Sphere(radius, SPHERE_DIVISIONS)
      sphere.moveTo(position)
      optimize(sphere) match {case sphere: Sphere => sphere}
    } else {
      createSphere(radius, Color.Black, position, drawOutlineOnly = false)
    }

  /** Creates a sphere that can be rendered as outline or as a filled sphere.
   * @param radius the desired radius of the sphere
   * @param color the desired color of the sphere
   * @param position the position where the sphere should be placed
   * @param drawOutlineOnly if it is true only the sphere will be rendered as wireframe
   * @return the sphere */
  def createSphere(radius: Double, color: Color, position: Point3D, drawOutlineOnly: Boolean): Sphere = {
    val MESH_DIVISIONS = 5 //this is low for performance reasons
    val sphere = new Sphere(radius, MESH_DIVISIONS) {material = createMaterial(color)}
    sphere.moveTo(position)
    if(drawOutlineOnly) sphere.setDrawMode(DrawMode.Line)
    optimize(sphere) match {case sphere: Sphere => sphere}
  }

  /** Creates a material given a color. Caching has been used, since the same materials can be requested many times.
   * @param color the chosen color
   * @return the material */
  def createMaterial(color: Color): Material = onFXAndWait {
    materialCache.getOrElse(color, {
      val material = new PhongMaterial {diffuseColor = color; specularColor = color}
      materialCache += (color -> material)
      material
    })
  }

  /** Creates a 3d line as a really thin cylinder.
   * @param points the start and end 3d points of the line
   * @param visible whether the line should be already visible or not
   * @param color the chosen color
   * @return the 3d line */
  def createLine(points: (Point3D, Point3D), visible: Boolean, color: java.awt.Color,
                 thickness: Double): FrustumMesh = {
    val line = new FrustumMesh(thickness, thickness, 1, 0, toFXyzPoint(points._1), toFXyzPoint(points._2))
    line.setColor(color)
    line.setVisible(visible)
    line
  }

  /** Updates the position, rotation and height of the line so that it connects the two provided points.
   * @param line the line to modify
   * @param point1 the first point
   * @param point2 the second point */
  def connectLineToPoints(line: FrustumMesh, point1: Point3D, point2: Point3D): Unit =
  {line.setAxisOrigin(toFXyzPoint(point1)); line.setAxisEnd(toFXyzPoint(point2))}

  private def toFXyzPoint(point: Point3D): org.fxyz3d.geometry.Point3D = new FxPoint3D(point.x, point.y, point.z)

  private final def optimize(node: Node): Node = {node.cache = true; node.setCacheHint(CacheHint.Speed); node}
}
