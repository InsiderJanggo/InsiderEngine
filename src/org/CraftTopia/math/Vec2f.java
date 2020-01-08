package org.CraftTopia.math;

public class Vec2f
{
	private float x, y;

	/**
	 * Cached value for the length of the vector
	 */
	private float len;

	/**
	 * Constructs a new Vec2i with this value: (0, 0)
	 */
	public Vec2f()
	{
		this(0, 0);
	}

	public Vec2f(float x, float y)
	{
		set(x, y);
	}

	/**
	 * Constructs a new Vec3f and copies the values of the passed vector.
	 * 
	 * @param v
	 *            the vector to be copied
	 */
	public Vec2f(Vec2f v)
	{
		this(v.x, v.y);
	}

	public void set(float x, float y)
	{
		if (this.x != x || this.y != y)
			clearCache();
		this.x = x;
		this.y = y;
	}

	private void clearCache()
	{
		this.len = Float.NaN;
	}

	/**
	 * Uses cache.
	 * 
	 * @return the squared length of this vector
	 */
	public float lengthSquared()
	{
		return x * x + y * y;
	}

	/**
	 * Uses cache.
	 * 
	 * @return the length of this vector
	 */
	public float length()
	{
		if (Float.isNaN(len))
		{
			len = (float) Math.sqrt(lengthSquared());
		}
		return len;
	}

	/**
	 * Performs a scalar product on this vector
	 * 
	 * @param factor
	 * @return {@code this}
	 */
	public Vec2f scale(float factor)
	{
		/* Backup the length */
		float len = this.len;

		set(x * factor, y * factor);

		/* Restore the length */
		if (!Float.isNaN(len))
		{
			this.len = len * factor;
		}

		return this;
	}

	/**
	 * Subtracts this vector with the passed vector.
	 * 
	 * @param v
	 *            the vector to subtract from this
	 * @return {@code this}
	 */
	public Vec2f sub(Vec2f v)
	{
		set(x - v.x, y - v.y);
		return this;
	}

	/**
	 * Adds the passed vector to this vector
	 * 
	 * @param v
	 *            the vector to add
	 * @return {@code this}
	 */
	public Vec2f add(Vec2f v)
	{
		set(x + v.x, y + v.y);
		return this;
	}

	public float x()
	{
		return x;
	}

	public float y()
	{
		return y;
	}

	public void setX(float x)
	{
		if (this.x != x)
			clearCache();
		this.x = x;
	}

	public void setY(float y)
	{
		if (this.y != y)
			clearCache();
		this.y = y;
	}

	public void x(float x)
	{
		setX(x);
	}

	public void y(float y)
	{
		setY(y);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2f other = (Vec2f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}
	
	
}
