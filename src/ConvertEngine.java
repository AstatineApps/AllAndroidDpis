import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class ConvertEngine {

    private static final String[] imageExtensions = {"jpeg", "jpg", "png", "bmp", "wbmp", "gif"};
    public static final Integer[] dpiRatios = {3, 4, 6, 8, 12, 16};
    public static final String[] dpiNames  = {"ldpi", "mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};

    public static int getDpiRatio(String dpiName){
        return dpiRatios[Arrays.asList(dpiNames).indexOf(dpiName)];
    }

    static boolean isImage(File file){
        return Arrays.stream(imageExtensions).anyMatch(imgP -> imgP.equals(ConvertEngine.getFileExtension(file)));
    }

    public static File[] getImages(File srcDir){
        FileFilter imageFilter = ConvertEngine::isImage;
        return srcDir.listFiles(imageFilter);
    }

    static boolean isDirectory(File dir){
        return dir.exists() && dir.isDirectory();
    }

    public static boolean makeDpiDirectories(String outputDir, int maxRatio, String[] dstDpiDir){
        int range = Arrays.asList(dpiRatios).indexOf(maxRatio) + 1;
        boolean[] booleans = new boolean[range];
        for (int i = 0; i < range; i++) {
            booleans[i] = new File(outputDir + "/" + dstDpiDir[i]).mkdirs();
        }
        return Collections.singletonList(booleans).size() == 1;
    }

    public static DpiImages[] scanDirectory(File srcDir){
        if(isDirectory(srcDir)){
            File[] imageFiles = getImages(srcDir);
            DpiImages[] dpiImages = new DpiImages[imageFiles.length];
            for (int i = 0; i < imageFiles.length; i++) {
                dpiImages[i] = new DpiImages(imageFiles[i]);
            }
            return dpiImages;
        }
        else{
            return null;
        }
    }

    public static DpiImages[] scanDirectory(
            File srcDir,
            String maxDpi,
            String outputDir,
            String[] outputDpiDirNames,
            String imageFormat,
            Integer bufferedImageType
    ){
        if(isDirectory(srcDir)){
            File[] imageFiles = getImages(srcDir);
            DpiImages[] dpiImages = new DpiImages[imageFiles.length];
            for (int i = 0; i < imageFiles.length; i++) {
                dpiImages[i] = new DpiImages(imageFiles[i]);

                if(maxDpi != null){
                    dpiImages[i].setMaxDpi(maxDpi);
                }
                if(outputDir != null){
                    dpiImages[i].setOutputDir(outputDir);
                }
                if(outputDpiDirNames != null){
                    dpiImages[i].setOutputDpiDirName(outputDpiDirNames);
                }
                if(imageFormat != null){
                    dpiImages[i].setImageFormat(imageFormat);
                }
                if(bufferedImageType != null){
                    dpiImages[i].setBufferedImageType(bufferedImageType);
                }
            }

            return dpiImages;
        }
        return null;
    }

    public static int[] getImageSize(File file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(file.getAbsolutePath()));
        return new int[] { bufferedImage.getWidth(), bufferedImage.getHeight()};
    }

    public static int[] resizedWidthHeight(int ratio, int max_ratio, int[] width_height){

        int width = (width_height[0] * ratio) / max_ratio;
        int height = (width_height[1] * ratio) / max_ratio;
        return new int[] {width, height};
    }

    public static BufferedImage toBufferedImage(Image image, int bufferedImageType) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                bufferedImageType
        );
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    public static Image scaleImage(Image image, int[] width_height, int scaleHint){
        return image.getScaledInstance(width_height[0], width_height[1], scaleHint);
    }

    static BufferedImage createBufferedImage(File file, int[] desired_WH, int ratio, int max_ratio, int bufferedImageType, int scaleHint) throws IOException {
        int[] width_height = resizedWidthHeight(ratio, max_ratio, desired_WH);
        Image image = scaleImage(ImageIO.read(file), width_height, scaleHint);
        return toBufferedImage(image, bufferedImageType);
    }

    static File createImageFile(File file, String dst){
        return new File(dst+file.getName());
    }

    public static String getFileExtension(File file){
        String fileName = file.getName();
        return fileName.lastIndexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".")+1) : null;
    }

    public static String getFileName(File file){
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

}
