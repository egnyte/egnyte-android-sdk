package com.egnyte.androidsdk.entities;

import java.io.File;

/**
 * This class represents {@link com.egnyte.androidsdk.requests.ChunkedUploadRequest} result
 */
public class ChunkedUploadResult {

    /**
     * Cloud path to upload to
     */
    public final String cloudPath;

    /**
     * File to upload
     */
    public final File sourceFile;

    /**
     * Chunk Size
     */
    public final long chunkSize;

    /**
     * Upload id. Null for result of uploading last chunk.
     */
    public final String uploadId;

    /**
     * Chunk number, 1-indexed. Null for result of uploading last chunk.
     */
    public final Integer chunkNum;

    /**
     * SHA512 hash of entire file. Might be null.
     */
    public final String checksum;


    /**
     *
     * @param cloudPath Cloud path to upload to
     * @param sourceFile File to upload
     * @param chunkSize Size
     * @param uploadId Upload id
     * @param chunkNum Chunk number, 1-indexed
     * @param checksum SHA512 hash of entire file. Might be null.
     */
    public ChunkedUploadResult(String cloudPath, File sourceFile, long chunkSize, String uploadId, Integer chunkNum,
                               String checksum) {
        this.cloudPath = cloudPath;
        this.sourceFile = sourceFile;
        this.chunkSize = chunkSize;
        this.uploadId = uploadId;
        this.chunkNum = chunkNum;
        this.checksum = checksum;
    }
}
