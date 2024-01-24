@Grab(group = 'com.amazonaws', module = 'aws-java-sdk-core', version = '1.12.642')
@Grab(group = 'com.amazonaws', module = 'aws-java-sdk-s3', version = '1.12.642')

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.apigateway.model.EndpointConfiguration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.services.s3.transfer.TransferManager;


class S3CopyCrossRegionPerformer {
    Script script
    def run(sourceBucket, sourceRegion, sourceFile, targetBucket, targetRegion, targetPath, accessKey, secretKey) {
        AmazonS3 s3ClientBuilder = null;
        AmazonS3 s3desClientBuilder = null;
        TransferManager transferManager = null;

        ClientConfiguration clientCfg = new ClientConfiguration();
        clientCfg.setProtocol(Protocol.HTTPS);
        clientCfg.setSignerOverride("S3SignerType");
        AWSStaticCredentialsProvider credentialProvidor = new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(accessKey, secretKey));

        s3ClientBuilder = AmazonS3ClientBuilder//
            .standard()//
            .withCredentials(credentialProvidor)//
            .withEndpointConfiguration(new EndpointConfiguration(sourceBucket, sourceRegion))//
            .withClientConfiguration(clientCfg)//
            .build();
        ListObjectsRequest lor = new ListObjectsRequest()
            .withBucketName(sourceBucket)
            .withPrefix(sourceFile);
        ObjectListing objectListing = s3ClientBuilder.listObjects(lor);
        for (S3ObjectSummary summary: objectListing.getObjectSummaries()) {

            SOURCE_KEY=summary.getKey();
            DESTINATION_KEY=SOURCE_KEY

            s3desClientBuilder = AmazonS3ClientBuilder//
                .standard()//
                .withCredentials(credentialProvidor)//
                .withEndpointConfiguration(new EndpointConfiguration(targetBucket, targetRegion))//
                .withClientConfiguration(clientCfg)//
                .build();

            transferManager = TransferManagerBuilder.standard()
                .withS3Client(s3desClientBuilder)
                .build();

            Copy copy = transferManager.copy(new CopyObjectRequest(sourceBucket, SOURCE_KEY,
                targetBucket, DESTINATION_KEY),
                s3ClientBuilder, null);
            copy.waitForCopyResult();   
        }
        transferManager.shutdownNow();
        s3ClientBuilder.shutdown();
        s3desClientBuilder.shutdown();
    }
}