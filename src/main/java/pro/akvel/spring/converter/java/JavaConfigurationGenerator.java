package pro.akvel.spring.converter.java;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.param.ConstructIndexParam;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.generator.param.PropertySubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Java configuration class generator
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class JavaConfigurationGenerator {

    private static final JCodeModel CODE_MODEL = new JCodeModel();


    public void generateClass(String packageName,
                              String classConfigurationName,
                              BeanData beanData,
                              String outputPath) {
        generateClass(packageName, classConfigurationName, Set.of(beanData), outputPath);
    }

    @SneakyThrows
    public void generateClass(String packageName,
                              String classConfigurationName,
                              Set<BeanData> beansData,
                              String outputPath) {
        // Create a new package
        JPackage jp = CODE_MODEL._package(packageName);

        // Create a new class
        JDefinedClass jc = jp._class(classConfigurationName);

        log.debug("\tCreate config class:{}.{}", packageName, classConfigurationName);

        jc.annotate(Configuration.class);
        jc.javadoc().add("Generated Java based configuration");

        // Add get beans
        beansData.forEach(it -> {
            //JClass only add import if class exists in class path
            cheatJClassImport(it.getClassName());

            log.debug("\tAdd bean to java config - id:{} class:{}", it.getId(), it.getClassName());

            JClass beanClass = CODE_MODEL.ref(it.getClassName());

            JMethod method = jc.method(JMod.PUBLIC,
                    CODE_MODEL.ref(it.getClassName()), getMethodName(it.getClassName(), it.getId()));

            addBeanAnnotation(method, it);

            if (it.getQualifierName() != null) {
                it.getQualifierName().forEach(itt -> {
                    //add all for show all in class
                    JAnnotationUse scopeAnnotation = method.annotate(Qualifier.class);
                    scopeAnnotation.param("value", itt);
                });
            }

            if (it.isLazyInit()) {
                JAnnotationUse scopeAnnotation = method.annotate(Lazy.class);
            }

            if (!it.getScope().isBlank()) {
                JAnnotationUse scopeAnnotation = method.annotate(Scope.class);
                scopeAnnotation.param("value", it.getScope());
            }

            if (it.getDependsOn() != null) {
                JAnnotationUse scopeAnnotation = method.annotate(DependsOn.class);
                var arrayParam = scopeAnnotation.paramArray("value");
                Arrays.stream(it.getDependsOn()).forEach(arrayParam::param);
            }

            if (it.getDescription() != null) {
                method.javadoc().add(it.getDescription());
            }

            if (it.isPrimary()) {
                method.annotate(Primary.class);
            }

            addMethodParams(it, method);


            JBlock body = method.body();

            if (constructorParamsOnly(it)) {
                JInvocation aNew = JExpr._new(beanClass);
                addParamToBeanConstructor(it, method, aNew);
                body._return(aNew);
            } else {
                JVar newBean = body.decl(beanClass, "bean", JExpr._new(beanClass));
                setProperties(newBean, it.getPropertyParams(), method);
                body._return(newBean);
            }
        });

        //create all output directories if not exists
        Files.createDirectories(Paths.get(outputPath));
        log.info("\tOutput dir: {}", outputPath);
        CODE_MODEL.build(new File(outputPath), System.err);
    }

    @SneakyThrows
    static Class<?> cheatJClassImport(String className) {
        //create fake classes and add they to classLoader
        //after that JClass find it and make correct imports
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass cc = pool.makeClass(className);
            return pool.toClass(cc);
        } catch (Exception e) {
            log.debug("Add class stub error: {}", className, e);
            return Class.forName(className);
        }
    }

    private static boolean constructorParamsOnly(BeanData it) {
        return it.getPropertyParams().isEmpty();
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

            if (it instanceof PropertySubBeanParam){
                PropertySubBeanParam subBeanData = (PropertySubBeanParam) it;

                JClass subBeanClass = CODE_MODEL.ref(subBeanData.getBeanData().getClassName());
                JInvocation subBeanNew = JExpr._new(subBeanClass);
                if (constructorParamsOnly(subBeanData.getBeanData())) {
                    addParamToBeanConstructor(subBeanData.getBeanData(), method, subBeanNew);

                    JInvocation invocation = method.body().invoke(newBean, getSetterName(subBeanData.getName()));
                    invocation.arg(subBeanNew);
                } else {
                    JVar newBeanVar = method.body().decl(subBeanClass, "bean", subBeanNew);
                    setProperties(newBeanVar, subBeanData.getBeanData().getPropertyParams(), method);
                    addParamToBeanConstructor(subBeanData.getBeanData(), method, subBeanNew);

                    JInvocation invocation = method.body().invoke(newBean, getSetterName(subBeanData.getName()));
                    invocation.arg(subBeanNew);
                }


            }
        });
    }

    @NonNull
    private String getSetterName(String fieldName) {
        return "set" + StringUtils.capitalize(fieldName);
    }


    private void addParamToBeanConstructor(BeanData it, JMethod method, JInvocation aNew) {
        it.getConstructorParams()
                .stream()
                .filter(itt -> itt instanceof ConstructIndexParam)
                .map(itt -> (ConstructIndexParam) itt)
                .sorted(Comparator.comparingInt(v ->
                        Optional.ofNullable(v.getIndex()).orElse(Integer.MAX_VALUE)))
                .forEach(arg -> {
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

                        JClass subBeanClass = CODE_MODEL.ref(subBeanData.getBeanData().getClassName());
                        JInvocation subBeanNew = JExpr._new(subBeanClass);
                        if (constructorParamsOnly(subBeanData.getBeanData())) {
                            addParamToBeanConstructor(subBeanData.getBeanData(), method, subBeanNew);
                            aNew.arg(subBeanNew);
                        } else {
                            JVar newBeanVar = method.body().decl(subBeanClass, "bean", subBeanNew);
                            setProperties(newBeanVar, subBeanData.getBeanData().getPropertyParams(), method);
                            addParamToBeanConstructor(subBeanData.getBeanData(), method, subBeanNew);
                            aNew.arg(newBeanVar);
                        }
                    }

                    if (arg instanceof ConstructorConstantParam) {
                        ConstructorConstantParam constant = (ConstructorConstantParam) arg;

                        if (Integer.class.getName().equals(constant.getType())
                                || constant.getType().equals("int")) {
                            aNew.arg(JExpr.lit(Integer.parseInt(constant.getValue())));
                        } else if (Long.class.getName().equals(constant.getType())
                                || constant.getType().equals("long")) {
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

        //add all refs from subBeans
        beanData.getConstructorParams().stream()
                .filter(it -> it instanceof ConstructorSubBeanParam)
                .map(it -> (ConstructorSubBeanParam) it)
                .forEach(it ->
                        addMethodParams(it.getBeanData(), method)
                );


        beanData.getPropertyParams().stream()
                .filter(it -> it instanceof PropertyBeanParam)
                .map(it -> (PropertyBeanParam) it)
                .forEach(arg -> {
                    JVar param = method.param(CODE_MODEL.ref(arg.getClassName()), arg.getRef());
                    if (arg.getRef() != null) {
                        param.annotate(Qualifier.class).param("value", arg.getRef());
                    }
                });


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


    private static void addBeanAnnotation(@Nonnull JMethod method, @Nonnull BeanData beanData) {
        JAnnotationUse beanAnnotation = method.annotate(Bean.class);

        if (beanData.getId() != null) {
            if (beanData.getInitMethodName() == null && beanData.getDestroyMethodName() == null) {
                beanAnnotation.param("value", beanData.getId());
            } else {
                beanAnnotation.param("name", beanData.getId());
            }
        }

        if (beanData.getInitMethodName() != null) {
            beanAnnotation.param("initMethod", beanData.getInitMethodName());
        }

        if (beanData.getDestroyMethodName() != null) {
            beanAnnotation.param("destroyMethod", beanData.getDestroyMethodName());
        }


    }


}
