package ImageService;

import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ImgServiceApplicationTests {

    @Test
    void contextLoads() throws IOException {
        Tika tika = new Tika();
        File directory = new File("C:/test/resize/origin/");

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    // 각 파일에 대한 MIME 타입 체크
                    String mimeType = tika.detect(file);
                    System.out.println(file.getName() + " 의 MIME 타입 : " + mimeType);
                }
            }
        }
    }

    @Test
    void imageMagick() throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add("convert");
        commands.add("-resize");
        commands.add("100x100");
        commands.add("C:/test/resize/origin/1.png");
        commands.add("C:/test/resize/origin/100.png");

        ProcessBuilder pb = new ProcessBuilder(commands);

        try {
            Process process = pb.start();
            process.waitFor();
            System.out.println("Image converted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}