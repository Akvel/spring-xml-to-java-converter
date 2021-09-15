package pro.akvel.spring.converter.java;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.akvel.spring.converter.metadata.JavaConfigurationMetadata;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static pro.akvel.spring.converter.java.JavaConfigurationGenerator.cheatJClassImport;

/**
 * Main java configuration class generator
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class JavaMainConfigurationGenerator {
    private final JCodeModel CODE_MODEL = new JCodeModel();

    private static final JavaMainConfigurationGenerator INSTANCE = new JavaMainConfigurationGenerator();

    private JavaMainConfigurationGenerator() {

    }

    @SneakyThrows
    public void createMainConfiguration(String packageName,
                                        String classConfigurationName,
                                        String outputPath,
                                        Set<JavaConfigurationMetadata> configs) {
        if (configs.isEmpty()) {
            log.warn("Skip main config generations. Configs set is empty");
            return;
        }

        // Create a new package
        JPackage jp = CODE_MODEL._package(packageName);

        // Create a new class
        JDefinedClass jc = jp._class(classConfigurationName);

        log.debug("\tCreate config class:{}.{}", packageName, classConfigurationName);

        jc.annotate(Configuration.class);
        jc.javadoc().add("Generated Main Java based configuration");

        var importAnnotation = jc.annotate(Import.class);

        var arrayParam = importAnnotation.paramArray("value");
        configs.forEach(conf -> {
            var clazz = cheatJClassImport(conf.getFullClassName());
            JClass confClass = CODE_MODEL.ref(conf.getFullClassName());
            arrayParam.param(confClass);
        });


        //create all output directories if not exists
        Files.createDirectories(Paths.get(outputPath));
        log.info("\tOutput dir: {}", outputPath);
        CODE_MODEL.build(new File(outputPath), System.err);
    }


    public static JavaMainConfigurationGenerator getInstance() {
        return INSTANCE;
    }
}
