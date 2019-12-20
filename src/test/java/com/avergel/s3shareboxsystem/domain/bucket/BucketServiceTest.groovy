package com.avergel.s3shareboxsystem.domain.bucket

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import com.avergel.s3shareboxsystem.domain.bucket.model.S3BlobFile
import com.avergel.s3shareboxsystem.domain.bucket.model.S3File
import com.avergel.s3shareboxsystem.domain.bucket.model.S3Folder
import com.avergel.s3shareboxsystem.infrastructure.config.S3Config
import org.apache.commons.io.IOUtils
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.Charset

class BucketServiceTest extends Specification {

    def bucket = "bucket"
    def s3Config = new S3Config("eu-west-1", bucket)
    def s3Client = Mock AmazonS3
    def userId = "userId"
    @Subject
    def service = new BucketService(s3Config, s3Client)

    def "setup"() {
        def authentication = Mock(Authentication)
        authentication.getName() >> userId
        def securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)
    }

    def "ListFiles"() {
        given:
        def key = "/path/"
        def folderSummary = new S3ObjectSummary()
        folderSummary.setKey(userId + "/path/")
        def subFolderSummary = new S3ObjectSummary()
        subFolderSummary.setKey(userId + "/path/path1/")
        def subSubFolderSummary = new S3ObjectSummary()
        subSubFolderSummary.setKey(userId + "/path/path1/path2/")
        def objectSummary = new S3ObjectSummary()
        objectSummary.setKey(userId + "/path/object")
        def objectSummaries = [objectSummary, folderSummary, subFolderSummary, subSubFolderSummary]
        def result = new ListObjectsV2Result()
        result.getObjectSummaries().addAll(objectSummaries)

        def subFolder = S3Folder.builder()
                .name("path1")
                .path("/path/path1/")
                .build()
        def folder = S3Folder.builder()
                .name("path")
                .path("/path/")
                .files([new S3File("/path/object", "object")])
                .subFolders([subFolder])
                .build()

        def expected = new S3Folder().builder()
                .name(folder.getName())
                .path(folder.getPath())
                .files(folder.getFiles())
                .subFolders(folder.getSubFolders())
                .build()

        when:
        def actual = service.listFiles(key)

        then:
        1 * s3Client.listObjectsV2(_) >> result

        and:
        actual == expected
    }

    def "GetFile"() {
        given:
        def key = "/path/file"
        def req = new GetObjectRequest(bucket, userId + key)
        def s3Object = new S3Object()
        def metadata = new ObjectMetadata()
        metadata.setContentLength(100L)
        metadata.setContentType(MediaType.TEXT_PLAIN_VALUE)
        s3Object.setObjectMetadata(metadata)
        s3Object.setObjectContent(IOUtils.toInputStream("content", Charset.defaultCharset()))

        def expected = new S3BlobFile()
        expected.setContentType(MediaType.TEXT_PLAIN_VALUE)
        expected.setContentLength(100L)
        expected.setBytes(IOUtils.toByteArray(IOUtils.toInputStream("content", Charset.defaultCharset())))
        expected.setName("file")

        when:
        def actual = service.getFile(key)

        then:
        1 * s3Client.getObject(req) >> s3Object

        and:
        actual == expected
    }
}
