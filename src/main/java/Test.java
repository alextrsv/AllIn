import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class Test {
    public static void main(String[] args) {
        getImg();
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
}
