package org.mpdev.projects.tsreports.utils;

import org.mpdev.projects.tsreports.TSReports;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class FileUtils {

    public static Set<File> getFilesIn(String path, Predicate<? super Path> filter) {
        Set<File> files = new LinkedHashSet<>();
        String packagePath = path.replace(".", "/");
        try {
            URI uri = TSReports.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + uri), Collections.emptyMap());
            files = Files.walk(fileSystem.getPath(packagePath)).
                    filter(Objects::nonNull).
                    filter(filter).
                    map(p -> new File(p.toString())).
                    collect(Collectors.toSet());
            fileSystem.close();
        } catch (URISyntaxException | IOException ex) {
            TSReports.getInstance().getLogger().
                    log(Level.SEVERE, "An error occurred while trying to load files: " + ex.getMessage(), ex);
        }
        return files;
    }

}
