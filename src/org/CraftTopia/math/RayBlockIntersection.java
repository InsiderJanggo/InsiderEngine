package org.CraftTopia.math;

import java.util.ArrayList;
import java.util.Collections;

import org.CraftTopia.datastructures.AABB;
import org.CraftTopia.game.Game;

/**
 * Provides support for ray-box intersection tests.
*
* @author Original code byBenjamin Glatzel <benjamin.glatzel@me.com>
* @author Edited by martijncourteaux
*/
public class RayBlockIntersection
{

   private static final Game _game = Game.getInstance();
   
   /**
    * Represents an intersection of a ray with the face of a block.
    *
    * @author Benjamin Glatzel <benjamin.glatzel@me.com>
    */
   public static class Intersection implements Comparable<Intersection>
   {

       private final float _d;
       private final float _t;
       private final Vec3f _rayOrigin, _intersectionPoint, _rayDirection;
       private final Vec3i _surfaceNormal;
       private final int _blockX, _blockY, _blockZ;

       public Intersection(int blockX, int blockY, int blockZ, Vec3i normal, float d, float t, Vec3f rayOrigin, Vec3f rayDirection, Vec3f intersectionPoint)
       {
           this._d = d;
           this._t = t;
           this._rayOrigin = rayOrigin;
           this._rayDirection = rayDirection;
           this._intersectionPoint = intersectionPoint;
           this._surfaceNormal = normal;
           this._blockX = blockX;
           this._blockY = blockY;
           this._blockZ = blockZ;
       }

       @Override
       public int compareTo(Intersection o)
       {
           if (o == null)
           {
               return 0;
           }

           double distance = _t;
           double distance2 = o._t;

           if (distance == distance2)
           {
               return 0;
           }

           return distance2 > distance ? -1 : 1;
       }

       Vec3i getSurfaceNormal()
       {
           return _surfaceNormal;
       }

       public Vec3i calcAdjacentBlockPos()
       {
           Vec3i pos = getBlockPosition();
           pos.add(getSurfaceNormal());

           return pos;
       }

       public Vec3i getBlockPosition()
       {
           return new Vec3i(_blockX, _blockY, _blockZ);
       }

       @Override
       public String toString()
       {
           return String.format("x: %d y: %d z: %d", _blockX, _blockY, _blockZ);
       }

       public float getDistance()
       {
           return _t;
       }
   }

   /**
    * Calculates the intersection of a given ray originating from a specified point with
    * a block. Returns a list of intersections ordered by the distance to the player.
    *
    * @param w            The world provider
    * @param x            The block's position on the x-axis
    * @param y            The block's position on the y-axis
    * @param z            The block's position on the z-axis
    * @param rayOrigin    The origin of the ray
    * @param rayDirection The direction of the ray
    */
   public static ArrayList<Intersection> executeIntersection(int x, int y, int z, AABB blockAABB, Vec3f rayOrigin, Vec3f rayDirection)
   {

       ArrayList<Intersection> result = new ArrayList<Intersection>(2);

       /*
        * Fetch all vertices of the specified block.
        */
       Vec3f[] vertices = blockAABB.getVertices();

       /*
        * Generate a new intersection for each side of the block.
        */

       // Front
       Intersection is = executeBlockFaceIntersection(x, y, z, vertices[0], vertices[1], vertices[3], rayOrigin, rayDirection);
       if (is != null)
       {
           result.add(is);
       }

       // Back
       is = executeBlockFaceIntersection(x, y, z, vertices[4], vertices[7], vertices[5], rayOrigin, rayDirection);
       if (is != null)
       {
           result.add(is);
       }

       // Left
       is = executeBlockFaceIntersection(x, y, z, vertices[4], vertices[0], vertices[7], rayOrigin, rayDirection);
       if (is != null)
       {
           result.add(is);
       }

       // Right
       is = executeBlockFaceIntersection(x, y, z, vertices[5], vertices[6], vertices[1], rayOrigin, rayDirection);
       if (is != null)
       {
           result.add(is);
       }

       // Top
       is = executeBlockFaceIntersection(x, y, z, vertices[7], vertices[3], vertices[6], rayOrigin, rayDirection);
       if (is != null)
       {
           result.add(is);
       }

       // Bottom
       is = executeBlockFaceIntersection(x, y, z, vertices[4], vertices[5], vertices[0], rayOrigin, rayDirection);
       if (is != null)
       {
           result.add(is);
       }

       // Sort the intersections by distance to the player
       Collections.sort(result);
       return result;
   }

   /**
    * Calculates an intersection with the face of a block defined by three points.
    *
    * @param blockPos The position of the block to intersect with
    * @param v0       Point 1
    * @param v1       Point 2
    * @param v2       Point 3
    * @param origin   Origin of the intersection ray
    * @param ray      Direction of the intersection ray
    * @return Ray-face-intersection
    */
   private static Intersection executeBlockFaceIntersection(int blockX, int blockY, int blockZ, Vec3f v0, Vec3f v1, Vec3f v2, Vec3f origin, Vec3f ray)
   {

       // Calculate the plane to intersect with
       Vec3f a = new Vec3f(v1);
       a.sub(v0);
       Vec3f b = new Vec3f(v2);
       b.sub(v0);
       Vec3f norm = new Vec3f();
       norm.cross(a, b);

       float d = -(norm.x() * v0.x() + norm.y() * v0.y() + norm.z() * v0.z());

       /**
        * Calculate the distance on the ray, where the intersection occurs.
        */
       float t = -(norm.x() * origin.x() + norm.y() * origin.y() + norm.z() * origin.z() + d) / Vec3f.dot(ray, norm);

       // No intersection possible
       if (t < 0)
       {
           return null;
       }

       /**
        * Calc. the point of intersection.
        */
       Vec3f intersectPoint = new Vec3f((ray.x() * t), (ray.y() * t), (ray.z() * t));
       intersectPoint.add(origin);

       /**
        * Check if the point lies on block's face.
        */
       if (intersectPoint.x() >= v0.x() && intersectPoint.x() <= Math.max(v1.x(), v2.x()) && intersectPoint.y() >= v0.y() && intersectPoint.y() <= Math.max(v1.y(), v2.y()) && intersectPoint.z() >= v0.z() && intersectPoint.z() <= Math.max(v1.z(), v2.z()))
       {
           return new Intersection(blockX, blockY, blockZ, new Vec3i((int) norm.x(), (int) norm.y(), (int) norm.z()), d, t, origin, ray, intersectPoint);
       }

       // Point of intersection was NOT lying on the block's face
       return null;
   }
}
