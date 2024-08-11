package br.com.jproject.apptarefas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URL;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucketName}") String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public Mono<String> uploadFile(String key, MultipartFile file) {
        return Mono.fromCallable(() -> {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            URL url = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return url.toString();
        });
    }
}
