package ImageService;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/")
    public String showUploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadAndResizeImage(@RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "cropWidth", defaultValue = "0") int cropWidth,
                                       @RequestParam(value = "cropHeight", defaultValue = "0") int cropHeight,
                                       @RequestParam(value = "cropX", defaultValue = "0") int cropX,
                                       @RequestParam(value = "cropY", defaultValue = "0") int cropY,
                                       @RequestParam(value = "resizeWidth", defaultValue = "0") int resizeWidth,
                                       @RequestParam(value = "resizeHeight", defaultValue = "0") int resizeHeight) {
        if (!file.isEmpty()) {
            try {
                BufferedImage originalImage = ImageIO.read(file.getInputStream());
                int originalWidth = originalImage.getWidth();
                int originalHeight = originalImage.getHeight();

                // 사용자가 입력한 값이 0인 경우, 원본 이미지 크기로 설정
                if (cropWidth == 0) cropWidth = originalWidth;
                if (cropHeight == 0) cropHeight = originalHeight;
                if (resizeWidth == 0) resizeWidth = originalWidth;
                if (resizeHeight == 0) resizeHeight = originalHeight;

                // 이미지 리사이징
                String outputDir = "C:/"; // 변경 가능한 디렉토리 경로
                File resizedFile = new File(outputDir + "resized_" + file.getOriginalFilename());
                Thumbnails.of(file.getInputStream()) // 업로드된 파일의 InputStream을 사용하여 리사이징
                        .sourceRegion(cropX, cropY, cropWidth, cropHeight) // 크롭 영역 설정
                        .size(resizeWidth, resizeHeight)
                        .toFile(resizedFile);

                return "redirect:/"; // 업로드 성공 시 홈페이지로 리다이렉트
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/"; // 업로드 실패 시 홈페이지로 리다이렉트
    }

    @GetMapping("/resize")
    public ResponseEntity<?> resizeImage(@RequestParam("url") String imageUrl, @RequestParam("width") int width, @RequestParam("height") int height) throws IOException {

        imageService.resizeImage(imageUrl, width, height);
        Tika tika = new Tika();
        String extension = "";
        byte[] imageData;

        // 처음으로 이미지 URL에서 스트림을 가져와 MIME 타입 감지
        try (InputStream imageStreamForDetecting = new URL(imageUrl).openStream()) {
            String mimeType = tika.detect(imageStreamForDetecting);
            extension = mimeType.split("/")[1];
        } catch (IOException e) {
            e.printStackTrace();
            // 여기서 예외 처리
        }

        // 두 번째로 이미지 URL에서 스트림을 가져와 실제 데이터 저장
        try (InputStream imageStreamForSaving = new URL(imageUrl).openStream()) {
            File dir = new File("C:/test/resize/origin/");
            if (!dir.exists()) {
                dir.mkdirs();  // 디렉토리가 없으면 생성
            }

            File tempFile = new File(dir, "555." + extension);
            Files.copy(imageStreamForSaving, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 세 번째로 리사이징
            List<String> commands = new ArrayList<>();
            commands.add("magick");
            commands.add("convert");
            commands.add("-resize");
            commands.add(width + "x" + height);

            if (tempFile != null && tempFile.exists()) {
                commands.add(tempFile.getAbsolutePath());

                String outputFilePath = "C:/test/resize/origin/666." + extension;
                commands.add(outputFilePath);

                ProcessBuilder pb = new ProcessBuilder(commands);

                try {
                    Process process = pb.start();
                    process.waitFor();

                    imageData = Files.readAllBytes(Paths.get(outputFilePath));

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType("image/" + extension))
                            .body(imageData);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}