package ImageService;

import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
    void contextLoads2() throws IOException {
        Tika tika = new Tika();
        String extension = "";

        // 처음으로 이미지 URL에서 스트림을 가져와 MIME 타입 감지
        try (InputStream imageStreamForDetecting = new URL("https://w7.pngwing.com/pngs/724/759/png-transparent-apple-logo-apple-computer-icons-apple-logo-heart-computer-logo-thumbnail.png").openStream()) {
            String mimeType = tika.detect(imageStreamForDetecting);
            extension = mimeType.split("/")[1];
        } catch (IOException e) {
            e.printStackTrace();
            // 여기서 예외 처리
        }

        // 두 번째로 이미지 URL에서 스트림을 가져와 실제 데이터 저장
        try (InputStream imageStreamForSaving = new URL("https://w7.pngwing.com/pngs/724/759/png-transparent-apple-logo-apple-computer-icons-apple-logo-heart-computer-logo-thumbnail.png").openStream()) {
            File dir = new File("C:/test/resize/origin/");
            if (!dir.exists()) {
                dir.mkdirs();  // 디렉토리가 없으면 생성
            }

            File tempFile = new File(dir, "555." + extension);

            Files.copy(imageStreamForSaving, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            // 여기서 예외 처리
        }

        // 세 번째로 리사이징
        List<String> commands = new ArrayList<>();
        commands.add("magick");
        commands.add("convert");
        commands.add("-resize");
        commands.add("100x100");
        commands.add("C:/test/resize/origin/555.png");
        commands.add("C:/test/resize/origin/666.png");
        System.out.println("됨");

        ProcessBuilder pb = new ProcessBuilder(commands);

        try {
            Process process = pb.start();
            process.waitFor();
            System.out.println("Image converted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void imageMagick() throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add("magick");
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