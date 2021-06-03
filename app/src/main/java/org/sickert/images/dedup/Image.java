package org.sickert.images.dedup;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Image {

    private BufferedImage data;

    public static Image fromPath(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        return new Image(image);
    }

    public double similarity(Image otherImage) {
        Image otherImageOfSameSize = makeSameSize(otherImage);
        ColorModel thisColorModel = this.data.getColorModel();
        ColorModel otherColorModel = otherImageOfSameSize.data.getColorModel();
        long difference = 0;
        for (int y = this.data.getMinY(); y < this.data.getMinY() + this.data.getHeight(); y++) {
            for (int x = this.data.getMinX(); x < this.data.getMinX() + this.data.getWidth(); x++) {
                difference += pixelDifference(this.data.getRGB(x, y), thisColorModel, 
                    otherImageOfSameSize.data.getRGB(x, y), otherColorModel);
            }
        }

        return 1.0d - ((double)difference / (double)((255 + 255 + 255) * this.data.getWidth() * this.data.getHeight()));
    }

    private Image makeSameSize(Image otherImage) {
        if (this.data.getWidth() != otherImage.data.getWidth() || this.data.getHeight() != otherImage.data.getHeight()) {
            BufferedImage resizedImage = new BufferedImage(this.data.getWidth(), this.data.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(otherImage.data, 0, 0, this.data.getWidth(), this.data.getHeight(), null);
            graphics2D.dispose();
            return new Image(resizedImage);
        } else {
            return otherImage;
        }
    }

    /**
     * Returns the color difference of the given pixels in the given color models.
     * The result is in the range 0 (= identical) to 100 (= completely different).
     */
    private int pixelDifference(int pixel1, ColorModel colorModel1, int pixel2, ColorModel colorModel2) {
        return 
            Math.abs(colorModel1.getRed(pixel1) - colorModel2.getRed(pixel2)) +
            Math.abs(colorModel1.getGreen(pixel1) - colorModel2.getGreen(pixel2)) +
            Math.abs(colorModel1.getBlue(pixel1) - colorModel2.getBlue(pixel2));
    }
}
