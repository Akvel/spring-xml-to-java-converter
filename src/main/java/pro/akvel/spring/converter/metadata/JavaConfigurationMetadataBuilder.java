package pro.akvel.spring.converter.metadata;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;

import javax.annotation.Nullable;
import java.io.File;

/**
 * @author akvel
 * @since 12.09.2021
 */
@UtilityClass
@Slf4j
public class JavaConfigurationMetadataBuilder {

    @SneakyThrows
    public JavaConfigurationMetadata getMetadata(@NonNull String filePath,
                                                 @NonNull String baseConfigurationPath,
                                                 @NonNull String basePackageName,
                                                 @Nullable String configsPath) {
        log.debug("Get metadata: filePath={}, baseConfigurationPath={}", filePath, baseConfigurationPath);
        File file = new File(filePath);

        return JavaConfigurationMetadata.builder()
                .packageName(basePackageName)
                .className(getClassName(file.getName()))
                .configsPath(getOutputBaseConfigPath(configsPath, file.getParent()))
                .build();
    }

    private static String getOutputBaseConfigPath(@Nullable String configsPath, @NonNull String fileDirectoryPath) {
        if (configsPath != null) {
            return configsPath;
        }

        int indexResource = fileDirectoryPath.lastIndexOf("resources");
        if (indexResource != -1) {
            //src
            //  main
            //    java
            //    --> set this path
            //    resources
            return fileDirectoryPath.substring(0, indexResource) + "java";

        } else {
            return fileDirectoryPath;
        }
    }

    static String getPackageName(String basePackageName,
                                 String configFileAbsolutePath,
                                 String baseConfigAbsolutePath) {
        if (baseConfigAbsolutePath.startsWith(".")) {
            throw new IllegalArgumentException("Config base path not absolute " + baseConfigAbsolutePath);
        }

        if (configFileAbsolutePath.startsWith(".")) {
            throw new IllegalArgumentException("Config file path not absolute " + baseConfigAbsolutePath);
        }

        if (!configFileAbsolutePath.startsWith(baseConfigAbsolutePath)) {
            throw new IllegalArgumentException("Files path should start with base path file:" + configFileAbsolutePath + " base:" + baseConfigAbsolutePath);
        }


        String added = configFileAbsolutePath
                .substring(baseConfigAbsolutePath.length())
                .replaceAll("[/\\\\]", ".")
                .replaceAll("[-\\s]", "_")
                .toLowerCase();
        return basePackageName + (added.isEmpty() ? "" :
                added.startsWith(".") ? added : "." + added);
    }

    static String getClassName(String name) {
        return CaseUtils.toCamelCase(addSpaces(name.split("\\.")[0]),
                true,
                ' ', '-', '_', ',');
        //+ ".java";
    }

    private static String addSpaces(String s) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                result.append(" ");
            }

            result.append(s.charAt(i));
        }


        return result.toString();
    }

}
