package pro.akvel.spring.converter;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java configuration class generator
 *
 * @author akvel
 * @since 12.08.2020
 */
public class JavaConfigurationGenerator {


    private static final JCodeModel CODE_MODEL = new JCodeModel();

    void generateClass(String packageName,
                       String classConfigurationName,
                       List<BeanData> beans,
                       String outputPath) throws JClassAlreadyExistsException, IOException {
        // Instantiate a new JCodeModel

        // Create a new package
        JPackage jp = CODE_MODEL._package(packageName);

        // Create a new class
        JDefinedClass jc = jp._class(classConfigurationName);

        jc.annotate(Configuration.class);
        jc.javadoc().add("Generated Java based configuration");

        // Add get beans
        beans.forEach(it -> {
            JClass beanClass = CODE_MODEL.ref(it.getClazzName());

            JMethod method = jc.method(JMod.PUBLIC,
                    CODE_MODEL.ref(it.getClazzName()), getMethodName(it.getClazzName(), it.getId()));

            addBeanAnnotation(method, it.getId());

            addMethodParams(it, method);


            JBlock body = method.body();

            if (canReturnNewObject(it)) {
                JInvocation aNew = JExpr._new(beanClass);
                addParamToBeanConstructor(it, method, aNew);
                body._return(aNew);
            } else {
                JVar newBean = body.decl(beanClass, "bean", JExpr._new(beanClass));
                setProperties(newBean, getPropertyParams(it), method);
                body._return(newBean);
            }
        });

        CODE_MODEL.build(new File(outputPath));
    }

    private List<PropertyParam> getPropertyParams(BeanData it) {
        return it.getConstructorParams().stream()
                .filter(itt -> itt instanceof PropertyParam)
                .map(itt -> (PropertyParam) itt)
                .collect(Collectors.toList());
    }

    private void setProperties(JVar newBean, List<PropertyParam> propertyParams, JMethod method) {
        propertyParams.forEach(it -> {
            if (it instanceof PropertyBeanParam) {
                PropertyBeanParam beanParam = (PropertyBeanParam) it;

                JInvocation invocation = method.body().invoke(newBean, getSetterName(beanParam.getName()));
                invocation.arg(method.params().stream()
                        .filter(itt -> itt.name().equals(beanParam.getRef()))
                        .findFirst().orElseThrow());
            }

            if (it instanceof PropertyValueParam) {
                PropertyValueParam valueParam = (PropertyValueParam) it;
                JInvocation invocation = method.body().invoke(newBean, getSetterName(valueParam.getName()));
                invocation.arg(JExpr.lit(valueParam.getValue()));
            }
        });
    }

    @NonNull
    private String getSetterName(String fieldName) {
        return "set" + StringUtils.capitalize(fieldName);
    }

    private boolean canReturnNewObject(BeanData beanData) {
        return beanData.getConstructorParams().stream()
                .noneMatch(it -> it instanceof PropertyBeanParam);
    }

    private void addParamToBeanConstructor(BeanData it, JMethod method, JInvocation aNew) {
        it.getConstructorParams().forEach(arg -> {

            if (arg instanceof ConstructorBeanParam) {
                ConstructorBeanParam beanParam = (ConstructorBeanParam) arg;

                aNew.arg(method.params().stream()
                        .filter(itt -> itt.name().equals(beanParam.getRef()))
                        .findFirst().orElseThrow());
            }

            if (arg instanceof ConstructorNullParam) {
                aNew.arg(JExpr._null());
            }

            if (arg instanceof ConstructorSubBeanParam) {
                ConstructorSubBeanParam subBeanData = (ConstructorSubBeanParam) arg;

                JClass subBeanClass = CODE_MODEL.ref(subBeanData.getBeanData().getClazzName());
                JInvocation subBeanNew = JExpr._new(subBeanClass);
                if (canReturnNewObject(subBeanData.getBeanData())) {
                    addParamToBeanConstructor(subBeanData.getBeanData(), method, subBeanNew);
                    aNew.arg(subBeanNew);
                } else {
                    JVar newBeanVar = method.body().decl(subBeanClass, "bean", subBeanNew);
                    setProperties(newBeanVar, getPropertyParams(subBeanData.getBeanData()), method);
                    addParamToBeanConstructor(subBeanData.getBeanData(), method, subBeanNew);
                    aNew.arg(newBeanVar);
                }
            }

            if (arg instanceof ConstructorConstantParam) {
                ConstructorConstantParam constant = (ConstructorConstantParam) arg;

                if (Integer.class.getName().equals(constant.getType())) {
                    aNew.arg(JExpr.lit(Integer.parseInt(constant.getValue())));
                } else if (Long.class.getName().equals(constant.getType())) {
                    aNew.arg(JExpr.lit(Long.parseLong(constant.getValue())));
                } else {
                    aNew.arg(JExpr.lit(constant.getValue()));
                }
            }
        });
    }

    private void addMethodParams(BeanData beanData, JMethod method) {
        beanData.getConstructorParams().stream()
                .filter(it -> it instanceof ConstructorBeanParam)
                .map(it -> (ConstructorBeanParam) it)
                .forEach(arg -> {
                    JVar param = method.param(CODE_MODEL.ref(arg.getClassName()), arg.getRef());
                    if (arg.getRef() != null) {
                        param.annotate(Qualifier.class).param("value", arg.getRef());
                    }
                });

        beanData.getConstructorParams().stream()
                .filter(it -> it instanceof PropertyBeanParam)
                .map(it -> (PropertyBeanParam) it)
                .forEach(arg -> {
                    JVar param = method.param(CODE_MODEL.ref(arg.getClassName()), arg.getRef());
                    if (arg.getRef() != null) {
                        param.annotate(Qualifier.class).param("value", arg.getRef());
                    }
                });

        //add all refs from subBeans
        beanData.getConstructorParams().stream()
                .filter(it -> it instanceof ConstructorSubBeanParam)
                .map(it -> (ConstructorSubBeanParam) it)
                .forEach(it ->
                        addMethodParams(it.getBeanData(), method)
                );
    }

    private String getMethodName(@Nonnull String className, @Nullable String id) {
        if (id != null) {
            return id;
        }

        //pro.akvel.test.TestBean -> TestBean
        String methodName = className.substring(className.lastIndexOf(".") + 1);

        //TestBean -> testBean
        methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

        return methodName;
    }


    private static void addBeanAnnotation(@Nonnull JMethod method, @Nullable String id) {
        JAnnotationUse beanAnnotation = method.annotate(Bean.class);

        if (id != null) {
            beanAnnotation.param("value", id);
        }
    }


}
