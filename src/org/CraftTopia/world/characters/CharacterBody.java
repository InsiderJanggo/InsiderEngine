package org.CraftTopia.world.characters;


import org.CraftTopia.game.Game;
import org.lwjgl.opengl.GL11;

/**
 * 
 * @author martijncourteaux
 */
public class CharacterBody
{

	protected boolean _usingRightHand;
	protected float _progressRightHand;
	protected boolean _dirRightHand;
	protected float _animationSpeedRightHand;
	protected float _blockDistance;

	public void enableUsingRightHand()
	{
		_usingRightHand = true;
	}

	public void disableUsingRightHand()
	{
		_usingRightHand = false;
	}

	public void update()
	{
		updateRightHand();
	}

	public void updateRightHand()
	{
		if (_usingRightHand)
		{
			if (_dirRightHand)
			{
				_progressRightHand -= Game.getInstance().getStep() * _animationSpeedRightHand;
				if (_progressRightHand < 0.0f)
				{
					_dirRightHand = !_dirRightHand;
				}
			} else
			{
				_progressRightHand += Game.getInstance().getStep() * _animationSpeedRightHand;
				if (_progressRightHand > 1.0f)
				{
					_dirRightHand = !_dirRightHand;
				}
			}
		} else
		{
			if (_progressRightHand > 0)
			{
				_progressRightHand -= Game.getInstance().getStep() * _animationSpeedRightHand * 0.9f;
				if (_progressRightHand < 0)
				{
					_dirRightHand = true;
				}
			}
		}
	}

	public void airSmash()
	{
		_usingRightHand = true;
		_progressRightHand = 0.1f;
	}

	public void setBlockDistance(float distance)
	{
		this._blockDistance = distance;
	}

	public void forceDisableUsingRightHand()
	{
		disableUsingRightHand();
		_progressRightHand = 0.0f;
		_dirRightHand = false;
	}

	public void transformToRightHand()
	{
		/* Transform the matix */
		GL11.glLoadIdentity();

		GL11.glTranslatef(0.2f - _progressRightHand / 10.0f, -0.05f, -0.2f - _progressRightHand / 20.0f * +(_progressRightHand * _blockDistance * 1.2f));
		GL11.glRotatef(-65, 0, 1, 0);
		GL11.glRotated(_progressRightHand * 50 - 10.0d, 0, 0, 1);
		GL11.glRotated(_progressRightHand * 20, 1, 1, 0);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glRotatef(20, 0, 1, 0);
	}
}
