package pro.akvel.spring.converter.xml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Xml configuration searcher
 *
 * @author akvel
 * @since 12.08.2020
 */
public class ConfigurationSearcher {
    /**
     * Directory for search
     */
    private final String path;

    public ConfigurationSearcher(String path) {
        this.path = path;
    }

    public List<File> getConfigurations() {
        File directory = new File(path);

        if (!directory.isDirectory()){
            throw new IllegalArgumentException("Directory not found");
        }

        String[] files = directory.list((dir, name) -> name.endsWith(".xml"));

        return Arrays.stream(files)
                .map(File::new)
                .collect(Collectors.toList());
    }
}
