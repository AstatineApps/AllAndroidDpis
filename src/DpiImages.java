import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DpiImages {
    private final List<BufferedImage> bufferedImage = new ArrayList<>();
    private final File srcFile;
    private final List<File>outputFile = new ArrayList<>();
    private String outputDir;
    private final String fileName;
    private String imageFormat;

    private int bufferedImageType = BufferedImage.TYPE_INT_ARGB;
    private int scaleHint = Image.SCALE_AREA_AVERAGING;
    private final String[] dstDpiDir = ConvertEngine.dpiNames;
    private int maxRatio = 16;


    public DpiImages(File srcFile) {
        this.srcFile = srcFile;
        outputDir = srcFile.getParent();
        this.imageFormat = ConvertEngine.getFileExtension(srcFile);
        fileName = ConvertEngine.getFileName(srcFile);

        updateBufferedImageType();
    }

    private void updateBufferedImageType(){
        this.bufferedImageType = this.imageFormat.equals("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
    }

    private int[] getDesiredWidthHeight(int ratio) throws IOException {
        return ConvertEngine.resizedWidthHeight(
                ratio,
                this.maxRatio,
                ConvertEngine.getImageSize(this.srcFile)
        );
    }

    public void setOutputDpiDirName(String[] dstDpiDir){
        System.arraycopy(dstDpiDir, 0, this.dstDpiDir, 0, dstDpiDir.length);
    }

    public void setMaxDpi(String maxDpi){
        maxRatio = ConvertEngine.getDpiRatio(maxDpi);
    }

    public void setImageFormat(String imageFormat){
        this.imageFormat = imageFormat;
        updateBufferedImageType();
    }

    public void setBufferedImageType(int bufferedImageType){
        this.bufferedImageType = bufferedImageType;
    }

    public void setScaleHint(int scaleHint){
        this.scaleHint = scaleHint;
    }

    public void setOutputDir(String outputDir){
        this.outputDir = outputDir;
    }

    public void makeImages() throws IOException {
        int range = Arrays.asList(ConvertEngine.dpiRatios).indexOf(maxRatio) + 1;
        for (int i = 0; i < range; i++) {
            Image image = ConvertEngine.scaleImage(
                    ImageIO.read(srcFile),
                    getDesiredWidthHeight(ConvertEngine.dpiRatios[i]),
                    scaleHint
            );
            bufferedImage.add(ConvertEngine.toBufferedImage(
                    image,
                    bufferedImageType
            ));
            outputFile.add(new File(outputDir + "/" + dstDpiDir[i] + "/" +
                    fileName + "." + imageFormat
            ));
        }

    }

    public void saveImages() throws IOException {
        int range = Arrays.asList(ConvertEngine.dpiRatios).indexOf(maxRatio) + 1;
        ConvertEngine.makeDpiDirectories(outputDir, maxRatio, dstDpiDir);
        for (int i = 0; i < range; i++) {
            System.out.println(
                    ImageIO.write(
                            bufferedImage.get(i),
                            imageFormat,
                            outputFile.get(i)
                    )
            );

            System.out.println(imageFormat);
            System.out.print(outputFile.get(i).getAbsolutePath());
            System.out.println("( " + bufferedImage.get(i).getWidth() + " x " + bufferedImage.get(i).getHeight() + " )");
        }
    }

    public BufferedImage[] getDpiBuffImages(){
        return bufferedImage.toArray(new BufferedImage[0]);
    }
}
