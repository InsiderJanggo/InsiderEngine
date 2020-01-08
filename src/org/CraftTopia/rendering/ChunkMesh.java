package org.CraftTopia.rendering;

import org.CraftTopia.rendering.ChunkMeshBuilder.MeshType;

public class ChunkMesh
{
	private int _vertexCount[];
	private int[] _vbos;

	public ChunkMesh(int[] vertexCount, int[] vbos)
	{
		super();
		this._vertexCount = vertexCount;
		this._vbos = vbos;
	}

	public ChunkMesh()
	{
		_vbos = new int[MeshType.values().length];
		_vertexCount = new int[MeshType.values().length];
	}

	public void setVBO(MeshType meshType, int vbo)
	{
		this._vbos[meshType.ordinal()] = vbo;
	}

	public void setVertexCount(MeshType meshType, int vertexCount)
	{
		this._vertexCount[meshType.ordinal()] = vertexCount;
	}

	public int getVBO(MeshType meshType)
	{
		return _vbos[meshType.ordinal()];
	}

	public int getVertexCount(MeshType meshType)
	{
		return _vertexCount[meshType.ordinal()];
	}

	public synchronized void destroy(MeshType meshType)
	{
		if (_vbos[meshType.ordinal()] != 0 && _vbos[meshType.ordinal()] != -1)
		{
			BufferManager.getInstance().deleteBuffer(_vbos[meshType.ordinal()]);
			_vbos[meshType.ordinal()] = 0;
			_vertexCount[meshType.ordinal()] = 0;
		}
	}

	public void destroyAllMeshes()
	{
		destroy(MeshType.OPAQUE);
		destroy(MeshType.TRANSLUCENT);
	}
}