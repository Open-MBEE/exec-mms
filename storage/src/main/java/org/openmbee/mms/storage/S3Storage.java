package org.openmbee.mms.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
public class S3Storage implements ArtifactStorage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private S3Client s3Client;

    @Value("${s3.endpoint}")
    private String ENDPOINT;

    @Value("${s3.access_key:#{null}}")
    private Optional<String> ACCESS_KEY;

    @Value("${s3.secret_key:#{null}}")
    private Optional<String> SECRET_KEY;

    @Value("${s3.region}")
    private String REGION;

    @Value("${s3.bucket:#{null}}")
    private Optional<String> BUCKET;


    private MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

    private S3Client getClient() {
        if (s3Client == null) {
            // Checksums only when an operation requires them, so S3-compatible stores keep working.
            S3ClientBuilder builder = S3Client.builder()
                .endpointOverride(URI.create(ENDPOINT))
                .region(Region.of(REGION))
                .forcePathStyle(true)
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED);

            if (ACCESS_KEY.isPresent() && SECRET_KEY.isPresent()) {
                builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(ACCESS_KEY.get(), SECRET_KEY.get())));
            } else {
                builder.credentialsProvider(DefaultCredentialsProvider.create());
            }
            S3Client client = builder.build();
            if (!bucketExists(client)) {
                try {
                    client.createBucket(request -> request.bucket(getBucket()));
                } catch (S3Exception e) {
                    throw new InternalErrorException(e);
                }
            }
            s3Client = client;
        }

        return s3Client;
    }

    // Only a 404 means the bucket is missing; a redirect or a denial means it exists elsewhere or is not ours.
    private boolean bucketExists(S3Client client) {
        try {
            client.headBucket(request -> request.bucket(getBucket()));
            return true;
        } catch (S3Exception e) {
            return e.statusCode() != 404;
        }
    }

    @Override
    public byte[] get(String location, ElementJson element, String mimetype) {
        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(getBucket()).key(location).build();
        try (InputStream content = getClient().getObject(objectRequest)) {
            return content.readAllBytes();
        } catch (IOException ioe) {
            throw new NotFoundException(ioe);
        }
    }

    @Override
    public String store(byte[] data, ElementJson element, String mimetype) {
        String location = buildLocation(element, mimetype);
        PutObjectRequest por = PutObjectRequest.builder()
            .bucket(getBucket())
            .key(location)
            .contentType(mimetype)
            .build();

        try {
            getClient().putObject(por, RequestBody.fromBytes(data));
        } catch (RuntimeException e) {
            logger.error("Error storing artifact: ", e);
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
