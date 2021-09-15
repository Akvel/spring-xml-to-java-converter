package pro.akvel.spring.converter.java;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.metadata.JavaConfigurationMetadata;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class JavaMainConfigurationGeneratorTest {
    private static final String OUTPUT_PATH = "build/tmp/";
    public static final String EXPECTED_CLASS_PATH = "src/test/resources/pro/akvel/spring/converter/xml/expected/";
    public static final String PACKAGE_NAME = "pro.akvel.spring.converter.generator";

    @Test
    public void checkMainClass() {
        var gen = JavaMainConfigurationGenerator.getInstance();

        String configurationName = "TestMainConfiguration";
        gen.createMainConfiguration(
                PACKAGE_NAME,
                configurationName,
                OUTPUT_PATH,
                new LinkedHashSet<>(
                        List.of(JavaConfigurationMetadata.builder()
                                        .className("JavaConfig")
                                        .packageName("pro.akve.test.conf.subdir")
                                        .build(),
                                JavaConfigurationMetadata.builder()
                                        .className("JavaConfig1")
                                        .packageName("pro.akvel.spring.converter.generator")
                                        .build()
                        )
                )
        );

        Path generatedFile = Paths.get(getPath(configurationName));
        Path expectedFile = Paths.get(EXPECTED_CLASS_PATH + configurationName + ".java");

        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));


    }

    @SneakyThrows
    private static List<String> getLines(Path expectedFile) {
        return Files.readAllLines(expectedFile, StandardCharsets.UTF_8)
                .stream()
                .map(it -> it.replaceAll("[^;*@.()\\[\\]a-zA-Z0-9]", ""))
                .collect(Collectors.toList());
    }

    private static String getPath(String configClassName) {
        return OUTPUT_PATH +
                PACKAGE_NAME.replaceAll("\\.", "\\/") + "/" + configClassName + ".java";
    }
}