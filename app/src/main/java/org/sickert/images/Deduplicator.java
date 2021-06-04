package org.sickert.images;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Help.Visibility;

@Command(
    name = "dedupimgs",
    description = "Removes duplicates from a collection of images",
    mixinStandardHelpOptions = true,
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
        names = { "-t", "--threshold" },
        description = "The minimum similarity at which two images are considered duplicates.",
        defaultValue = "0.92",
        showDefaultValue = Visibility.ALWAYS)
    private double threshold;

    @Override
    public Integer call() throws Exception {
        if (!imageFolder.isDirectory()) {
            System.err.printf("%s is not a directory! Aborting %n", imageFolder);
            return 64;
        }
        System.out.printf("Deduplicating images in %s %n", imageFolder);

        File[] imageFiles = imageFolder.listFiles(file -> file.isFile() && file.canRead());
        // TODO: Filter on image files
        System.out.printf("Found %d images %n", imageFiles.length);

        int numDuplicates = 0;
        boolean[] isDuplicate = new boolean[imageFiles.length];
        Arrays.fill(isDuplicate, false);

        for (int thisIndex = 0; thisIndex < imageFiles.length - 1; thisIndex++) {
            if (isDuplicate[thisIndex]) {
                continue;
            }
            Image thisImage = Image.fromFile(imageFiles[thisIndex]);
            for (int otherIndex = thisIndex + 1; otherIndex < imageFiles.length; otherIndex++) {
                if (isDuplicate[otherIndex]) {
                    continue;
                }
                Image otherImage = Image.fromFile(imageFiles[otherIndex]);
                try {
                    double similarity = thisImage.similarity(otherImage);
                    if (similarity > this.threshold) {
                        System.out.printf("%s seems to be a duplicate of %s %n", 
                            imageFiles[otherIndex].getName(), imageFiles[thisIndex].getName());
                        isDuplicate[otherIndex] = true;
                        imageFiles[otherIndex].renameTo(new File(
                            imageFiles[otherIndex].getParentFile(),
                            "duplicate_of_" + imageFiles[thisIndex].getName()));
                        numDuplicates++;
                    }
                }
                catch (Exception e) {
                    System.err.printf("Unable to determine similarity between %s and %s. An error occurred comparing these images. Skipping them.%n", 
                        imageFiles[thisIndex], imageFiles[otherIndex]);
                }
            }
        }
        System.out.printf("Finished. Found %d duplicates.%n", numDuplicates);

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Deduplicator()).execute(args));
    }
}
