package pro.akvel.spring.converter.xml;

import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Xml configuration searcher
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class ConfigurationSearcher {
    public static final String DEFAULT_FILES_MASK = "**/*.xml";

    /**
     * Directory for search
     */
    private final Set<String> paths;
    private final String fileMask;

    public ConfigurationSearcher(Set<String> paths) {
        this(paths, DEFAULT_FILES_MASK);
    }

    public ConfigurationSearcher(Set<String> paths,
                                 String fileMask) {
        this.paths = paths;
        this.fileMask = fileMask;
    }

    public Set<ConfigData> getConfigurations() {
        log.debug("Path: {}, mask: {}", paths, fileMask);

        paths.forEach(path -> {
            if (!new File(path).isDirectory()) {
                throw new IllegalArgumentException("Directory not found: " + path);
            }
        });

        return paths.stream().flatMap(path -> {
                    DirectoryScanner scanner = new DirectoryScanner();
                    scanner.setIncludes(new String[]{fileMask});
                    scanner.setBasedir(path);
                    scanner.setCaseSensitive(false);
                    scanner.scan();
                    String[] files = scanner.getIncludedFiles();
                    log.debug("Path: {}. Found files {}", path, Arrays.toString(files));
                    return Arrays.stream(files)
                            .map(it -> ConfigData.builder()
                                    .path(new File(path + "/" + it))
                                    .sourcePath(path)
                                    .build());
                })
                .collect(Collectors.toSet());


    }
}
