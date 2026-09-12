import java.io.FileInputStream;

public class CheckHeader {
    public static void main(String[] args) throws Exception {
        try (FileInputStream fis = new FileInputStream("src/main/resources/cs/images/joker/joker_2.jpg")) {
            byte[] bytes = new byte[10];
            fis.read(bytes);
            for (byte b : bytes) {
                System.out.printf("%02X ", b);
            }
            System.out.println();
        }
    }
}
