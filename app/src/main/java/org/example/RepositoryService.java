package org.example;

import java.nio.file.Path;

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

    public RepositoryService createObject(String id, Path inputPath, String message, User user) {
        repo.putObject(
            ObjectVersionId.head(id),
            inputPath,
            new VersionInfo().setUser(user.username(), user.email()).setMessage(message)
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


    // public RepositoryService updateObject(...)
    // }
}