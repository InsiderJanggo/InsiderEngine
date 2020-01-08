package org.CraftTopia.inventory;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.CraftTopia.Side;
import org.CraftTopia.blocks.BlockType;
import org.CraftTopia.blocks.CrossedBlockBrush;
import org.CraftTopia.blocks.DefaultBlockBrush;
import org.CraftTopia.game.TextureStorage;
import org.CraftTopia.math.MathHelper;
import org.CraftTopia.math.Vec2f;
import org.CraftTopia.math.Vec3f;
import org.newdawn.slick.opengl.Texture;

/**
 * 
 * @author martijncourteaux
 */
public class InventoryImageCreator
{

	private int _size;
	private Texture _terrainTexture;
	private BufferedImage _terrainImage;
	private final BufferedImage NOT_AVAILABLE;

	public InventoryImageCreator(int size)
	{
		_size = size;
		_terrainTexture = TextureStorage.getTexture("terrain");
		_terrainImage = textureToBufferedImage(_terrainTexture);
		NOT_AVAILABLE = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Default size 32 * 32
	 */
	public InventoryImageCreator()
	{
		this(32);
	}

	public BufferedImage createInventoryImage(BlockType type)
	{
		if (type.getBrush() == null)
		{
			return NOT_AVAILABLE;
		}
		if (type.getCustomInventoryImage() != null && type.getCustomInventoryImageTexture() != null)
		{
			Texture tex = TextureStorage.getTexture(type.getCustomInventoryImageTexture());
			BufferedImage img = textureToBufferedImage(tex);
			img = getTile16(img, type.getCustomInventoryImage().x(), type.getCustomInventoryImage().y());
			return img;
		} else
		{
			if (type.isCrossed())
			{
				return createInventoryImage(type.getCrossedBlockBrush());
			} else
			{
				return createInventoryImage(type.getDefaultBlockBrush());
			}
		}
	}

	/**
	 * Creates an inventory image of a crossed block brush
	 * 
	 * @param dbb
	 *            The CrossedBlockBrush for which an inventory image will be
	 *            created
	 * @return
	 */
	public BufferedImage createInventoryImage(CrossedBlockBrush cbb)
	{
		BufferedImage bi = new BufferedImage(_size, _size, BufferedImage.TYPE_INT_ARGB);

		Vec2f texpos = cbb.getTexturePosition();
		BufferedImage tile = getTile16(_terrainImage, MathHelper.round(texpos.x() / 16f * _terrainImage.getWidth()), MathHelper.round(texpos.y() / 16f * _terrainImage.getHeight()));

		Graphics2D g = bi.createGraphics();
		g.drawImage(tile, 0, 0, bi.getWidth(), bi.getHeight(), null);
		g.dispose();

		return bi;
	}

	/**
	 * Creates an inventory image of a default block brush
	 * 
	 * @param dbb
	 *            The DefaultBlockBrush for which an inventory image will be
	 *            created
	 * @return
	 */
	public BufferedImage createInventoryImage(DefaultBlockBrush dbb)
	{

		// Changed from TYPE_4BYTES_ABGR to TYPE_INT_ARGB
		BufferedImage bi = new BufferedImage(_size, _size, BufferedImage.TYPE_INT_ARGB);

		Vec2f leftPos = dbb.getTexturePositionInGridFor(Side.LEFT);
		Vec2f topPos = dbb.getTexturePositionInGridFor(Side.TOP);
		Vec2f frontPos = dbb.getTexturePositionInGridFor(Side.FRONT);

		Vec3f leftColor = dbb.getColorFor(Side.LEFT);
		Vec3f topColor = dbb.getColorFor(Side.TOP);
		Vec3f frontColor = dbb.getColorFor(Side.FRONT);

		Graphics2D g = bi.createGraphics();
		g.scale(_size / 16.0f, _size / 16.0f);
		// Left
		{
			AffineTransform transform = new AffineTransform();
			transform.translate(0, 3.72f);
			transform.scale(0.5, 0.5);
			transform.shear(0.0, 0.36);
			BufferedImage leftSide = getTile16(_terrainImage, (int) leftPos.x(), (int) leftPos.y());
			leftSide = applyColorFilter(leftSide, leftColor.x(), leftColor.y(), leftColor.z());
			g.drawImage(leftSide, transform, null);
		}
		// Front
		{
			AffineTransform transform = new AffineTransform();
			transform.translate(8, 6.62f);
			transform.scale(0.5, 0.5);
			transform.shear(0.0, -0.36);
			BufferedImage frontSide = getTile16(_terrainImage, (int) frontPos.x(), (int) frontPos.y());
			frontSide = applyColorFilter(frontSide, frontColor.x(), frontColor.y(), frontColor.z());
			g.drawImage(frontSide, transform, null);
		}
		// Top
		{
			AffineTransform transform = new AffineTransform();
			transform.scale(16.0d / 22.6d, 0.26);
			transform.translate(22.6d / 2.0f, 3);
			transform.rotate(Math.PI / 4.0d);

			BufferedImage topSide = getTile16(_terrainImage, (int) topPos.x(), (int) topPos.y());
			topSide = applyColorFilter(topSide, topColor.x(), topColor.y(), topColor.z());
			g.drawImage(topSide, transform, null);
		}

		g.dispose();

		return bi;
	}

	private static BufferedImage textureToBufferedImage(Texture t)
	{
		int w = t.getTextureWidth();
		int h = t.getTextureHeight();
		byte[] data = t.getTextureData();
		boolean hasAlpha = t.hasAlpha();
		int bytesPerPixel = data.length / (w * h);
		int imageFormat = 0;
		System.out.println("Bytes per pixel = " + bytesPerPixel);
		System.out.println("Has Alpha = " + hasAlpha);
		if (hasAlpha)
		{
			if (bytesPerPixel == 4)
			{
				imageFormat = BufferedImage.TYPE_4BYTE_ABGR;
			} else
			{
				imageFormat = BufferedImage.TYPE_CUSTOM;
			}
		} else
		{
			if (bytesPerPixel == 3)
			{
				imageFormat = BufferedImage.TYPE_3BYTE_BGR;
			} else
			{
				imageFormat = BufferedImage.TYPE_CUSTOM;
			}
		}

		BufferedImage bi = new BufferedImage(w, h, imageFormat);

		for (int x = 0; x < w; ++x)
		{
			for (int y = 0; y < h; ++y)
			{
				int pixelOffset = (y * w + x) * bytesPerPixel;
				byte red = data[pixelOffset + 0];
				byte green = data[pixelOffset + 1];
				byte blue = data[pixelOffset + 2];
				byte alpha = 0;
				if (hasAlpha)
				{
					alpha = data[pixelOffset + 3];
				}
				int argb = ((alpha << 24) & 0xFF000000) | ((red << 16) & 0xFF0000) | ((green << 8) & 0xFF00) | (blue & 0xFF);
				bi.setRGB(x, y, argb);
			}
		}
		return bi;
	}

	private static BufferedImage getTile16(BufferedImage _terrainImage, int x, int y)
	{
		return _terrainImage.getSubimage(x * 16, y * 16, 16, 16);
	}

	private static BufferedImage applyColorFilter(BufferedImage img, float r, float g, float b)
	{
		BufferedImage bi = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		for (int x = 0; x < img.getWidth(); ++x)
		{
			for (int y = 0; y < img.getHeight(); ++y)
			{
				int rgb = img.getRGB(x, y);

				int alpha = (byte) ((rgb >> 24) & 0xFF);
				float red = ((rgb >> 16) & 0xFF);
				float green = ((rgb >> 8) & 0xFF);
				float blue = (rgb & 0xFF);

				red *= r;
				green *= g;
				blue *= b;

				int rr = MathHelper.floor(red);
				int gg = MathHelper.floor(green);
				int bb = MathHelper.floor(blue);

				rr = MathHelper.clamp(rr, 0, 255);
				gg = MathHelper.clamp(gg, 0, 255);
				bb = MathHelper.clamp(bb, 0, 255);

				rgb = (alpha << 24) | (rr << 16) | (gg << 8) | bb;
				bi.setRGB(x, y, rgb);
			}
		}
		return bi;
	}
}
