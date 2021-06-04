package org.sickert.images;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "dedupimgs", description = "Removes duplicates from a collection of images")
public class Deduplicator implements Callable<Integer> {
    
    @Override
    public Integer call() throws Exception {
        Image image1 = Image.fromPath("/Users/christian/Downloads/alte-zeitz-bilder/IMG-20210515-WA0000.jpg");
        Image image2 = Image.fromPath("/Users/christian/Downloads/alte-zeitz-bilder/IMG-20210515-WA0013.jpg");
        System.out.println("Similarity of image1 and image1: " + image1.similarity(image1));
        System.out.println("Similarity of image1 and image2: " + image1.similarity(image2));
        System.out.println("Similarity of image2 and image1: " + image2.similarity(image1));
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Deduplicator()).execute(args));
    }
}
