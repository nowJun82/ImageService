package ImageService;

import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ImageService {
    int increaseNum;

    ImageService() { this.increaseNum = 1; }

    public int getIncreaseNum() { return this.increaseNum++; }

    public ResponseEntity<?> resizeImage (String imageURL, int width, int height) throws IOException {
        Tika tika = new Tika();
        String extension = "";
        byte[] imageData;

        // 이미지 URL 에서 스트림을 가져와서 MIME 타입 확인
        InputStream imageStreamForDetecting = new URL(imageURL).openStream();
        String mimeType = tika.detect(imageStreamForDetecting);
        extension = mimeType.split("/")[1];

        // 이미지 URL 에서 스트림을 가져와서 지정한 절대 경로에 실제 데이터 저장
        File dir = new File("C:/test/resize/origin/2023-10-06/");
        File tempFile = new File(dir, this.increaseNum + "." + extension);
        Files.copy(imageStreamForDetecting, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        getIncreaseNum();

        // 리사이즈
        List<String> commands = new ArrayList<>();
        commands.add("magick");
        commands.add("convert");
        commands.add("-resize");
        commands.add(width + "x" + height);
        commands.add("C:/test/resize/origin/2023-10-06/" + tempFile);
        commands.add("C:/test/resize/origin/2023-10-06/" + tempFile + "_resize");

        ProcessBuilder pb = new ProcessBuilder();
        try {
            Process process = pb.start();
            process.waitFor();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/" + extension))
                    .body(imageData);
        }
    }
}