package alex.controllers;

import alex.service.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService service;

    @PostMapping("/upload")
    public ResponseEntity<URL> uploadFile(@RequestBody MultipartFile file) {
        return new ResponseEntity<>(service.uploadFileFromHttp(file), HttpStatus.OK);
    }

    @PostMapping("/uploadD")
    public ResponseEntity<URL> uploadFile(@RequestParam(value = "name") String name) {
        return new ResponseEntity<>(service.uploadFileFromDir(name), HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
        //http://localhost:8080/file/download/ooo.jpg
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }
}
