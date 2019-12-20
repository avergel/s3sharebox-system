package com.avergel.s3shareboxsystem.domain.bucket.dto;

import com.avergel.s3shareboxsystem.domain.bucket.model.S3File;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListFilesResponse {
    private S3File currentFolder;
    private List<S3File> folders;
    private List<S3File> files;
}
