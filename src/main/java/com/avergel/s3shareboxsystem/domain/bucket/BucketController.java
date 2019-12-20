package com.avergel.s3shareboxsystem.domain.bucket;

import com.avergel.s3shareboxsystem.domain.bucket.dto.ListFilesResponse;
import com.avergel.s3shareboxsystem.domain.bucket.model.S3BlobFile;
import com.avergel.s3shareboxsystem.domain.bucket.model.S3File;
import com.avergel.s3shareboxsystem.domain.bucket.model.S3Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;

@RestController
@RequestMapping("files")
@RequiredArgsConstructor(access = PACKAGE)
@CrossOrigin(value = {"*"}, exposedHeaders = {"Content-Disposition"})
class BucketController {
    private BucketService bucketService;

    @Autowired
    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GetMapping("list")
    public ResponseEntity<ListFilesResponse> listFiles(@RequestParam String prefix) {
        prefix = StringUtils.startsWithIgnoreCase(prefix, "/") ? prefix : "/" + prefix;
        prefix = StringUtils.endsWithIgnoreCase(prefix, "/") ? prefix : prefix + "/";

        S3Folder s3Folder = bucketService.listFiles(prefix);
        ListFilesResponse response = ListFilesResponse.builder()
                                                      .currentFolder(new S3File(s3Folder.getPath(), s3Folder.getName()))
                                                      .files(s3Folder.getFiles())
                                                      .folders(s3Folder.getSubFolders()
                                                                       .stream()
                                                                       .map(folder -> new S3File((folder.getPath()), folder.getName()))
                                                                       .collect(toList()))
                                                      .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("upload")
    public ResponseEntity uploadFile(@RequestParam MultipartFile file, @RequestParam String path) {
        bucketService.uploadFile(file, path);
        return ResponseEntity.ok().build();

    }
    @GetMapping
    public ResponseEntity<byte[]> getFile(@RequestParam String key) {
        S3BlobFile file = bucketService.getFile(key);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.valueOf(file.getContentType()));
        header.setContentLength(file.getContentLength());
        header.set("Content-Disposition", "attachment; filename=" + file.getName());

        return new ResponseEntity<>(file.getBytes(), header, HttpStatus.OK);
    }
}
