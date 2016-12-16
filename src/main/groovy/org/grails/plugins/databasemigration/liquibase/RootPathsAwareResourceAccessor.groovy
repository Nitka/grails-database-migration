package org.grails.plugins.databasemigration.liquibase

import groovy.transform.CompileStatic
import liquibase.resource.FileSystemResourceAccessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.zip.GZIPInputStream

@CompileStatic
class RootPathsAwareResourceAccessor extends FileSystemResourceAccessor {
    private static final Logger log = LoggerFactory.getLogger(this);

    RootPathsAwareResourceAccessor(String base) {
        super(base)
    }

    @Override
    Set<InputStream> getResourcesAsStream(String path) throws IOException {
        Set<InputStream> inputStreams = super.getResourcesAsStream(path)
        if (inputStreams == null) {
            log.debug("Base directory ${toString()} does not contain $path , try root paths")
            InputStream fileStream = null;
            for (String rootPath : rootPaths) {
                log.debug("Try $rootPath ...")
                File file = (rootPath) ? new File(path) : new File(rootPath, path)
                try {
                    fileStream = openStream(file)
                    log.debug("$rootPath contains $path")
                    break;
                } catch (FileNotFoundException e2) {
                    log.debug("$rootPath does not contain $path")
                    //try next rootPath
                }
            }
            if (fileStream != null) {
                inputStreams = new HashSet<>()
                inputStreams << fileStream
            }
        }
        return inputStreams
    }

    private InputStream openStream(File file) throws IOException, FileNotFoundException {
        if (file.getName().toLowerCase().endsWith(".gz")) {
            return new BufferedInputStream(new GZIPInputStream(new FileInputStream(file)));
        } else {
            return new BufferedInputStream(new FileInputStream(file));
        }
    }
}
