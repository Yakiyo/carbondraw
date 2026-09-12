import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ConvertImage {
    public static void main(String[] args) throws Exception {
        File inputFile = new File("src/main/resources/cs/images/joker/joker_2.jpg");
        BufferedImage img = ImageIO.read(inputFile);
        if (img == null) {
            System.out.println("Could not read image!");
            return;
        }
        
        // create an RGB image
        BufferedImage rgbImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        rgbImage.getGraphics().drawImage(img, 0, 0, null);
        
        File outputFile = new File("src/main/resources/cs/images/joker/joker_2_fixed.jpg");
        ImageIO.write(rgbImage, "jpg", outputFile);
        
        System.out.println("Image converted!");
    }
}
