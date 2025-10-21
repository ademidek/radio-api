package com.example.api.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/admin/debug")
class DebugController {
  private final software.amazon.awssdk.services.sts.StsClient sts;
  private final software.amazon.awssdk.services.s3.S3Client s3;
  private final String bucket;

  DebugController(software.amazon.awssdk.services.sts.StsClient sts,
                  software.amazon.awssdk.services.s3.S3Client s3,
                  @Value("${aws.s3.bucket}") String bucket) {
    this.sts = sts; this.s3 = s3; this.bucket = bucket;
  }

  @GetMapping("/aws")
  Map<String,Object> aws() {
    var ident = sts.getCallerIdentity();
    var probe = s3.listObjectsV2(b->b.bucket(bucket).maxKeys(1));
    return Map.of(
      "account", ident.account(),
      "userArn", ident.arn(),
      "region", s3.serviceClientConfiguration().region().toString(),
      "bucket", bucket,
      "objectsFound", probe.keyCount()
    );
  }
}
