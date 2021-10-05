package pro.akvel.spring.converter;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.config.BeanDefinition;
import pro.akvel.spring.converter.java.JavaConfigurationGenerator;
import pro.akvel.spring.converter.java.JavaGeneratorParams;
import pro.akvel.spring.converter.java.JavaMainConfigurationGenerator;
import pro.akvel.spring.converter.metadata.JavaConfigurationMetadata;
import pro.akvel.spring.converter.metadata.JavaConfigurationMetadataBuilder;
import pro.akvel.spring.converter.xml.ConfigData;
import pro.akvel.spring.converter.xml.ConfigurationDataConverter;
import pro.akvel.spring.converter.xml.ConfigurationSearcher;
import pro.akvel.spring.converter.xml.XmlConfigurationReader;
import pro.akvel.spring.converter.xml.write.XmlConfigurationWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FilterOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standalone convert application
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
@Generated //need for skip any jacoco attempts to scan this file
public class Convertor {

    private static final String ARGS_XML_BASE_PATH = "xp";
    private static final String ARGS_XML_SEARCH_MASK = "xm";
    private static final String ARGS_OUTPUT_DIR = "op";
    private static final String ARGS_BASE_PACKAGE_NAME = "p";
    private static final String ARGS_PRINT_HELP = "help";
    private static final String ARGS_LOG_LEVEL = "loglevel";
    private static final String ARGS_WRITE_MAIN_CLASS = "xmlmainconfig";
    private static final String ARGS_XML_FILE = "xmlfile";

    private static final String DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE = "configs";

    private static final String PROJECT_PATH = ".";

    private static final String DEFAULT_JAVA_CONFIG_FILES_PATH = PROJECT_PATH + "/src/main/java/";

    private static final boolean JAVA_CONFIGS_SAME_STRUCTURE_AS_XMLS = true;
    public static final String PRINT_PARAMS_FORMATTER = "\t{}={}";


    public static void main(String[] args) throws InterruptedException {
        try {
            suppressWarning();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.debug("suppressWarning error", e);
        }


        Options options = new Options();
        options.addOption(ARGS_XML_BASE_PATH, "xmlbasepath", true, "Directory with ALL XML configs. You can specify more then one directory separate it by comma (,) ");
        options.addOption(ARGS_XML_SEARCH_MASK, "xmlfilesearchmask", true, "Files search mask, **/ - search in subdirectories. Default value: " + ConfigurationSearcher.DEFAULT_FILES_MASK);
        options.addOption(ARGS_OUTPUT_DIR, "outputpath", true, "Java source base directory. Default value: " + DEFAULT_JAVA_CONFIG_FILES_PATH);
        options.addOption(ARGS_BASE_PACKAGE_NAME, "outputbasepackagename", true, "Java classes base package name. Default value: " + DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE);
        options.addOption("h", ARGS_PRINT_HELP, false, "Print this help");
        options.addOption("l", ARGS_LOG_LEVEL, true, "Log level (TRACE/DEBUG/INFO/WARN/ERROR). Default value: INFO");
        options.addOption("wm", ARGS_WRITE_MAIN_CLASS, true, "Create main config class. Default: true");
        options.addOption("xf", ARGS_XML_FILE, true, "Path to XML file. Convert only one configuration. xmlbasepath also needed to find all refs");


        try {
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

            var paths = convertWinPathToUnixPath(cmd.getOptionValue(ARGS_XML_BASE_PATH));
            var fileMask = cmd.getOptionValue(ARGS_XML_SEARCH_MASK, ConfigurationSearcher.DEFAULT_FILES_MASK);
            var basePackageName = cmd.getOptionValue(ARGS_BASE_PACKAGE_NAME, DEFAULT_OUTPUT_CONFIGS_BASE_PACKAGE);
            var configsPath = convertWinPathToUnixPath(cmd.getOptionValue(ARGS_OUTPUT_DIR, DEFAULT_JAVA_CONFIG_FILES_PATH));
            var createMainConfig = Boolean.parseBoolean(cmd.getOptionValue(ARGS_WRITE_MAIN_CLASS, "true"));
            var xmlfile = cmd.getOptionValue(ARGS_XML_FILE, null);

            var xmlStucture = JAVA_CONFIGS_SAME_STRUCTURE_AS_XMLS;

            log.info("");
            log.info("Start convertation with params:");
            log.info(PRINT_PARAMS_FORMATTER, ARGS_XML_BASE_PATH, paths);
            log.info(PRINT_PARAMS_FORMATTER, ARGS_XML_SEARCH_MASK, fileMask);
            log.info(PRINT_PARAMS_FORMATTER, ARGS_BASE_PACKAGE_NAME, basePackageName);
            log.info(PRINT_PARAMS_FORMATTER, ARGS_OUTPUT_DIR, configsPath);
            log.info(PRINT_PARAMS_FORMATTER, ARGS_XML_FILE, xmlfile);


            log.info("");
            log.info("Search files");
            var configs = new ConfigurationSearcher(
                    Arrays.stream(paths.split(","))
                            .map(String::trim)
                            .map(path -> new File(path).getAbsolutePath())
                            .collect(Collectors.toSet())
                    , fileMask).getConfigurations();
            log.info("Found files count: {}", configs.size());


            var configsMeta = new HashSet<JavaConfigurationMetadata>();

            //we need know about all bean for convert not imported parts
            var allBeans = new XmlConfigurationReader(configs.stream()
                    .map(it -> it.getPath().getAbsolutePath())
                    .collect(Collectors.toSet()))
                    .getBeanFactory();

            if (allBeans.isEmpty()) {
                log.info("Not found any valid configurations. Job done");
                return;
            }

            final Set<ConfigData> convertedConfigs;
            if (xmlfile != null) {
                log.info("Convert one file {}", xmlfile);
                File file = new File(xmlfile);

                if (!file.exists()){
                    log.error("XML file not found {}. Stop", xmlfile);
                    return;
                }

                convertedConfigs = Set.of(ConfigData.builder()
                        .path(file)
                        .sourcePath("")
                        .build());

                //Output classes to base output directory
                xmlStucture = false;
            } else {
                convertedConfigs = configs;
            }

            for (var configData : convertedConfigs) {
                var file = configData.getPath();
                log.info("");
                log.info("Read XML file: {}", file.getAbsolutePath());


                Map<String, BeanDefinition> beanFromFile =
                        Arrays.stream(allBeans.get().getBeanDefinitionNames())
                                .filter(it ->
                                        file.getAbsolutePath().equals(
                                                allBeans.get().getBeanDefinition(it).getResourceDescription()))
                                .collect(Collectors.toMap(
                                                it -> it,
                                                it -> allBeans.get().getBeanDefinition(it)
                                        )
                                );

                if (beanFromFile.isEmpty()) {
                    log.info("Bad XML, skipped");
                    continue;
                }
                var beansData = ConfigurationDataConverter.getInstance()
                        .getConfigurationData(beanFromFile, allBeans.get());

                log.info("Beans converted: {}, skipped: {}",
                        beansData.getConvertedBeansCount(),
                        beansData.getSkippedBeansCount());

                if (beansData.getConvertedBeansCount() == 0) {
                    log.debug("Can not convert any beans in {}. Skip java file generation", file);
                    continue;
                }

                var meta = JavaConfigurationMetadataBuilder.getMetadata(
                        file.getAbsolutePath(),
                        new File(configData.getSourcePath()).getAbsolutePath(),
                        basePackageName,
                        xmlStucture
                );

                JavaConfigurationGenerator generator = new JavaConfigurationGenerator(JavaGeneratorParams.builder()
                        .trueFalseAsBoolean(true)
                        .build());

                log.info("");
                log.info("Generate java config file: path:{} class:{}",
                        new File(configsPath).getAbsolutePath(),
                        meta.getClassName());

                generator.generateClass(
                        meta.getPackageName(),
                        meta.getClassName(),
                        beansData.getBeans(),
                        configsPath);


                log.info("");
                //backup original XML
                log.info("Backup XML file: {}", file);
                Path source = file.toPath();
                Path target = source.resolveSibling(source.getFileName() + ".backup");
                Files.move(source, target);

                String newXmlFile = file.getPath();
                log.info("Write new xml without converted beans: {}", newXmlFile);
                XmlConfigurationWriter writer = new XmlConfigurationWriter();

                writer.writeXmlWithoutConvertedBeans(
                        beansData.getBeans(),
                        target.toFile().getAbsolutePath(),
                        newXmlFile,
                        meta.getFullClassName()
                );
                configsMeta.add(meta);
            }

            if (createMainConfig) {
                log.info("Create main config");
                createMainConfig(basePackageName, configsPath, configsMeta);
            }

            log.info("Done");
        } catch (Exception e) {
            log.error("Convertaion error", e);
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("spring-xml-to-java-converter", options);
        }
    }

    private static void createMainConfig(String basePackageName, String configsPath, HashSet<JavaConfigurationMetadata> configsMeta) {
        if (configsMeta.isEmpty()) {
            log.info("\tConfigs empty. No need main config");
            return;
        }

        if (configsMeta.size() == 1) {
            log.info("\tOnly one config. No need main config");
            return;
        }
        JavaMainConfigurationGenerator.getInstance()
                .createMainConfiguration(
                        basePackageName,
                        "AkvMainConfiguration",
                        configsPath,
                        configsMeta
                );
    }

    private static String convertWinPathToUnixPath(@Nonnull String path) {
        return path.replaceAll("\\\\", "/");
    }

    //hide all reflections warning
    //https://stackoverflow.com/a/61700723/442050
    private static void suppressWarning() throws NoSuchFieldException, InterruptedException, IllegalAccessException {
        Field f = FilterOutputStream.class.getDeclaredField("out");
        Runnable r = () -> {
            f.setAccessible(true);
            synchronized (Convertor.class) {
                Convertor.class.notifyAll();
            }
        };
        Object errorOutput;
        synchronized (Convertor.class) {
            synchronized (System.err) //lock System.err to delay the warning
            {
                new Thread(r).start(); //One of these 2 threads will
                new Thread(r).start(); //hang, the other will succeed.
                Convertor.class.wait(0, 0); //Wait 1st thread to end.
                errorOutput = f.get(System.err); //Field is now accessible, set
                f.set(System.err, null); // it to null to suppress the warning

            } //release System.err to allow 2nd thread to complete.
            Convertor.class.wait(); //Wait 2nd thread to end.
            f.set(System.err, errorOutput); //Restore System.err
        }
    }
}