package com.avergel.s3shareboxsystem.domain.bucket.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class S3BlobFile {
    private byte[] bytes;
    private String name;
    private Long contentLength;
    private String contentType;
}
