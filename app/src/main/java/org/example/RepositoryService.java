package org.example;

import java.nio.file.Path;

import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;

public class RepositoryService {

    private OcflRepository repo;

    public RepositoryService(Path rootPath, Path workPath) {
        this.repo = new OcflRepositoryBuilder()
            .prettyPrintJson()
            .defaultLayoutConfig(new HashedNTupleLayoutConfig())
            .storage(storage -> storage.fileSystem(rootPath))
            .workDir(workPath)
            .build();
    }

    private VersionInfo createNewVersion(User user, String message) {
        return new VersionInfo().setUser(user.username(), user.email()).setMessage(message);
    }

    public RepositoryService createObject(String id, Path inputPath, User user, String message) {
        repo.putObject(
            ObjectVersionId.head(id),
            inputPath,
            createNewVersion(user, message)
        );
        return this;
    }

    public RepositoryService readObject(String id, Path outputPath) {
        repo.getObject(ObjectVersionId.head(id), outputPath);
        return this;
    }

    public RepositoryService deleteObject(String id) {
        repo.purgeObject(id);
        return this;
    }

    public boolean hasObject(String id) {
        return repo.containsObject(id);
    }

    public RepositoryService deleteObjectFile(String objectId, String filePath, User user, String message) {
        repo.updateObject(
            ObjectVersionId.head(objectId),
            createNewVersion(user, message),
            updater -> { updater.removeFile(filePath); }
        );
        return this;
    }

    public RepositoryService updateObjectFile(
        String objectId, Path inputPath, String filePath, User user, String message
    ) {
        repo.updateObject(
            ObjectVersionId.head(objectId),
            createNewVersion(user, message),
            updater -> { updater.addPath(inputPath, filePath, OcflOption.OVERWRITE); }
        );
        return this;
    }
}
