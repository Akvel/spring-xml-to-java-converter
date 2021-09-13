package pro.akvel.spring.converter.metadata;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.CaseUtils;

import java.io.File;

@UtilityClass
@Slf4j
public class JavaConfigurationMetadataBuilder {

    @SneakyThrows
    public JavaConfigurationMetadata getMetadata(String filePath,
                                                 String baseConfigurationPath,
                                                 String basePackageName,
                                                 boolean addXmlPatchToPackageName) {
        log.debug("Get metadata: filePath={}, baseConfigurationPath={}", filePath, baseConfigurationPath);
        File file = new File(filePath);

        return JavaConfigurationMetadata.builder()
                .packageName(addXmlPatchToPackageName
                        ? getPackageName(basePackageName,
                        file.getParent(),
                        new File(baseConfigurationPath)
                                .getCanonicalPath())
                        : basePackageName)
                .javaConfigFileClassName(getClassName(file.getName()))
                .build();
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

        if (!configFileAbsolutePath.startsWith(baseConfigAbsolutePath)){
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
