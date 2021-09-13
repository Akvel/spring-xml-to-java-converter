package pro.akvel.spring.converter.xml;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.TypedStringValue;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class BeanSupportValidator {

    private static final Pattern EXPRESSION_LANGUAGE = Pattern.compile("#\\{.*\\}");

    public boolean isBeanSupport(BeanDefinition beanDefinition, String name) {
        return isBeanSupport(beanDefinition, name, new HashSet<>());
    }

    private boolean isBeanSupport(BeanDefinition beanDefinition, String name, Set<BeanDefinition> guard) {
        if (beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().stream()
                .anyMatch(it -> it.getName() != null)) {
            log.debug("Skipped" + name + ". Convert bean with named constructor parameter no supported");
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
                .anyMatch(it -> it.getValue() instanceof Mergeable)) {
            log.debug("Skipped" + name + ". Convert bean with List, Set, Array, Properties, Map not supported");
            return false;
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof TypedStringValue)
                .map(it -> (TypedStringValue) it.getValue())
                .anyMatch(it -> EXPRESSION_LANGUAGE.matcher("" + it.getValue()).matches())) {
            log.debug("Skipped" + name + ". Expression language in constructor args not supported");
            return false;
        }

        if (beanDefinition.getPropertyValues()
                .stream()
                .filter(it -> it.getValue() instanceof TypedStringValue)
                .map(it -> (TypedStringValue) it.getValue())
                .anyMatch(it -> EXPRESSION_LANGUAGE.matcher("" + it.getValue()).matches())) {
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

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .anyMatch(it -> {
                    Set<BeanDefinition> newGuard = new HashSet<>(guard);
                    newGuard.add(beanDefinition);
                    return !isBeanSupport(it.getBeanDefinition(), it.getBeanName(), newGuard);
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
