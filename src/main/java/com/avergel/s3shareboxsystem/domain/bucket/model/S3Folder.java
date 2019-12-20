package com.avergel.s3shareboxsystem.domain.bucket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3Folder {
    private String user;
    private String path;
    private String name;
    private List<S3Folder> subFolders = new ArrayList<>();
    private List<S3File> files = new ArrayList<>();
}
