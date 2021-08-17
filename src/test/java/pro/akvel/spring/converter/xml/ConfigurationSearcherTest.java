package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.xml.ConfigurationSearcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class ConfigurationSearcherTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    @Test
    public void shouldAllFindXmls() {
        ConfigurationSearcher scanner = new ConfigurationSearcher(root + "/src/test/resources/pro/akvel/spring/converter/xml/search/all");
        List<File> files = scanner.getConfigurations();

        Assertions.assertEquals(2, files.size());
        Assertions.assertTrue(files.stream().anyMatch(it -> it.getName().equals("1.xml")));
        Assertions.assertTrue(files.stream().anyMatch(it -> it.getName().equals("2.xml")));
    }


    @Test
    public void shouldReturnEmpty() {
        ConfigurationSearcher scanner = new ConfigurationSearcher(root + "/src/test/resources/pro/akvel/spring/converter/xml/search/empty");
        List<File> files = scanner.getConfigurations();

        Assertions.assertTrue(files.isEmpty());
    }

    @Test
    public void shouldReturnErrorIfFolderNotFound() {
        ConfigurationSearcher scanner = new ConfigurationSearcher(root + "/src/test/resources/pro/akvel/spring/converter/xml/search/unknown");

        Assertions.assertThrows(IllegalArgumentException.class, scanner::getConfigurations);
    }
}