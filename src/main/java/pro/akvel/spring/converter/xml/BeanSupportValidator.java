package pro.akvel.spring.converter.xml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import pro.akvel.spring.converter.generator.param.MergeableParam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author akvel
 * @since 12.09.2021
 */
@Slf4j
public class BeanSupportValidator {

    private static final Set<Class> ALLOWED_MERGEABLE_CLASSES = Arrays.stream(MergeableParam.Type.values())
            .map(MergeableParam.Type::getSupportedClasses)
            .collect(Collectors.toSet());

    private static final Pattern EXPRESSION_LANGUAGE = Pattern.compile("classpath:.+|[#]\\{.+\\}");

    public boolean isBeanSupport(BeanDefinition beanDefinition, String name, BeanDefinitionRegistry beanDefinitionRegistry) {
        return isBeanSupport(beanDefinition, name, new HashSet<>(), beanDefinitionRegistry);
    }

    private boolean isBeanSupport(BeanDefinition beanDefinition,
                                  String name,
                                  Set<BeanDefinition> guard,
                                  BeanDefinitionRegistry beanDefinitionRegistry) {
        if (beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().stream()
                .anyMatch(it -> it.getName() != null)) {
            log.debug("Skipped" + name + ". Convert bean with named constructor parameter no supported");
            return false;
        }

        if (beanDefinition.isAbstract()) {
            log.debug("Skipped" + name + ". Abstract beans not supported");
            return false;
        }

        if (beanDefinition.getParentName() != null) {
            log.debug("Skipped" + name + ". Beans with parent not supported");
            return false;
        }

        if ((beanDefinition instanceof AbstractBeanDefinition)
                && ((AbstractBeanDefinition) beanDefinition).getAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE) {
            log.debug("Skipped" + name + ". Beans with AUTOWIRE_BY_TYPE not supported");
            return false;
        }

        if ((beanDefinition instanceof AbstractBeanDefinition)
                && ((AbstractBeanDefinition) beanDefinition).getAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR) {
            log.debug("Skipped" + name + ". Beans with AUTOWIRE_CONSTRUCTOR not supported");
            return false;
        }

        if (!beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().isEmpty()
                && !beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().isEmpty()) {
            log.debug("Skipped" + name + ". Mixed constructor params with \"index\" attr and without not supported");
            return false;
        }

        if (beanDefinition.getFactoryBeanName() != null) {
            log.debug("Skipped" + name + ". Convert bean with factory no supported");
            return false;
        }

        for (int index = 0; index < beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(); index++) {
            if (!beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().containsKey(index)) {
                log.debug("Skipped" + name + ". Missed index " + index);
                return false;
            }
        }

        if (getConstructorParamsStream(beanDefinition)
                .anyMatch(it -> it.getValue() instanceof Mergeable
                        && ALLOWED_MERGEABLE_CLASSES
                        .stream()
                        .noneMatch(itt -> itt.isAssignableFrom(it.getValue().getClass())))) {
            log.debug("Skipped" + name + ". Convert bean with constructor param List, Set, Array, Properties, Map not supported");
            return false;
        }
        if (beanDefinition.getPropertyValues().stream()
                .anyMatch(it -> it.getValue() instanceof Mergeable
                        && ALLOWED_MERGEABLE_CLASSES
                        .stream()
                        .noneMatch(itt -> itt.isAssignableFrom(it.getValue().getClass())))) {
            log.debug("Skipped" + name + ". Convert bean with property List, Set, Array, Properties, Map not supported");
            return false;
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof TypedStringValue)
                .map(it -> (TypedStringValue) it.getValue())
                .anyMatch(it -> EXPRESSION_LANGUAGE.matcher("" + it.getValue()).find())) {
            log.debug("Skipped" + name + ". Expression language in constructor args not supported");
            return false;
        }

        if (beanDefinition.getPropertyValues()
                .stream()
                .filter(it -> it.getValue() instanceof TypedStringValue)
                .map(it -> (TypedStringValue) it.getValue())
                .anyMatch(it -> EXPRESSION_LANGUAGE.matcher("" + it.getValue()).find())) {
            log.debug("Skipped" + name + ". Expression language in property args not supported");
            return false;
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .anyMatch(it -> it.getBeanDefinition().getFactoryBeanName() != null)) {
            log.debug("Skipped" + name + ". Constructor param factory not supported");
            return false;
        }

        if (beanDefinition.getPropertyValues().stream()
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .anyMatch(it -> it.getBeanDefinition().getFactoryBeanName() != null)) {
            log.debug("Skipped" + name + ". Property param factory not supported");
            return false;
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof RuntimeBeanReference)
                .map(it -> (RuntimeBeanReference) it.getValue())
                .anyMatch(it -> {
                    var rez = beanDefinitionRegistry.containsBeanDefinition(it.getBeanName());
                    if (!rez) {
                        log.debug("Bean not found {}", it.getBeanName());
                    }
                    return !rez;
                })) {
            log.debug("Skipped" + name + ". Missed ref in constructor param");
            return false;
        }

        if (beanDefinition.getPropertyValues().stream()
                .filter(it -> it.getValue() instanceof RuntimeBeanReference)
                .map(it -> (RuntimeBeanReference) it.getValue())
                .anyMatch(it -> !beanDefinitionRegistry.containsBeanDefinition(it.getBeanName()))) {
            log.debug("Skipped" + name + ". Missed ref in property param");
            return false;
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .anyMatch(it -> {
                    Set<BeanDefinition> newGuard = new HashSet<>(guard);
                    newGuard.add(beanDefinition);
                    return !isBeanSupport(it.getBeanDefinition(), it.getBeanName(), newGuard, beanDefinitionRegistry);
                })) {
            log.debug("Skipped" + name + ". Found sub bean type that not supported");
            return false;
        }

        return true;
    }

    private Stream<ConstructorArgumentValues.ValueHolder> getConstructorParamsStream(BeanDefinition beanDefinition) {
        return Stream.concat(
                beanDefinition.getConstructorArgumentValues()
                        .getGenericArgumentValues()
                        .stream(),
                beanDefinition.getConstructorArgumentValues()
                        .getIndexedArgumentValues()
                        .values()
                        .stream());
    }
}
