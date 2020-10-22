package pro.akvel.spring.converter.generator;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import pro.akvel.spring.converter.generator.param.Param;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * FIXME
 *
 * @author akvel
 * @since 12.08.2020
 */
@Data
@Builder
public class BeanData {

    //FIXME support scopes

    /**
     * Bean id ({@link Bean#name()}
     */
    @Nullable
    String id;

    /**
     * Full class name, with package     */
    @Nonnull
    String clazzName;

    /**
     * Bean constructor params with same order as in xml configuration
     */
    @Nonnull
    List<Param> constructorParams;

    /**
     * Name method for {@link Bean#initMethod()}
     */
    final String initMethodName;

    /**
     * Name method for {@link Bean#destroyMethod()}
     */
    final String destroyMethodName;
}
