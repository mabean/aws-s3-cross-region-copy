@Grab(group = 'com.amazonaws', module = 'aws-java-sdk-core', version = '1.12.642')
@Grab(group = 'com.amazonaws', module = 'aws-java-sdk-s3', version = '1.12.642')

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.model.CopyObjectRequest;
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
            .withEndpointConfiguration(new EndpointConfiguration("https://s3.${sourceRegion}.amazonaws.com", sourceRegion))//
            .withClientConfiguration(clientCfg)//
            .build();
        script.echo("Looking for ${sourceFile} on ${sourceBucket}")
        s3desClientBuilder = AmazonS3ClientBuilder//
            .standard()//
            .withCredentials(credentialProvidor)//
            .withEndpointConfiguration(new EndpointConfiguration("https://s3.${targetRegion}.amazonaws.com", targetRegion))//
            .withClientConfiguration(clientCfg)//
            .build();
        transferManager = TransferManagerBuilder.standard()
            .withS3Client(s3desClientBuilder)
            .build();
        def fileName = sourceFile.substring(sourceFile.lastIndexOf('/') + 1, sourceFile.length())
        Copy copy = transferManager.copy(new CopyObjectRequest(sourceBucket, sourceFile,
            targetBucket, "${targetPath}/${fileName}"),
            s3ClientBuilder, null);
        copy.waitForCopyResult();   

        transferManager?.shutdownNow();
        s3ClientBuilder?.shutdown();
        s3desClientBuilder?.shutdown();
    }
}