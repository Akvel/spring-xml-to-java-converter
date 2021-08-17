package pro.akvel.spring.converter;

import pro.akvel.spring.converter.xml.ConfigurationSearcher;

/**
 * Standalone convert application
 *
 * @author akvel
 * @since 12.08.2020
 */
public class Convertor {

    /**
     * Path where {@link ConfigurationSearcher} search xml bean definitions
     */
    private static String DEFAULT_SCAN_PATH = "./src/resources";

    private static String DEFAULT_JAVA_CONFIG_FILES_PATH = "./src/java/configs";

    private static boolean DEFAULT_REPLACE_XML_FILES = true;



    public static void main(String[] args) {
        ConfigurationSearcher scanner = new ConfigurationSearcher(
                "src/test/resources/pro/akvel/spring/converter");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
