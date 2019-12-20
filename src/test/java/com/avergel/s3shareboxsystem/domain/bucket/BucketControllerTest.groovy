package com.avergel.s3shareboxsystem.domain.bucket

import com.avergel.s3shareboxsystem.domain.bucket.dto.ListFilesResponse
import com.avergel.s3shareboxsystem.domain.bucket.model.S3BlobFile
import com.avergel.s3shareboxsystem.domain.bucket.model.S3File
import com.avergel.s3shareboxsystem.domain.bucket.model.S3Folder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

class BucketControllerTest extends Specification {

    def bucketService = Mock BucketService

    @Subject
    def controller = new BucketController(bucketService)

    def "ListFiles"() {
        given:
        def prefix = "/path/"
        def s3Folder = S3Folder.builder()
                .name("path")
                .path("/path/")
                .files([])
                .subFolders([])
                .build()
        def expected = ResponseEntity.ok(ListFilesResponse.builder()
                .currentFolder(new S3File(s3Folder.getPath(), s3Folder.getName()))
                .files([])
                .folders([])
                .build())

        when:
        def actual = controller.listFiles(prefix)

        then:
        1 * bucketService.listFiles(prefix) >> s3Folder

        and:
        expected == actual
    }

    def "GetFile"() {
        given:
        def key = "/name"
        def file = new S3BlobFile()
        file.setName("name")
        file.setBytes("content".getBytes())
        file.setContentLength("content".length())
        file.setContentType(MediaType.TEXT_PLAIN_VALUE)

        def header = new HttpHeaders();
        header.setContentType(MediaType.valueOf(file.getContentType()))
        header.setContentLength(file.getContentLength())
        header.set("Content-Disposition", "attachment; filename=" + file.getName())

        def expected = new ResponseEntity<>(file.getBytes(), header, HttpStatus.OK);

        when:
        def actual = controller.getFile(key)

        then:
        1 * bucketService.getFile(key) >> file

        and:
        expected == actual
    }
}
