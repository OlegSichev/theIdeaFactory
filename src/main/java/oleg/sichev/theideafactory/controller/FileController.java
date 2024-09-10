package oleg.sichev.theideafactory.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    // Импорт пути из конфигурационного файла
    private final Path fileStorageLocation;

    public FileController(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/files/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            System.out.println("Attempting to access file: " + filePath.toString());

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                // Определяем тип контента файла
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream"; // default
                }

                // Кодируем имя файла для безопасной передачи
                String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8.toString())
                        .replace("+", "%20");

                // Формируем и возвращаем ответ
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                        .body(resource);
            } else {
                System.out.println("File not found or not readable: " + filePath.toString());
                throw new FileNotFoundException("Файл не найден или не может быть прочитан: " + filename);
            }
        } catch (IOException ex) {
            System.err.println("Ошибка при скачивании файла: " + filename);
            ex.printStackTrace();
            throw new RuntimeException("Ошибка при скачивании файла: " + filename, ex);
        }
    }
}
