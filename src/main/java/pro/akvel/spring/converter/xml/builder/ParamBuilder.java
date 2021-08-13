package pro.akvel.spring.converter.xml.builder;

import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface ParamBuilder<TClazz> {

    ConstructorParam createConstructorParam(ParamBuildContext<TClazz> context);

    PropertyParam createPropertyParam(ParamBuildContext<TClazz> context);

    default boolean applicable(ParamBuildContext<TClazz> context) {
        //FIXME покрасивее можно?
        Class supportedClass = (Class) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

        return supportedClass.isAssignableFrom(context.getValue().getClass());
    }
}
