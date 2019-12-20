package com.avergel.s3shareboxsystem.domain.bucket;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.avergel.s3shareboxsystem.domain.bucket.model.S3BlobFile;
import com.avergel.s3shareboxsystem.domain.bucket.model.S3File;
import com.avergel.s3shareboxsystem.domain.bucket.model.S3Folder;
import com.avergel.s3shareboxsystem.infrastructure.config.S3Config;
import com.avergel.s3shareboxsystem.infrastructure.exception.model.CustomException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BucketService {
    final S3Config s3Config;
    final AmazonS3 s3Client;

    public BucketService(S3Config s3Config, AmazonS3 s3Client) {
        this.s3Config = s3Config;
        this.s3Client = s3Client;
    }

    @SneakyThrows
    S3Folder listFiles(String prefix) {
        String userId = SecurityContextHolder.getContext()
                                             .getAuthentication()
                                             .getName();

        S3Folder s3Folder = new S3Folder();
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3Config.getBucketName())
                                       .withPrefix(userId + prefix);

        List<S3ObjectSummary> objectSummaries = s3Client.listObjectsV2(req)
                                                        .getObjectSummaries();
        if (CollectionUtils.isEmpty(objectSummaries)) {
            throw new CustomException(HttpStatus.NOT_FOUND.value(), "Invalid path");
        }

        s3Folder.setName(getName(prefix));
        s3Folder.setPath(prefix);

        for (S3ObjectSummary o : objectSummaries) {
            String keyWithoutUserId = o.getKey()
                                       .replace(userId, "");
            String keyWithoutPrefix = o.getKey()
                                       .replace(userId + prefix, "");
            if (!StringUtils.isEmpty(keyWithoutPrefix)) {
                int numberOfSlashesInKeyWihoutPrefix = StringUtils.countOccurrencesOf(keyWithoutPrefix, "/");

                if (numberOfSlashesInKeyWihoutPrefix == 1 && keyWithoutPrefix.endsWith("/")) {
                    s3Folder.getSubFolders()
                            .add(S3Folder.builder()
                                         .path(keyWithoutUserId)
                                         .name(getName(keyWithoutPrefix))
                                         .build());
                }
                if (numberOfSlashesInKeyWihoutPrefix == 0) {
                    s3Folder.getFiles()
                            .add(new S3File(keyWithoutUserId, keyWithoutPrefix));
                }
            }
        }

        return s3Folder;
    }

    @SneakyThrows
    S3BlobFile getFile(String key) {
        String userId = SecurityContextHolder.getContext()
                                             .getAuthentication()
                                             .getName();
        GetObjectRequest req = new GetObjectRequest(s3Config.getBucketName(), userId + key);
        try {
            S3Object s3Object = s3Client.getObject(req);
            S3BlobFile blobFile = new S3BlobFile();
            blobFile.setBytes(IOUtils.toByteArray(s3Object.getObjectContent()));
            blobFile.setContentLength(s3Object.getObjectMetadata().getContentLength());
            blobFile.setContentType(s3Object.getObjectMetadata().getContentType());
            blobFile.setName(getName(key));
            return blobFile;
        } catch (IOException e) {
            String errorMessage = "Exception retrieving object " + key + " from S3";
            log.error(errorMessage, e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
        }
    }

    @SneakyThrows
    void uploadFile(MultipartFile multipartFile, String path) {
        String userId = SecurityContextHolder.getContext()
                                             .getAuthentication()
                                             .getName();
        File file = null;
        try {
            file = convertMultiPartToFile(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename())
                                 .replace("/", "");
        PutObjectRequest req = new PutObjectRequest(s3Config.getBucketName(), userId + path + fileName, file);
        s3Client.putObject(req);
        FileUtils.deleteQuietly(file);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
    private String getName(String key) {
        if (key.equals("/")) {
            return key;
        }
        String[] split = key.split("/");
        return split[split.length - 1];
    }
}
