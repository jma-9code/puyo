package tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.net.URL;

import javax.swing.ImageIcon;

public class GImage {

	/**
	 * Permet de remplir le tableau sprites
	 * 
	 * @param img
	 * @return
	 */
	public static Image[][] loadSprite(String path) {
		Image tab[][] = new Image[16][7];
		Image img = loadImage(path);
		if ( img == null ) { return null; }
		BufferedImage buffImg = toBufferedImage(img);
		for ( int i = 0; i < tab.length; i++ ) {
			for ( int j = 0; j < tab[0].length; j++ ) {
				tab[i][j] = buffImg.getSubimage(i * 26, 26 * j, 26, 26);// .getScaledInstance(Config._TAILLE_ICON+1,
																		// Config._TAILLE_ICON+1,
																		// -1);
			}
		}
		return tab;
	}

	/**
	 * Fonction trouv�e sur internet qui permet de transform� un Image en
	 * Buffered img !
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if ( image instanceof BufferedImage ) { return (BufferedImage) image; }

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels
		final boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if ( hasAlpha == true ) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			final GraphicsDevice gs = ge.getDefaultScreenDevice();
			final GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (final HeadlessException e) {
		} // No screen

		if ( bimage == null ) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if ( hasAlpha == true ) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		final Graphics g = bimage.createGraphics();
		final Graphics2D g2d = (Graphics2D) g;
		/** Lissage du texte et des dessins */
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * Permet de savoir si l'image est compos�e de la transparence
	 * 
	 * @param image
	 * @return
	 */
	public static boolean hasAlpha(final Image image) {
		// If buffered image, the color model is readily available
		if ( image instanceof BufferedImage ) { return ((BufferedImage) image).getColorModel().hasAlpha(); }

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		final PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (final InterruptedException e) {
		}

		// Get the image's color model
		return pg.getColorModel().hasAlpha();
	}

	/**
	 * Permet de charger les images.
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static Image loadImage(final String path) {
		URL imageURL = GImage.class.getResource(path);
		if ( imageURL != null ) {
			ImageIcon icon = new ImageIcon(imageURL);
			return icon.getImage();
		}
		return null;
	}

}
