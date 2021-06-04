package org.sickert.images;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Image {

    private BufferedImage data;

    public static Image fromFile(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        return new Image(image);
    }

    public double similarity(Image otherImage) {
        Image otherImageOfSameSize = makeSameSize(otherImage);
        long difference = 0;
        for (int y = this.data.getMinY(); y < this.data.getMinY() + this.data.getHeight(); y++) {
            for (int x = this.data.getMinX(); x < this.data.getMinX() + this.data.getWidth(); x++) {
                difference += pixelDifference(this.data.getRGB(x, y), otherImageOfSameSize.data.getRGB(x, y));
            }
        }

        double maxDifference = (255 + 255 + 255) * this.data.getWidth() * this.data.getHeight();
        return 1.0d - ((double)difference / maxDifference);
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
     * Returns the color difference of the given pixels.
     * The result is in the range 0 (= identical) to 255+255+255 (= completely different).
     */
    private int pixelDifference(int pixel1, int pixel2) {
        return 
            Math.abs(redPortion(pixel1) - redPortion(pixel2)) +
            Math.abs(greenPortion(pixel1) - greenPortion(pixel2)) +
            Math.abs(bluePortion(pixel1) - bluePortion(pixel2));
    }

    private int redPortion(int pixel) {
        return pixel >> 16 & 0xff;
    }

    private int greenPortion(int pixel) {
        return pixel >> 8 & 0xff;
    }

    private int bluePortion(int pixel) {
        return pixel & 0xff;
    }
}
