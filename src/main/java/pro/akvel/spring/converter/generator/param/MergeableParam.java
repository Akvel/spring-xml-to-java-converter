package pro.akvel.spring.converter.generator.param;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Implement holder for
 * <p>
 * {@link org.springframework.beans.factory.support.ManagedList}
 * {@link org.springframework.beans.factory.support.ManagedSet}
 * {@link org.springframework.beans.factory.support.ManagedProperties}
 *
 * @param <T>
 */
public interface MergeableParam<T extends Param> extends Param {


    @NonNull
    Type getType();

    @NonNull
    Collection<T> getValues();

    enum Type {
        LIST(List.class, ArrayList.class),
        /**
         * HashSet()
         */
        SET(Set.class, HashSet.class),
        /**
         * Properties()
         */
        //PROPERTIES(Properties.class, Properties.class)
        ;

        private final Class supportedClasses;

        private final Class outputClass;

        Type(Class clazz, Class outputClass) {
            this.supportedClasses = clazz;
            this.outputClass = outputClass;
        }

        public Class getSupportedClasses() {
            return supportedClasses;
        }

        public Class getOutputClass() {
            return outputClass;
        }
    }
}
