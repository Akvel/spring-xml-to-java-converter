package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.xml.ConfigurationSearcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

class ConfigurationSearcherTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    @Test
    public void shouldAllFindXmls() {
        ConfigurationSearcher scanner = new ConfigurationSearcher(Set.of(root + "/src/test/resources/pro/akvel/spring/converter/xml/search/all"));
        Set<ConfigData> files = scanner.getConfigurations();

        Assertions.assertEquals(2, files.size());
        Assertions.assertTrue(files.stream().anyMatch(it -> it.getPath().getName().equals("1.xml")));
        Assertions.assertTrue(files.stream().anyMatch(it -> it.getPath().getName().equals("2.xml")));
    }


    @Test
    public void shouldReturnEmpty() {
        ConfigurationSearcher scanner = new ConfigurationSearcher(Set.of(root + "/src/test/resources/pro/akvel/spring/converter/xml/search/empty"));
        Set<ConfigData> files = scanner.getConfigurations();

        Assertions.assertTrue(files.isEmpty());
    }

    @Test
    public void shouldReturnErrorIfFolderNotFound() {
        ConfigurationSearcher scanner = new ConfigurationSearcher(Set.of(root + "/src/test/resources/pro/akvel/spring/converter/xml/search/unknown"));

        Assertions.assertThrows(IllegalArgumentException.class, scanner::getConfigurations);
    }
}