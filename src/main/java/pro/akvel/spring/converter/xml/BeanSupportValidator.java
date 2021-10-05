package pro.akvel.spring.converter.xml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
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
import java.util.Optional;
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

    public Optional<NotSupportBeanType> isBeanSupport(BeanDefinition beanDefinition,
                                                      String name,
                                                      BeanDefinitionRegistry beanDefinitionRegistry,
                                                      boolean subCheck) {
        return isBeanSupport(beanDefinition, name, new HashSet<>(), beanDefinitionRegistry, subCheck);
    }

    private Optional<NotSupportBeanType> isBeanSupport(BeanDefinition beanDefinition,
                                                       String name,
                                                       Set<BeanDefinition> guard,
                                                       BeanDefinitionRegistry beanDefinitionRegistry,
                                                       boolean subCheck) {

        //pass all beans read from Java configurations
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            if (subCheck) {
                return Optional.empty();
            } else {
                log.debug("Skipped annotated bean {}", name);
                return Optional.of(NotSupportBeanType.ANNOTATED_BEAN);
            }
        }

        if (beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().stream()
                .anyMatch(it -> it.getName() != null)) {
            log.debug("Skipped" + name + ". Convert bean with named constructor parameter no supported");
            return Optional.of(NotSupportBeanType.CONSTRUCTOR_WITH_NAME);
        }

        if (beanDefinition.isAbstract()) {
            log.debug("Skipped" + name + ". Abstract beans not supported");
            return Optional.of(NotSupportBeanType.ABSTRACT_BEAN);
        }

        if (beanDefinition.getParentName() != null) {
            log.debug("Skipped {}. Beans with parent not supported", name);
            return Optional.of(NotSupportBeanType.BEAN_WITH_PARENT);
        }

        if ((beanDefinition instanceof AbstractBeanDefinition)
                && ((AbstractBeanDefinition) beanDefinition).getAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE) {
            log.debug("Skipped {}. Beans with AUTOWIRE_BY_TYPE not supported {}", name,
                    ((AbstractBeanDefinition) beanDefinition).getAutowireMode());
            return Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPORTED_AUTOWIRE_BY_TYPE);
        }

        if ((beanDefinition instanceof AbstractBeanDefinition)
                && ((AbstractBeanDefinition) beanDefinition).getAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR) {
            log.debug("Skipped {}. Beans with AUTOWIRE_CONSTRUCTOR not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPORTED_AUTOWIRE_CONSTRUCTOR);
        }

        if (!beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().isEmpty()
                && !beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().isEmpty()) {
            log.debug("Skipped {}. Mixed constructor params with \"index\" attr and without not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_MIXED_CONSTRUCTO_PARAM);
        }

        if (isBeanHashFactory(beanDefinition)) {
            log.debug("Skipped {}. Convert bean with factory no supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY);
        }

        for (int index = 0; index < beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().size(); index++) {
            if (!beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().containsKey(index)) {
                log.debug("Skipped {}. Missed index {}", name, index);
                return Optional.of(NotSupportBeanType.BEAT_WITH_MISSED_CONSTRUCTOR_INDEX);
            }
        }

        if (getConstructorParamsStream(beanDefinition)
                .anyMatch(it -> isMergeableTypeAllowed(it.getValue()))) {
            log.debug("Skipped {}. Convert bean with constructor param Array, Properties, Map not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPOTED_MERGEABLE_TYPE);
        }
        if (beanDefinition.getPropertyValues().stream()
                .anyMatch(it -> isMergeableTypeAllowed(it.getValue()))) {
            log.debug("Skipped {}. Convert bean with property Array, Properties, Map not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPOTED_MERGEABLE_TYPE);
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof TypedStringValue)
                .map(it -> (TypedStringValue) it.getValue())
                .anyMatch(this::isContainEL)) {
            log.debug("Skipped {}. Expression language in constructor args not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_EL);
        }

        if (beanDefinition.getPropertyValues()
                .stream()
                .filter(it -> it.getValue() instanceof TypedStringValue)
                .map(it -> (TypedStringValue) it.getValue())
                .anyMatch(this::isContainEL)) {
            log.debug("Skipped {}. Expression language in property args not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_EL);
        }

        if (getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .anyMatch(it -> isBeanHashFactory(it.getBeanDefinition()))) {
            log.debug("Skipped {}. Constructor param factory not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY);
        }

        if (beanDefinition.getPropertyValues().stream()
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .anyMatch(it -> isBeanHashFactory(it.getBeanDefinition()))) {
            log.debug("Skipped {}. Property param factory not supported", name);
            return Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY);
        }

        var checkConstructorRefs = getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof RuntimeBeanReference)
                .map(it -> (RuntimeBeanReference) it.getValue())
                .map(it -> isRefBeanValid(beanDefinitionRegistry, it))
                .filter(Optional::isPresent)
                .findAny();
        if (checkConstructorRefs.isPresent()) {
            log.debug("Skipped {}. Bad ref in constructor param - {}", name, checkConstructorRefs.get());
            return checkConstructorRefs.get();
        }

        var checkPropertyRefs = beanDefinition.getPropertyValues().stream()
                .filter(it -> it.getValue() instanceof RuntimeBeanReference)
                .map(it -> (RuntimeBeanReference) it.getValue())
                .map(it -> isRefBeanValid(beanDefinitionRegistry, it))
                .filter(Optional::isPresent)
                .findAny();
        if (checkPropertyRefs.isPresent()) {
            log.debug("Skipped {}. Bad ref in property params {}", name, checkPropertyRefs.get());
            return checkPropertyRefs.get();
        }

        var checkBeanDefinition = getConstructorParamsStream(beanDefinition)
                .filter(it -> it.getValue() instanceof BeanDefinitionHolder)
                .map(it -> (BeanDefinitionHolder) it.getValue())
                .map(it -> {
                    Set<BeanDefinition> newGuard = new HashSet<>(guard);
                    newGuard.add(beanDefinition);
                    return isBeanSupport(it.getBeanDefinition(), it.getBeanName(), newGuard, beanDefinitionRegistry, true) ;
                })
                .filter(Optional::isPresent)
                .findAny();
        ;
        if (checkBeanDefinition.isPresent()) {
            log.debug("Skipped {}. Found sub bean type that not supported - {}", name, checkBeanDefinition.get());
            return checkBeanDefinition.get();
        }

        return Optional.empty();
    }

    private boolean isMergeableTypeAllowed(Object value) {
        return value instanceof Mergeable
                && ALLOWED_MERGEABLE_CLASSES
                .stream()
                .noneMatch(itt -> itt.isAssignableFrom(value.getClass()));
    }

    private boolean isContainEL(TypedStringValue it) {
        return EXPRESSION_LANGUAGE.matcher("" + it.getValue()).find();
    }

    private boolean isBeanHashFactory(BeanDefinition beanDefinition) {
        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            return false;
        }

        if (beanDefinition.getFactoryBeanName() != null) {
            log.debug("Bean with factory bean");
            return true;
        }

        if (beanDefinition.getFactoryMethodName() != null &&
                !beanDefinition.getPropertyValues().isEmpty()) {
            log.debug("Bean with factory method and properties not supported");
            return true;
        }

        return false;
    }

    private Optional<NotSupportBeanType> isRefBeanValid(BeanDefinitionRegistry beanDefinitionRegistry,
                                                        RuntimeBeanReference it) {
        var rez = beanDefinitionRegistry.containsBeanDefinition(it.getBeanName());
        if (!rez) {
            log.debug("Bean not found {}", it.getBeanName());
            return Optional.of(NotSupportBeanType.BEAT_WITH_MISSED_REF);
        }
        //we can not autowire bean if we don't know bean Class
        if (isBeanHashFactory(beanDefinitionRegistry.getBeanDefinition(it.getBeanName()))) {
            log.debug("Bean by ref is bean with factory {}", it.getBeanName());
            return Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY);
        }

        return Optional.empty();


      /*  var beanDef = beanDefinitionRegistry.getBeanDefinition(it.getBeanName());
        Set<BeanDefinition> newGuard = new HashSet<>(guard);
        newGuard.add(beanDefinition);
        return deepCheck ? isBeanSupport(beanDef, it.getBeanName(), newGuard, beanDefinitionRegistry, true, false)
                : Optional.empty();*/
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


    public enum NotSupportBeanType {
        CONSTRUCTOR_WITH_NAME,
        ABSTRACT_BEAN, BEAN_WITH_PARENT,
        BEAT_WITH_NOT_SUPPORTED_AUTOWIRE_BY_TYPE,
        BEAT_WITH_NOT_SUPPORTED_AUTOWIRE_CONSTRUCTOR,
        BEAT_WITH_MIXED_CONSTRUCTO_PARAM,
        BEAT_WITH_FACTORY,
        BEAT_WITH_MISSED_CONSTRUCTOR_INDEX,
        BEAT_WITH_NOT_SUPPOTED_MERGEABLE_TYPE,
        BEAT_WITH_EL,
        BEAT_WITH_MISSED_REF,
        ANNOTATED_BEAN;
    }
}
