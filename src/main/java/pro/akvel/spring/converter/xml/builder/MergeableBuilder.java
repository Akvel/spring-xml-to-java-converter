package pro.akvel.spring.converter.xml.builder;

import lombok.NonNull;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedSet;
import pro.akvel.spring.converter.generator.param.ConstructorMergeableParam;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.MergeableParam;
import pro.akvel.spring.converter.generator.param.PropertyMergeableParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MergeableBuilder implements ParamBuilder<Mergeable> {
    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<Mergeable> context) {
        if (context.getValue() instanceof ManagedList) {
            var list = (ManagedList) context.getValue();
            return ConstructorMergeableParam.builder()
                    .type(MergeableParam.Type.LIST)
                    .values((List<PropertyParam>) list
                            .stream()
                            .map(it -> createParamContext(context, it))
                            .collect(Collectors.toList()))
                    .build();
        } else if (context.getValue() instanceof ManagedSet) {
            return ConstructorMergeableParam.builder()
                    .type(MergeableParam.Type.SET)
                    .values((Set<PropertyParam>) ((ManagedSet) context.getValue())
                            .stream()
                            .map(it -> createParamContext(context, it)
                            )
                            .collect(Collectors.toSet()))
                    .build();
        } else {
            throw new IllegalArgumentException("Unsupported type" + context.getValue().getClass());
        }
    }

    @NonNull
    private static PropertyParam createParamContext(@NonNull ParamBuildContext<Mergeable> context,
                                                    @NonNull Object it) {
        return ParamBuilderProvider.getInstance().createPropertyParam(
                new ParamBuildContext(
                        it,
                        null,
                        context.getBeanDefinitionRegistry(),
                        "add", //list.add()
                        null),
                context.getFieldName()
        );
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<Mergeable> context) {
        if (context.getValue() instanceof ManagedList) {
            return PropertyMergeableParam.builder()
                    .name(context.getFieldName())
                    .type(MergeableParam.Type.LIST)
                    .values((List<PropertyParam>) ((ManagedList) context.getValue())
                            .stream()
                            .map(it -> createParamContext(context, it)

                            )
                            .collect(Collectors.toList()))
                    .build();
        } else if (context.getValue() instanceof ManagedSet) {
            return PropertyMergeableParam.builder()
                    .name(context.getFieldName())
                    .type(MergeableParam.Type.SET)
                    .values((Set<PropertyParam>) ((ManagedSet) context.getValue())
                            .stream()
                            .map(it -> createParamContext(context, it)
                            )
                            .collect(Collectors.toSet()))
                    .build();
        } else {
            throw new IllegalArgumentException("Unsupported type" + context.getValue().getClass());
        }
    }
}
