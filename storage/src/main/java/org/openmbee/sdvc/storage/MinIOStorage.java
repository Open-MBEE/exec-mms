package org.openmbee.sdvc.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.RegionConflictException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.apache.tika.mime.MimeTypes;
import org.openmbee.sdvc.artifacts.storage.ArtifactStorage;
import org.openmbee.sdvc.json.ElementJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOStorage implements ArtifactStorage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MinioClient minio;

    @Value("${minio.endpoint}")
    private String ENDPOINT;

    @Value("${minio.access_key}")
    private String ACCESS_KEY;

    @Value("${minio.secret_key}")
    private String SECRET_KEY;

    @Value("${minio.bucket:#{null}}")
    private Optional<String> BUCKET;

    @Value("${minio.region:#{null}}")
    private Optional<String> REGION;

    private MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

    private MinioClient getClient()
        throws IOException, InvalidKeyException, InvalidResponseException, RegionConflictException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        if (minio == null) {
            MinioClient.Builder builder = MinioClient.builder().endpoint(ENDPOINT).credentials(ACCESS_KEY, SECRET_KEY);
            REGION.ifPresent(builder::region);
            minio = builder.build();
            boolean isExist = minio.bucketExists(BucketExistsArgs.builder().bucket(getBucket()).build());
            if (!isExist) {
                minio.makeBucket(MakeBucketArgs.builder().bucket(getBucket()).build());
            }
        }
        return minio;
    }

    @Override
    public byte[] get(String location, ElementJson element, String mimetype) {
        GetObjectArgs goa = GetObjectArgs.builder()
            .bucket(getBucket())
            .object(location)
            .build();
        byte[] ba = null;
        try {
            ba = getClient().getObject(goa).readAllBytes();
        } catch (Exception e) {
            logger.error("Error getting object: ", e);
        }
        return ba;
    }

    @Override
    public String store(byte[] data, ElementJson element, String mimetype) {
        String location = buildLocation(element, mimetype);
        PutObjectArgs poa = PutObjectArgs.builder()
            .bucket(getBucket())
            .object(location)
            .contentType(mimetype)
            .stream(new ByteArrayInputStream(data), -1,  1048576 * 5) // 5MB Part size
            .build();

        try {
            getClient().putObject(poa);
        } catch (Exception e) {
            logger.error("Error putting object: ", e);
        }
        return location;
    }

    private String buildLocation(ElementJson element, String mimetype) {
        int it = 1;
        try {
            Iterable<Result<Item>> results = getClient().listObjects(
                ListObjectsArgs.builder().bucket(getBucket()).build());
            for (Result<Item> result : results) {
                it++;
            }
        } catch (Exception e) {
            logger.error("Error building location: ", e);
        }
        return String.format("%s//%s//%s//v%d", element.getProjectId(), element.getId(), getExtension(mimetype), it);
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
