package org.CraftTopia.rendering;

mport static org.lwjgl.opengl.GL11.*;

import org.CraftTopia.datastructures.ViewFrustum;
import org.CraftTopia.math.MathHelper;
import org.CraftTopia.math.Vec3f;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

/**
 * 
 * @author martijncourteaux
 */
public class Camera
{

	private Vec3f position;
	private Vec3f lookDirection;
	private Vec3f up;
	private Vec3f right;
	private float bobbing;
	
	private float x, y, z;
	/** Rotations of the camera, in radians */
	private float rotX, rotY;

	/** Angle of the scene, taken by this camera. Defined in degrees */
	private float fovy;

	private ViewFrustum viewFrustum;

	public Camera()
	{
		viewFrustum = new ViewFrustum();
		position = new Vec3f();
		lookDirection = new Vec3f();
		up = new Vec3f(0, 1, 0);
		right = new Vec3f();
	}

	public Camera(Camera cam)
	{
		this();
		x = cam.x;
		y = cam.y;
		z = cam.z;
		rotX = cam.rotX;
		rotY = cam.rotY;
		fovy = cam.fovy;
		viewFrustum = new ViewFrustum();
	}

	public ViewFrustum getViewFrustum()
	{
		return viewFrustum;
	}

	public void lookThrough(float viewingDistance)
	{
		// Change to projection matrix.
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		// Perspective.
		float widthHeightRatio = (float) Display.getWidth() / (float) Display.getHeight();
		GLU.gluPerspective(fovy, widthHeightRatio, 0.1f, viewingDistance);

		// Change back to model view matrix.
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		GLU.gluLookAt(x, y + Math.abs(bobbing) * 3.5f, z, x + lookDirection.x(), y + lookDirection.y() + Math.abs(bobbing) * 3.5f, z + lookDirection.z(), up.x() + right.x(), up.y() + right.y(), up.z() + right.z());

		viewFrustum.updateFrustum();
	}

	public void setPosition(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.position.set(x, y, z);
	}

	public void setRotation(float rotX, float rotY, float bobbing)
	{
		this.rotX = rotX;
		this.rotY = rotY;
		
		this.lookDirection.set(MathHelper.cos(rotY), MathHelper.tan(rotX), -MathHelper.sin(rotY));
		this.right.cross(lookDirection, up);
		this.right.scale(bobbing * 0.6f);
		
		this.bobbing = bobbing;
	}

	public void setFovy(float fovy)
	{
		this.fovy = fovy;
	}

	public float getFovy()
	{
		return fovy;
	}

	public float getRotX()
	{
		return rotX;
	}

	public float getRotY()
	{
		return rotY;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getZ()
	{
		return z;
	}

	public Vec3f getPosition()
	{
		return position;
	}

	public Vec3f getLookDirection()
	{
		return lookDirection;
	}

}
