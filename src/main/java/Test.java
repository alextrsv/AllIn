import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Test {
    public static void main(String[] args) {

//        getImg();
//        copyTest();
    }

    public static byte[] getImg() {
       try{
        InputStream in = Test.class.getClassLoader().getResourceAsStream("img1.jpg");

            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }

//    public static void copyTest() {
//        File inputFile = new File("E:\\Pshoo\\PshooCode\\server\\src\\main\\resources\\imgs\\first.jpg");
//        File outputFile = new File("NEW.jpg");
//        try (InputStream is = new FileInputStream(inputFile)) {
//            BufferedImage image = ImageIO.read(is);
//            try (OutputStream os = new FileOutputStream(outputFile)) {
//                ImageIO.write(image, "jpg", os);
//            } catch (Exception exp) {
//                exp.printStackTrace();
//            }
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
//    }
}
