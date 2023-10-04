package ImageService;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Controller
public class ImageController {

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

//    @GetMapping("/resize")
//    void imageMagick() throws IOException {
//        List<String> commands = new ArrayList<>();
//        commands.add("convert");
//        commands.add("-resize");
//        commands.add("100x100");
//        commands.add("C:/test/resize/origin/1.png");
//        commands.add("C:/test/resize/origin/100.png");
//
//        ProcessBuilder pb = new ProcessBuilder(commands);
//
//        try {
//            Process process = pb.start();
//            process.waitFor();
//            System.out.println("Image converted successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}