# OCI Object Storage Usage Guide

This guide explains how application developers and AI coding assistants should configure and use
the shared `OciObjectStorageService`.

## Scope and implementation

The shared service is implemented in
[`OciObjectStorageService.java`](../../p2p-common/src/main/java/com/p2p/common/service/OciObjectStorageService.java).
It provides synchronous operations for one configured OCI Object Storage namespace and bucket:

- `uploadObject(...)`
- `downloadObject(...)`
- `deleteObject(...)`
- `objectExists(...)`

The OCI client is created by
[`OciStorageConfig.java`](../../p2p-common/src/main/java/com/p2p/common/configs/OciStorageConfig.java)
using values bound to
[`OciStorageProperties.java`](../../p2p-common/src/main/java/com/p2p/common/configs/OciStorageProperties.java).
Spring closes the shared client when the application context shuts down.

The service performs storage operations only. Applications remain responsible for authorization,
business rules, object-key design, and persistence of any metadata.

## Configuration

Provide all five properties under the `oci.object-storage` prefix:

```yaml
oci:
  object-storage:
    region: ${OCI_OBJECT_STORAGE_REGION}
    namespace: ${OCI_OBJECT_STORAGE_NAMESPACE}
    bucket: ${OCI_OBJECT_STORAGE_BUCKET}
    config-file: ${OCI_CONFIG_FILE}
    profile: ${OCI_CONFIG_PROFILE:DEFAULT}
```

Property meanings:

| Property | Meaning |
| --- | --- |
| `region` | OCI region identifier, for example `ap-singapore-1`. |
| `namespace` | OCI Object Storage namespace that owns the bucket. |
| `bucket` | Name of the bucket used by this service. |
| `config-file` | Path to the OCI CLI configuration file containing the selected profile and credentials. |
| `profile` | Profile name in the OCI configuration file; the example defaults to `DEFAULT`. |

Use the deployment environment or a secret manager to provide credentials. Do not commit private
keys, tokens, or credential files to the repository. The configured OCI identity must have the
permissions required for the operations the application uses.

Ensure that `OciStorageConfig` and `OciObjectStorageService` are included in Spring's application
context. In this repository they are under `com.p2p.common`; applications whose component scan does
not include that package must import or otherwise register the configuration and service.

## Injecting the service

Inject the Spring-managed service into the application component that needs object storage:

```java
private final OciObjectStorageService objectStorageService;

public CourseFileService(OciObjectStorageService objectStorageService) {
  this.objectStorageService = objectStorageService;
}
```

Do not construct another OCI client in a business service. Do not close the injected `ObjectStorage`
client; its lifecycle belongs to Spring.

## Operations

### Upload

```java
try (InputStream content = file.getInputStream()) {
  objectStorageService.uploadObject(
      objectKey,
      content,
      file.getSize(),
      contentType);
}
```

`objectKey` and `contentType` must not be blank, `content` must not be null, and
`contentLength` must be non-negative. An existing object with the same key is overwritten. The
service does not explicitly close the supplied upload stream; manage its lifecycle at the call site.

### Download

```java
try (InputStream content = objectStorageService.downloadObject(objectKey)) {
  Files.copy(content, destination, StandardCopyOption.REPLACE_EXISTING);
}
```

The returned stream must be closed by the caller. A missing object results in an
`OciObjectStorageException` with `RESOURCE_NOT_FOUND`.

### Delete

```java
objectStorageService.deleteObject(objectKey);
```

A missing object (OCI status `404`) is treated as successful deletion. Other OCI SDK
`BmcException` failures are wrapped in `OciObjectStorageException`.

### Check existence

```java
boolean exists = objectStorageService.objectExists(objectKey);
```

The method returns `false` for OCI status `404`; other OCI SDK `BmcException` failures are wrapped
in `OciObjectStorageException`.

## Errors and API responses

The service uses the common exception types:

| Situation | Exception / error code |
| --- | --- |
| Blank object key or content type, null upload stream, or negative content length | `ValidationException` / `VALIDATION_FAILED` |
| Upload failure reported as OCI `BmcException` | `OciObjectStorageException` / `SERVICE_UNAVAILABLE` |
| Download of a missing object | `OciObjectStorageException` / `RESOURCE_NOT_FOUND` |
| Other download, delete, or existence-check failure reported as OCI `BmcException` | `OciObjectStorageException` / `SERVICE_UNAVAILABLE` |
| Delete of a missing object | No exception |
| Existence check of a missing object | Returns `false` |

`OciObjectStorageException` retains the OCI exception as its cause. Do not discard the cause or
convert storage errors into a successful-looking result.

`ErrorCode` associates these codes with HTTP statuses (`VALIDATION_FAILED` 400,
`RESOURCE_NOT_FOUND` 404, and `SERVICE_UNAVAILABLE` 503). An HTTP response only receives those
statuses when the consuming application's exception handler maps `BusinessException` using its
`ErrorCode`. Do not assume that throwing one of these exceptions automatically produces the mapped
response. Check the handler used by the application; the current API Gateway handler falls back to
`INTERNAL_ERROR` for exceptions other than `ResponseStatusException`.

## Object-key guidance

An object key is the OCI object name, not a local filesystem path. The current service only checks
that the key is not blank. Choose keys according to the consuming application's authorization and
naming rules, and do not treat an object key as proof that the caller may access that object.

## Guidance for AI coding assistants

When integrating or changing code that uses this service:

1. Read this guide and the service implementation before editing.
2. Inject and reuse `OciObjectStorageService`; do not create a duplicate storage client or duplicate
   the service's request-building logic.
3. Use the existing four operations and preserve their documented semantics, especially overwrite
   on upload, stream closure by the download caller, and the 404 behavior for delete and existence
   checks.
4. Validate business authorization and metadata requirements in the consuming service; this shared
   service does not implement them.
5. Propagate `ValidationException` and `OciObjectStorageException` to the application's established
   exception handler. Do not swallow them, return success on a failed operation, or expose OCI
   credentials in logs or responses.
6. If changing configuration, exception behavior, or operation semantics, update this guide and
   the directly related JavaDoc. Verify the relevant Checkstyle, formatting, and tests.
