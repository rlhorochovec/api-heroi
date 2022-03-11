package br.rafaelhorochovec.heroi.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import net.coobird.thumbnailator.Thumbnails;

public class ImageResize {

	public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
		Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
		BufferedImage bufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(resultingImage, 0, 0, null);
		return bufferedImage;
	}

	public static BufferedImage resizeImageThumb(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
            .size(targetWidth, targetHeight)
            .outputFormat("JPEG")
            .outputQuality(0.90)
            .toOutputStream(outputStream);
        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return ImageIO.read(inputStream);
    }

	public static BufferedImage simpleResizeImage(BufferedImage originalImage, int targetWidth) {
        return Scalr.resize(originalImage, targetWidth);
    }
	
	public static BufferedImage resizeImageScalr(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

	/*
	public static void main(String[] args) throws IOException {
		BufferedImage originalImage = ImageIO.read(new File("uploads/avatar_20210222_174632047.jpg"));
		BufferedImage outputImage = resizeImage(originalImage, 150, 150);
		ImageIO.write(outputImage, "jpg", new File("uploads/avatar_20210222_174632047-resized-scaledinstance.jpg"));
	}
	
    public static void main(String[] args) throws Exception {
    	BufferedImage originalImage = ImageIO.read(new File("uploads/avatar_20210222_174632047.jpg"));
        BufferedImage outputImage = resizeImageScalr(originalImage, 200, 200);
        ImageIO.write(outputImage, "jpg", new File("uploads/avatar_20210222_174632047-resized-imgscalr.jpg"));
    }
	
    public static void main(String[] args) throws Exception {
    	BufferedImage originalImage = ImageIO.read(new File("uploads/avatar_20210222_174632047.jpg"));
        BufferedImage outputImage = resizeImageThumb(originalImage, 200, 200);
        ImageIO.write(outputImage, "jpg", new File("uploads/avatar_20210222_174632047-resized-thumbnailator.jpg"));
    }
    */
}
