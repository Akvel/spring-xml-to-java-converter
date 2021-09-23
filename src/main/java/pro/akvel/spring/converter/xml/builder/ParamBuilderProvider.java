package pro.akvel.spring.converter.xml.builder;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author akvel
 * @since 12.09.2021
 */
@Slf4j
public class ParamBuilderProvider {
    public static final String PACKAGE_WITH_BUILDERS = "pro.akvel.spring.converter.xml.builder";
    private final Set<ParamBuilder> builders;

    private static ParamBuilderProvider INSTANCE = new ParamBuilderProvider();

    private ParamBuilderProvider() {
        //maybe I am already need Spring? ;)
        Reflections reflections = new Reflections(PACKAGE_WITH_BUILDERS);

        this.builders = reflections.getSubTypesOf(ParamBuilder.class).stream()
                .map(it -> {
                    try {
                        return (ParamBuilder) it.getDeclaredConstructors()[0]
                                .newInstance(null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    @Nonnull
    private ParamBuilder getParamBuilder(@Nonnull ParamBuildContext<?> obj, String beanId) {
        return builders.stream().filter(it -> it.applicable(obj))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            if (list.size() == 0) {
                                throw new IllegalStateException("Builder not found: " + obj.getValue().getClass() + " bean:" + beanId);
                            }
                            if (list.size() > 1) {
                                log.debug("builders: " + list);
                                throw new IllegalStateException("Found more then one builder: " + obj.getValue().getClass() + " bean:" + beanId);
                            }
                            return list.get(0);
                        }
                ));
    }

    public <T> ConstructorParam createConstructorParam(ParamBuildContext<T> value, String beanId) {
        return getParamBuilder(value, beanId).createConstructorParam(value);
    }

    public <T> PropertyParam createPropertyParam(ParamBuildContext<T> context, String beanId) {
        return getParamBuilder(context, beanId).createPropertyParam(context);
    }

    public static ParamBuilderProvider getInstance(){
        return INSTANCE;
    }
}
