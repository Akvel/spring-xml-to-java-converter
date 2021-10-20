package pro.akvel.spring.converter.java;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
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
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import pro.akvel.spring.converter.generator.param.ConstructorMergeableParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.MergeableParam;
import pro.akvel.spring.converter.generator.param.Param;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyMergeableParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.generator.param.PropertySubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java configuration class generator
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class JavaConfigurationGenerator {

    public static final String PLACEHOLDER_VARIABLE = "${}";

    private static int fieldIndex = 0;
    private final JavaGeneratorParams params;

    public JavaConfigurationGenerator(JavaGeneratorParams params) {
        this.params = params;
    }


    public void generateClass(String packageName,
                              String classConfigurationName,
                              BeanData beanData,
                              String outputPath) {
        generateClass(packageName, classConfigurationName,
                Set.of(beanData),
                outputPath);
    }

    @SneakyThrows
    public void generateClass(String packageName,
                              String classConfigurationName,
                              Set<BeanData> beansData,
                              String outputPath) {
        JCodeModel codeModel = new JCodeModel();

        // Create a new package
        JPackage jp = codeModel._package(packageName);

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

            JClass beanClass = codeModel.ref(it.getClassName());

            JMethod method = jc.method(JMod.PUBLIC,
                    codeModel.ref(it.getClassName()), getMethodName(it.getClassName(), it.getId()));

            //reset for each method
            fieldIndex = 0;

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

            addMethodParams(it, method, new HashSet<>(), codeModel);

            JBlock body = method.body();


            final JInvocation aNew;
            aNew = getInvocation(it, beanClass);
            addParamToBeanConstructor(it, method, aNew, jc, codeModel);
            if (constructorParamsOnly(it)) {
                body._return(aNew);
            } else {
                JVar newBean = body.decl(beanClass, "bean", aNew);
                setProperties(newBean, it.getPropertyParams(), method, true, jc, codeModel);
                body._return(newBean);
            }
        });

        //create all output directories if not exists
        Files.createDirectories(Paths.get(outputPath));
        log.info("\tOutput dir: {}", outputPath);
        codeModel.build(new File(outputPath), System.err);
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
            log.debug("Add class stub error: {} {}", className, e.getMessage());
            log.trace("Add class stub exception", e);
            return Class.forName(className);
        }
    }

    private static boolean constructorParamsOnly(BeanData beanData) {
        return beanData.getPropertyParams().isEmpty() &&
                beanData.getConstructorParams()
                        .stream()
                        .noneMatch(it -> it instanceof MergeableParam);
    }

    private void setProperties(JVar newBean,
                               List<PropertyParam> propertyParams,
                               JMethod method,
                               boolean setter,
                               JDefinedClass classDescription,
                               JCodeModel codeModel) {
        propertyParams.forEach(it -> {
            if (it instanceof PropertyBeanParam) {
                PropertyBeanParam beanParam = (PropertyBeanParam) it;

                JInvocation invocation = method.body().invoke(newBean, getSetterName(beanParam.getName(), setter));
                invocation.arg(method.params().stream()
                        .filter(itt -> itt.name().equals(getJavaValidName(beanParam.getRef())))
                        .findFirst().orElseThrow(() -> new IllegalStateException("Bean method param not found: " + getJavaValidName(beanParam.getRef()))));
            } else if (it instanceof PropertyValueParam) {
                PropertyValueParam valueParam = (PropertyValueParam) it;
                JInvocation invocation = method.body().invoke(newBean, getSetterName(valueParam.getName(), setter));

                if (isTrueFalseAsBoolean(valueParam.getValue())) {
                    invocation.arg(JExpr.lit(Boolean.parseBoolean(valueParam.getValue())));
                } else {
                    invocation.arg(createValue(valueParam.getValue(), classDescription));
                }
            } else if (it instanceof PropertySubBeanParam) {
                PropertySubBeanParam subBeanDataParam = (PropertySubBeanParam) it;
                BeanData subBeanData = subBeanDataParam.getBeanData();

                JClass subBeanClass = codeModel.ref(subBeanData.getClassName());

                final JInvocation subBeanNew = getInvocation(subBeanData, subBeanClass);

                if (constructorParamsOnly(subBeanData)) {
                    addParamToBeanConstructor(subBeanData, method, subBeanNew, classDescription, codeModel);

                    JInvocation invocation = method.body().invoke(newBean, getSetterName(subBeanDataParam.getName(), setter));
                    invocation.arg(subBeanNew);
                } else {
                    JVar newBeanVar = method.body().decl(subBeanClass, "bean" + fieldIndex++, subBeanNew);
                    setProperties(newBeanVar, subBeanData.getPropertyParams(), method, true, classDescription, codeModel);
                    addParamToBeanConstructor(subBeanData, method, subBeanNew, classDescription, codeModel);

                    JInvocation invocation = method.body().invoke(newBean, getSetterName(subBeanDataParam.getName(), setter));
                    invocation.arg(newBeanVar);
                }
            } else if (it instanceof PropertyMergeableParam) {
                PropertyMergeableParam listParam = (PropertyMergeableParam) it;

                JClass listClass = codeModel.ref(listParam.getType().getOutputClass());
                JInvocation list = JExpr._new(listClass);
                JVar listVar = method.body().decl(listClass, listParam.getType().name().toLowerCase() + fieldIndex++, list);

                listParam.getValues().forEach(itt -> {
                    setProperties(listVar, List.of(itt), method, false, classDescription, codeModel);
                });

                JInvocation invocation = method.body().invoke(newBean, getSetterName(listParam.getName(), setter));
                invocation.arg(listVar);
            } else {
                throw new IllegalArgumentException("Unknown property type " + it.getClass());
            }
        });
    }

    private static JExpression createValue(String value, JDefinedClass clazz) {
        var pattern = Pattern.compile("(\\$\\{(.+?)\\})");

        Matcher matcher = pattern.matcher(value);

        var result = value;
        Map<Integer, String> fields = new HashMap<>();
        int index = 0;

        while (matcher.find()) {
            //${value}
            var placeholder = matcher.group(1);
            //value
            var placeHolderValue = matcher.group(2);

            log.debug("Found placeholder {} = {}", placeholder, placeHolderValue);

            String fieldName = CaseUtils.toCamelCase(placeHolderValue, false, '.', '-');
            if (!clazz.fields().containsKey(fieldName)) {
                var field = clazz.field(JMod.PRIVATE, String.class, fieldName);
                var annotationValue = field.annotate(Value.class);
                annotationValue.param("value", placeholder);
                log.trace("Add field {}", fieldName);
            }
            fields.put(index++, fieldName);

            result = result.replace(placeholder, PLACEHOLDER_VARIABLE);
        }

        if (!fields.isEmpty()) {
            JExpression resultExpression = null;
            String[] split = result.split("\\$\\{\\}");

            if (result.equals(PLACEHOLDER_VARIABLE)) {
                if (fields.size() != 1) {
                    throw new IllegalStateException("Placeholder filed not one value " + value);
                }

                return JExpr.ref(fields.get(0));
            }

            //replace placeholders in row
            for (int i = 0, splitLength = split.length; i < splitLength; i++) {
                String s = split[i];
                if (!s.isEmpty()) {
                    if (resultExpression == null) {
                        resultExpression = JExpr.lit(s);
                    } else {
                        resultExpression = resultExpression.plus(JExpr.lit(s));
                    }
                }

                String field = fields.get(i);
                if (field != null) {
                    log.trace("Get {} {}", i, field);
                    if (resultExpression != null) {
                        resultExpression = resultExpression.plus(JExpr.ref(field));
                    } else {
                        resultExpression = JExpr.ref(field);
                    }
                }
            }

            return resultExpression;
        } else {
            return JExpr.lit(value);
        }
    }

    private boolean isTrueFalseAsBoolean(String value) {
        return params.isTrueFalseAsBoolean() &&
                ("false".equals(value) || "true".equals(value));
    }

    @NonNull
    private String getSetterName(String fieldName, boolean setter) {
        return setter ? "set" + StringUtils.capitalize(fieldName) : fieldName;
    }


    private void addParamToBeanConstructor(BeanData it, JMethod method,
                                           JInvocation aNew,
                                           JDefinedClass classDescription,
                                           JCodeModel codeModel) {
        it.getConstructorParams()
                .stream()
                .filter(itt -> itt instanceof ConstructIndexParam)
                .map(itt -> (ConstructIndexParam) itt)
                .sorted(Comparator.comparingInt(v ->
                        Optional.ofNullable(v.getIndex()).orElse(Integer.MAX_VALUE)))
                .forEach(arg -> {
                    processConstructor(method, aNew, arg, classDescription, codeModel);
                });
    }

    private void processConstructor(JMethod method,
                                    JInvocation aNew,
                                    ConstructIndexParam arg,
                                    JDefinedClass classDescription,
                                    JCodeModel codeModel) {
        if (arg instanceof ConstructorBeanParam) {
            ConstructorBeanParam beanParam = (ConstructorBeanParam) arg;

            aNew.arg(method.params().stream()
                    .filter(itt -> itt.name().equals(getJavaValidName(beanParam.getRef())))
                    .findFirst().orElseThrow(() -> new IllegalStateException("Bean ref not found " + getJavaValidName(beanParam.getRef()))));
        }

        if (arg instanceof ConstructorNullParam) {
            aNew.arg(JExpr._null());
        }

        if (arg instanceof ConstructorSubBeanParam) {
            ConstructorSubBeanParam subBeanDataParam = (ConstructorSubBeanParam) arg;
            BeanData subBeanData = subBeanDataParam.getBeanData();

            JClass subBeanClass = codeModel.ref(subBeanData.getClassName());

            final JInvocation subBeanNew;
            subBeanNew = getInvocation(subBeanData, subBeanClass);

            if (constructorParamsOnly(subBeanData)) {
                addParamToBeanConstructor(subBeanData, method, subBeanNew, classDescription, codeModel);
                aNew.arg(subBeanNew);
            } else {
                JVar newBeanVar = method.body().decl(subBeanClass, "bean" + fieldIndex++, subBeanNew);
                setProperties(newBeanVar, subBeanData.getPropertyParams(), method, true, classDescription, codeModel);
                addParamToBeanConstructor(subBeanData, method, subBeanNew, classDescription, codeModel);
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
                if (isTrueFalseAsBoolean(constant.getValue())) {
                    aNew.arg(JExpr.lit(Boolean.parseBoolean(constant.getValue())));
                } else {
                    aNew.arg(JExpr.lit(constant.getValue()));
                }
            }
        }

        if (arg instanceof ConstructorMergeableParam) {
            var listParam = (ConstructorMergeableParam) arg;

            JClass listClass = codeModel.ref(listParam.getType().getOutputClass());
            JInvocation list = JExpr._new(listClass);
            JVar listVar = method.body().decl(listClass, listParam.getType().name().toLowerCase() + fieldIndex++, list);

            listParam.getValues().forEach(itt -> {
                setProperties(listVar, List.of(itt), method, false, classDescription, codeModel);
            });

            aNew.arg(listVar);
        }
    }

    private JInvocation getInvocation(BeanData subBeanData, JClass subBeanClass) {
        if (subBeanData.getFactoryMethodName() != null) {
            return subBeanClass.staticInvoke(subBeanData.getFactoryMethodName());
        } else {
            return JExpr._new(subBeanClass);
        }
    }

    private void addMethodParams(BeanData beanData,
                                 JMethod method,
                                 Set<String> guard,
                                 JCodeModel codeModel) {
        beanData.getConstructorParams()
                .forEach(arg -> {
                    process(arg, method, guard, codeModel);
                });

        beanData.getPropertyParams()
                .forEach(arg -> {
                    process(arg, method, guard, codeModel);
                });
    }


    private void process(Param param,
                         JMethod method,
                         Set<String> guard,
                         JCodeModel codeModel) {
        if (param instanceof ConstructorBeanParam) {
            var arg = (ConstructorBeanParam) param;

            if (guard.contains(getGuardKey(arg))) {
                return;
            }

            JVar paramVar = method.param(codeModel.ref(arg.getClassName()), getJavaValidName(arg.getRef()));
            paramVar.annotate(Qualifier.class).param("value", arg.getRef());
            guard.add(getGuardKey(arg));
        } else if (param instanceof ConstructorSubBeanParam) {
            var arg = (ConstructorSubBeanParam) param;
            addMethodParams(arg.getBeanData(), method, guard, codeModel);
        } else if (param instanceof PropertySubBeanParam) {
            var arg = (PropertySubBeanParam) param;
            addMethodParams(arg.getBeanData(), method, guard, codeModel);
        } else if (param instanceof PropertyBeanParam) {
            var arg = (PropertyBeanParam) param;

            if (guard.contains(getGuardKey(arg))) {
                return;
            }

            JVar paramVar = method.param(codeModel.ref(arg.getClassName()), getJavaValidName(arg.getRef()));
            paramVar.annotate(Qualifier.class).param("value", arg.getRef());
            guard.add(getGuardKey(arg));
        } else if (param instanceof MergeableParam) {
            var arg = (MergeableParam<Param>) param;
            arg.getValues().forEach(it -> process(it, method, guard, codeModel));
        }
    }

    private String getJavaValidName(String ref) {
        return ref.replaceAll("[-\\.]", "_");
    }

    @NonNull
    private String getGuardKey(ConstructorBeanParam arg) {
        return arg.getClassName() + arg.getRef();
    }

    @NonNull
    private String getGuardKey(PropertyBeanParam arg) {
        return arg.getClassName() + arg.getRef();
    }

    private String getMethodName(@NonNull String className, @Nullable String id) {
        if (id != null) {
            return getJavaValidName(id);
        }

        //pro.akvel.test.TestBean -> TestBean
        String methodName = className.substring(className.lastIndexOf(".") + 1);

        //TestBean -> testBean
        methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);

        return methodName;
    }


    private static void addBeanAnnotation(@NonNull JMethod method, @NonNull BeanData beanData) {
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
