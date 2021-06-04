package org.sickert.images;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Help.Visibility;

@Command(
    name = "dedupimgs",
    description = "Removes duplicates from a collection of images",
    exitCodeListHeading = "Exit Codes:%n",
    exitCodeList = { 
        " 0:Successful program execution.",
        "64:Invalid input: an unknown option or invalid parameter was specified."})
public class Deduplicator implements Callable<Integer> {
    
    @Parameters(
        index = "0", 
        description = "A folder with images to de-duplicate. Operates on the current working directory if omitted.", 
        defaultValue = ".")
    private File imageFolder;

    @Option(
        names = {"-t", "--threshold"},
        description = "The minimum similarity at which two images are considered duplicates.",
        defaultValue = "0.9",
        showDefaultValue = Visibility.ALWAYS)
    private double threshold;
    
    @Override
    public Integer call() throws Exception {
        if (!imageFolder.isDirectory()) {
            System.err.println("The given folder is not a directory!");
            return 64;
        }
        System.out.println("Deduplicating images in " + imageFolder.getPath());

        File[] imageFiles = imageFolder.listFiles(file -> file.isFile() && file.canRead());

        for (File imageFile: imageFiles) {
            for (File otherImageFile: imageFiles) {
                if (imageFile.equals(otherImageFile)) {
                    continue;
                }
                Image thisImage = Image.fromFile(imageFile);
                Image otherImage = Image.fromFile(otherImageFile);
                double similarity = thisImage.similarity(otherImage);
                if (similarity > this.threshold) {
                    System.out.println("Images " + imageFile + " and " + otherImageFile + " seem to be equal.");
                }
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Deduplicator()).execute(args));
    }
}
