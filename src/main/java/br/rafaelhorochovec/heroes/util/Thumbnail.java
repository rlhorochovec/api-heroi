package br.rafaelhorochovec.heroes.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Helper for generating image thumbnails
 * 
 * @author Robson J. P.
 * 
 */
public class Thumbnail {

	/*
	public static void main(String[] args) throws IOException {
		FileInputStream fint = new FileInputStream("uploads/IMG_20190913_181716060.jpg");
		FileOutputStream fout = new FileOutputStream("uploads/IMG_20190913_181716060_resize.jpg");
		generateExactSize(fint, fout, 150, 150, 90, false);
	}
	*/

	/**
	 * Transforms the image size proportionally.
	 * 
	 * @param in          image
	 * @param out         image generated
	 * @param thumbWidth  new width
	 * @param thumbHeight new height
	 * @param quality     new quality
	 * @param bw          true for black and white
	 * @throws IOException
	 * 
	 */
	public static void generatePropotional(InputStream in, OutputStream out, int thumbWidth, int thumbHeight,
			int quality, boolean bw) throws IOException {
		generateThumb(ImageIO.read(in), out, thumbWidth, thumbHeight, quality, bw);
	}

	/**
	 * Transforms the image exact size
	 * 
	 * @param in
	 * @param out
	 * @param thumbWidth
	 * @param thumbHeight
	 * @param quality
	 * @param bw
	 * @throws IOException
	 * 
	 */
	public static void generateExactSize(InputStream in, OutputStream out, int thumbWidth, int thumbHeight, int quality,
			boolean bw) throws IOException {
		generateThumbRect(ImageIO.read(in), out, thumbWidth, thumbHeight, quality, bw);
	}

	private static void generateThumb(Image image, OutputStream out, int thumbWidth, int thumbHeight, int quality,
			boolean bw) throws IOException {
		Graphics2DImage g2dImage = generateThumbG2D(image, thumbWidth, thumbHeight, bw);
		saveToOutput(g2dImage.getImage(), out, quality);
	}

	private static Graphics2DImage generateThumbG2D(Image image, int thumbWidth, int thumbHeight, boolean bw)
			throws IOException {
		thumbWidth = Math.min(thumbWidth, 1280);
		thumbHeight = Math.min(thumbHeight, 1024);
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);
		int x = 0;
		int y = 0;

		if (thumbHeight == -1) {
			// Generates the square image
			thumbHeight = thumbWidth;
			if (imageWidth > imageHeight) {
				x = (imageWidth - imageHeight) / 2;
			} else if (imageHeight > imageWidth) {
				y = (imageHeight - imageWidth) / 2;
			}
		} else {
			double thumbRatio = (double) thumbWidth / (double) thumbHeight;
			double imageRatio = (double) imageWidth / (double) imageHeight;

			if (thumbRatio < imageRatio) {
				thumbHeight = (int) (thumbWidth / imageRatio);
			} else {
				thumbWidth = (int) (thumbHeight * imageRatio);
			}
		}

		// Image type original
		int type = BufferedImage.TYPE_INT_RGB;
		if (image instanceof BufferedImage) {
			if (((BufferedImage) image).getTransparency() == Transparency.TRANSLUCENT) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
		}

		// Draw original image to thumbnail image object and scale it to the new size
		// on-the-fly.
		if (bw) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		}

		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, type);
		Graphics2D graphics2D = thumbImage.createGraphics();

		if (x > 0 || y > 0) {
			// graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, x, y, imageWidth -
			// x, imageHeight - y, null);

			// Generates the square image
			BufferedImage tmpImage = new BufferedImage(imageWidth - x, imageHeight - y, type);
			tmpImage.createGraphics().drawImage(image, 0, 0, imageWidth - x, imageHeight - y, x, y, imageWidth - x,
					imageHeight - y, null);
			graphics2D.drawImage(tmpImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH), 0, 0, null);
		} else {
			graphics2D.drawImage(image.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH), x, y, null);
			// graphics2D.drawImage(image, x, y, thumbWidth, thumbHeight, null);
		}
		return new Graphics2DImage(thumbImage, graphics2D);
	}

	private static Graphics2DImage generateThumbRectG2D(Image image, int thumbWidth, int thumbHeight, boolean bw)
			throws IOException {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		double imageRatio = (double) imageWidth / (double) imageHeight;
		double thumbRatio = (double) thumbWidth / (double) thumbHeight;

		int thumbPropWidth = (int) (thumbHeight * imageRatio);
		int thumbPropHeight = (int) (thumbWidth / imageRatio);

		int sizeWidth = imageWidth;
		int sizeHeight = imageHeight;

		int x = 0;
		int y = 0;

		// if (thumbWidth > thumbHeight) {
		if (thumbRatio > imageRatio) {
			sizeHeight = (int) ((double) imageHeight * ((double) thumbHeight / (double) thumbPropHeight));
			y = (imageHeight - sizeHeight) / 2;
			// } else if (thumbHeight > thumbWidth) {
		} else if (thumbRatio < imageRatio) {
			sizeWidth = (int) ((double) imageWidth * ((double) thumbWidth / (double) thumbPropWidth));
			x = (imageWidth - sizeWidth) / 2;
		}

		// Image type original
		int type = BufferedImage.TYPE_INT_RGB;
		if (image instanceof BufferedImage) {
			if (((BufferedImage) image).getTransparency() == Transparency.TRANSLUCENT) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
		}

		// Draw original image to thumbnail image object and scale it to the new size
		// on-the-fly
		BufferedImage thumbImage = null;

		if (bw)
			type = BufferedImage.TYPE_BYTE_GRAY;
		thumbImage = new BufferedImage(sizeWidth, sizeHeight, type);

		Graphics2D graphics2D = thumbImage.createGraphics();
		// graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, x, y, x +
		// tamWidth, y + tamHeight, null);
		graphics2D.drawImage(image, 0, 0, sizeWidth, sizeHeight, x, y, x + sizeWidth, y + sizeHeight, null);

		Image ii = thumbImage.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH);
		BufferedImage bu = new BufferedImage(thumbWidth, thumbHeight, type);
		bu.createGraphics().drawImage(ii, 0, 0, null);
		return new Graphics2DImage(bu, graphics2D);
	}

	private static void generateThumbRect(Image image, OutputStream out, int thumbWidth, int thumbHeight, int quality,
			boolean bw) throws IOException {
		Graphics2DImage g2dImage = generateThumbRectG2D(image, thumbWidth, thumbHeight, bw);
		saveToOutput(g2dImage.getImage(), out, quality);
	}

	private static void saveToOutput(BufferedImage image, OutputStream out, int quality) throws IOException {

		// Transparency.TRANSLUCENT
		if (image.getTransparency() == Transparency.TRANSLUCENT) {
			// PNG
			ImageIO.write(image, "PNG", out);
		} else {
			// JPG
			ImageIO.write(image, "JPG", out);
		}
	}

	@SuppressWarnings("unused")
	private static class Graphics2DImage {
		private BufferedImage image;
		private Graphics2D graphics2D;

		public Graphics2DImage(BufferedImage image, Graphics2D graphics2D) {
			super();
			this.image = image;
			this.graphics2D = graphics2D;
		}

		public Graphics2DImage(Image image) {
			BufferedImage buffDefault = new BufferedImage(image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2D = buffDefault.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics2D.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
			this.image = buffDefault;
			this.graphics2D = graphics2D;
		}

		public BufferedImage getImage() {
			return image;
		}

		public Graphics2D getGraphics2D() {
			return graphics2D;
		}
	}
}