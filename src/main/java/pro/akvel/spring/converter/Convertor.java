package pro.akvel.spring.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import pro.akvel.spring.converter.metadata.JavaConfigurationMetadataBuilder;
import pro.akvel.spring.converter.xml.ConfigurationDataConverter;
import pro.akvel.spring.converter.xml.ConfigurationSearcher;
import pro.akvel.spring.converter.xml.XmlConfigurationReader;
import pro.akvel.spring.converter.xml.write.XmlConfigurationWriter;

import java.io.File;
import java.io.FilterOutputStream;
import java.lang.reflect.Field;

/**
 * Standalone convert application
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class Convertor {

    private static final String ARGS_XML_BASE_PATH = "xmlpath";
    private static final String ARGS_XML_SEARCH_MASK = "xmlmask";
    private static final String ARGS_OUTPUT_DIR = "odir";
    private static final String ARGS_BASE_PACKAGE_NAME = "opackage";

    private static final String DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE = "configs";
    private static final String ARGS_PRINT_HELP = "help";
    private static final String ARGS_LOG_LEVEL = "loglevel";

    private static String PROJECT_PATH = ".";

    private static String DEFAULT_JAVA_CONFIG_FILES_PATH = PROJECT_PATH + "/src/main/java/";

    private static boolean JAVA_CONFIGS_SAME_STRUCTURE_AS_XMLS = true;


    public static void main(String[] args) {
        try {
            suppressWarning();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Options options = new Options();
        options.addOption(ARGS_XML_BASE_PATH, "xmlbasepath", true, "Directory with XML-s.");
        options.addOption(ARGS_XML_SEARCH_MASK, "xmlfilesearchmask", true, "Files search mask, **/ - search in subdirectories. Default value: " + ConfigurationSearcher.DEFAULT_FILES_MASK);
        options.addOption(ARGS_OUTPUT_DIR, "outputdir", true, "Java source base directory. Default value: " + DEFAULT_JAVA_CONFIG_FILES_PATH);
        options.addOption(ARGS_BASE_PACKAGE_NAME, "outputbasepackagename", true, "Java classes base package name. Default value: " + DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE);
        options.addOption("h", ARGS_PRINT_HELP, false, "Print this help");
        options.addOption("l", ARGS_LOG_LEVEL, true, "Log level (TRACE/DEBUG/INFO/WARN/ERROR). Default value: INFO");

        try {
            //FIXME проверить запуск со всеми параметрами
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            //print help
            if (!cmd.hasOption(ARGS_XML_BASE_PATH) || cmd.hasOption(ARGS_PRINT_HELP)) {
                log.info("Please set " + ARGS_XML_BASE_PATH + " param");
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("spring-xml-to-java-converter", options);
                return;
            }

            if (cmd.hasOption(ARGS_LOG_LEVEL)) {
                log.info("Change log level to {}", cmd.getOptionValue(ARGS_LOG_LEVEL));
                org.apache.log4j.Logger logger4j = org.apache.log4j.Logger.getRootLogger();
                logger4j.setLevel(org.apache.log4j.Level.toLevel(cmd.getOptionValue(ARGS_LOG_LEVEL)));
            }

            var path = cmd.getOptionValue(ARGS_XML_BASE_PATH)
                    .replaceAll("\\\\", "/");


            var fileMask = cmd.getOptionValue(ARGS_XML_SEARCH_MASK, ConfigurationSearcher.DEFAULT_FILES_MASK);
            var basePackageName = cmd.getOptionValue(ARGS_BASE_PACKAGE_NAME, DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE);
            var configsPath = cmd.getOptionValue(ARGS_OUTPUT_DIR, DEFAULT_JAVA_CONFIG_FILES_PATH)
                    .replaceAll("\\\\", "/");

            log.info("");
            log.info("Start convertation with params:");
            log.info("\t{}={}", ARGS_XML_BASE_PATH, path);
            log.info("\t{}={}", ARGS_XML_SEARCH_MASK, fileMask);
            log.info("\t{}={}", ARGS_BASE_PACKAGE_NAME, basePackageName);
            log.info("\t{}={}", ARGS_OUTPUT_DIR, configsPath);
            //FIXME OVERRIDE


            log.info("");
            log.info("Search files");
            var configs = new ConfigurationSearcher(
                    new File(path).getAbsolutePath(), fileMask).getConfigurations();
            log.info("Found files count: {}", configs.size());

            for (var file : configs) {
                log.info("");
                log.info("Read XML file: {}", file.getAbsolutePath());
                XmlConfigurationReader reader = new XmlConfigurationReader(file.getAbsolutePath());
                var beansData = ConfigurationDataConverter.getInstance().getConfigurationData(reader.getBeanFactory());

                log.info("Beans converted: {}, skipped: {}",
                        beansData.getConvertedBeansCount(),
                        beansData.getSkippedBeansCount());

                if (beansData.getConvertedBeansCount() == 0){
                    log.debug("Can not convert any beans in {}. Skip java file generation", file);
                    continue;
                }

                var meta = JavaConfigurationMetadataBuilder.getMetadata(
                        file.getAbsolutePath(),
                        new File(path).getAbsolutePath(),
                        basePackageName,
                        JAVA_CONFIGS_SAME_STRUCTURE_AS_XMLS
                );


                JavaConfigurationGenerator generator = new JavaConfigurationGenerator();

                log.info("");
                log.info("Generate java config file: path:{} class:{}",
                        new File(configsPath).getAbsolutePath(),
                        meta.getJavaConfigFileClassName());

                generator.generateClass(
                        meta.getPackageName(),
                        meta.getJavaConfigFileClassName(),
                        beansData.getBeans(),
                        configsPath);

                //FIXME замена файла
                String newXmlFile = file + ".new";
                log.info("");
                log.info("Write new xml without converted beans: {}", newXmlFile);
                XmlConfigurationWriter writer = new XmlConfigurationWriter();

                writer.writeXmlWithoutConvertedBeans(
                        beansData.getBeans(),
                        file.getAbsolutePath(),
                        newXmlFile
                );
            }
            log.info("Done");
        } catch (Exception e) {
            log.error("Convertaion error", e);
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("spring-xml-to-java-converter", options);
        }
    }

    //hide all reflections warning
    //https://stackoverflow.com/a/61700723/442050
    private static void suppressWarning() throws Exception {
        Field f = FilterOutputStream.class.getDeclaredField("out");
        Runnable r = () -> {
            f.setAccessible(true);
            synchronized (Convertor.class) {
                Convertor.class.notify();
            }
        };
        Object errorOutput;
        synchronized (Convertor.class) {
            synchronized (System.err) //lock System.err to delay the warning
            {
                new Thread(r).start(); //One of these 2 threads will
                new Thread(r).start(); //hang, the other will succeed.
                Convertor.class.wait(); //Wait 1st thread to end.
                errorOutput = f.get(System.err); //Field is now accessible, set
                f.set(System.err, null); // it to null to suppress the warning

            } //release System.err to allow 2nd thread to complete.
            Convertor.class.wait(); //Wait 2nd thread to end.
            f.set(System.err, errorOutput); //Restore System.err
        }
    }
}