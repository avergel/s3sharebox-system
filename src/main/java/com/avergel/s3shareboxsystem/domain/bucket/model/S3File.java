package com.avergel.s3shareboxsystem.domain.bucket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3File {
    private String path;
    private String name;
}
