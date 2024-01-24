def call(sourceBucket, sourceRegion, sourceFile, targetBucket, targetRegion, targetPath, accessKey, secretKey) {
    S3CopyCrossRegionPerformer.run(sourceBucket, sourceRegion, sourceFile, targetBucket, targetRegion, targetPath, accessKey, secretKey)
}