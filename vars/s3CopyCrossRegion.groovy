def call(sourceBucket, sourceRegion, sourceFile, targetBucket, targetRegion, targetPath, accessKey, secretKey) {
    new S3CopyCrossRegionPerformer(script:this).run(sourceBucket, sourceRegion, sourceFile, targetBucket, targetRegion, targetPath, accessKey, secretKey)
}