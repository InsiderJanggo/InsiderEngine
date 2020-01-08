package org.CraftTopia.utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.CraftTopia.math.Vec3f;

public class IOUtilities
{
	
	
	public static void writeVec3f(DataOutputStream dos, Vec3f vec) throws IOException
	{
		dos.writeFloat(vec.x());
		dos.writeFloat(vec.y());
		dos.writeFloat(vec.z());
	}

	public static void readVec3f(DataInputStream dis, Vec3f vec) throws IOException
	{
		vec.x(dis.readFloat());
		vec.y(dis.readFloat());
		vec.z(dis.readFloat());
	}

}
