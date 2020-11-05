package org.openmbee.mms.storage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import org.apache.tika.mime.MimeTypes;
import org.openmbee.mms.artifacts.storage.ArtifactStorage;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.json.ElementJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Storage implements ArtifactStorage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AmazonS3 s3Client;

    @Value("${s3.endpoint}")
    private String ENDPOINT;

    @Value("${s3.access_key}")
    private String ACCESS_KEY;

    @Value("${s3.secret_key}")
    private String SECRET_KEY;

    @Value("${s3.region}")
    private String REGION;

    @Value("${s3.bucket:#{null}}")
    private Optional<String> BUCKET;


    private MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

    private AmazonS3 getClient() {
        if (s3Client == null) {
            AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setSignerOverride("AWSS3V4SignerType");

            s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

            if (!s3Client.doesBucketExistV2(getBucket())) {
                try {
                    s3Client.createBucket(getBucket());
                } catch (AmazonS3Exception e) {
                    throw new InternalErrorException(e);
                }
            }
        }

        return s3Client;
    }

    @Override
    public byte[] get(String location, ElementJson element, String mimetype) {
        GetObjectRequest rangeObjectRequest = new GetObjectRequest(getBucket(), location);
        try {
            return getClient().getObject(rangeObjectRequest).getObjectContent().readAllBytes();
        } catch (IOException ioe) {
            throw new NotFoundException(ioe);
        }
    }

    @Override
    public String store(byte[] data, ElementJson element, String mimetype) {
        String location = buildLocation(element, mimetype);
        ObjectMetadata om = new ObjectMetadata();
        om.setContentType(mimetype);
        om.setContentLength(data.length);

        PutObjectRequest por = new PutObjectRequest(getBucket(), location, new ByteArrayInputStream(data), om);

        try {
            getClient().putObject(por);
        } catch (RuntimeException e) {
            throw new InternalErrorException(e);
        }
        return location;
    }

    private String buildLocation(ElementJson element, String mimetype) {
        Date today = new Date();
        return String.format("%s/%s/%s/%d", element.getProjectId(), element.getId(), getExtension(mimetype), today.getTime());
    }

    private String getExtension(String mime) {
        String extension = "";
        try {
            extension = mimeTypes.forName(mime).getExtension().substring(1);
        } catch (Exception e) {
            logger.error("Error getting extension: ", e);
        }
        return extension;
    }

    private String getBucket() {
        String bucket = "mms";
        if (BUCKET.isPresent()) {
            bucket = BUCKET.get();
        }
        return bucket;
    }
}
