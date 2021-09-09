package pro.akvel.spring.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import pro.akvel.spring.converter.generator.ConfigurationData;
import pro.akvel.spring.converter.metadata.JavaConfigurationMetadataBuilder;
import pro.akvel.spring.converter.xml.ConfigurationDataConverter;
import pro.akvel.spring.converter.xml.ConfigurationSearcher;
import pro.akvel.spring.converter.xml.XmlConfigurationReader;
import pro.akvel.spring.converter.xml.write.XmlConfigurationWriter;

import java.io.File;

/**
 * Standalone convert application
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class Convertor {

    public static final String ARGS_XML_BASE_PATH = "xmlpath";
    public static final String ARGS_XML_SEARCH_MASK = "xmlmask";
    public static final String ARGS_OUTPUT_DIR = "odir";
    public static final String ARGS_BASE_PACKAGE_NAME = "opackage";

    public static final String DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE = "configs";
    private static String PROJECT_PATH = ".";

    /**
     * Path where {@link ConfigurationSearcher} search xml bean definitions
     */
    private static String DEFAULT_XML_SCAN_PATH = PROJECT_PATH + "/src/resources";

    private static String DEFAULT_JAVA_CONFIG_FILES_PATH = PROJECT_PATH + "/src/java/configs";

    private static boolean DEFAULT_REPLACE_XML_FILES = true;

    private static boolean ADD_JAVA_CONFIGS_TO_NEW_XML = true;

    private static boolean JAVA_CONFIGS_SAME_STRUCTURE_AS_XMLS = true;


    public static void main(String[] args) throws ParseException {

        Options options = new Options();
        options.addRequiredOption(ARGS_XML_BASE_PATH, "xmlbasepath", true, "Directory with xml-s. Default value: " + DEFAULT_XML_SCAN_PATH);
        options.addOption(ARGS_XML_SEARCH_MASK, "xmlsearchmask", true, "Files search mask, **/ - search in subdirectories. Default value: " + ConfigurationSearcher.DEFAULT_FILES_MASK);
        options.addOption(ARGS_OUTPUT_DIR, "outputdir", true, "Java files output directory. Default value: " + DEFAULT_JAVA_CONFIG_FILES_PATH);
        options.addOption(ARGS_BASE_PACKAGE_NAME, "outputbasepackagename", true, "Java classes base package name. Default value: " + DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE);

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            var path = cmd.getOptionValue(ARGS_XML_BASE_PATH, DEFAULT_XML_SCAN_PATH);
            var fileMask = cmd.getOptionValue(ARGS_XML_SEARCH_MASK, ConfigurationSearcher.DEFAULT_FILES_MASK);
            var basePackageName = cmd.getOptionValue(ARGS_BASE_PACKAGE_NAME, DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE);
            var configsPath = new File(cmd.getOptionValue(ARGS_OUTPUT_DIR, DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE));

            log.info("Start convertation with params:");
            log.info("\t{}={}", ARGS_XML_BASE_PATH, path);
            log.info("\t{}={}", ARGS_XML_SEARCH_MASK, fileMask);
            log.info("\t{}={}", ARGS_BASE_PACKAGE_NAME, basePackageName);
            log.info("\t{}={}", ARGS_OUTPUT_DIR, configsPath);

            ConfigurationSearcher scanner = new ConfigurationSearcher(path, fileMask);

            for (var file : scanner.getConfigurations()) {
                log.info("Read {}", file.getAbsolutePath());
                XmlConfigurationReader reader = new XmlConfigurationReader(file.getAbsolutePath());
                var beansData = ConfigurationDataConverter.getInstance().getConfigurationData(reader.getBeanFactory());

                log.info("Found beans that can be converted: {}", beansData.size());

                var meta = JavaConfigurationMetadataBuilder.getMetadata(
                        file.getAbsolutePath(),
                        configsPath.getAbsolutePath(),
                        basePackageName,
                        JAVA_CONFIGS_SAME_STRUCTURE_AS_XMLS
                );


                JavaConfigurationGenerator generator = new JavaConfigurationGenerator();

                generator.generateClass(
                        meta.getPackageName(),
                        meta.getJavaConfigFileClassName(),
                        ConfigurationData.builder()
                                .beans(beansData)
                                .build(),
                        DEFAULT_JAVA_CONFIG_FILES_PATH);

                //FIXME замена файла
                XmlConfigurationWriter writer = new XmlConfigurationWriter();
                writer.writeXmlWithoutConvertedBeans(
                        beansData,
                        file.getAbsolutePath(),
                        file + ".new"
                );
            }
        } catch (Exception e) {
            log.error("Convertaion error", e);

            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("spring-xml-to-java-converter", options);
        }
    }
}