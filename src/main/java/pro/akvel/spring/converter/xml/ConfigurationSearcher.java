package pro.akvel.spring.converter.xml;

import lombok.extern.log4j.Log4j;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Xml configuration searcher
 *
 * @author akvel
 * @since 12.08.2020
 */
@Log4j
public class ConfigurationSearcher {
    public static final String DEFAULT_FILES_MASK = "**/*.xml";

    /**
     * Directory for search
     */
    private final String path;
    private final String fileMask;

    public ConfigurationSearcher(String path) {
        this(path, DEFAULT_FILES_MASK);
    }

    public ConfigurationSearcher(String path, String fileMask) {
        this.path = path;
        this.fileMask = fileMask;
    }

    public List<File> getConfigurations() {
        log.info("Path: " + path + ", mask: " + fileMask);

        if (!new File(path).isDirectory()) {
            throw new IllegalArgumentException("Directory not found: " + path);
        }

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{fileMask});
        scanner.setBasedir(path);
        scanner.setCaseSensitive(false);
        scanner.scan();
        //FIXME тут только имена файлов, нужно проверить как отдаются имена файлов в поддиректориях
        String[] files = scanner.getIncludedFiles();

        log.info(Arrays.toString(files));

        return Arrays.stream(files)
                .map(it -> new File(path + "/" + it))
                .collect(Collectors.toList());
    }
}
