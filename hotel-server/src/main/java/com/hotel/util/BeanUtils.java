package com.hotel.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import java.beans.FeatureDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author guangyong.yang
 * @date 2019-01-20
 * @description
 */
@Slf4j
public class BeanUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object obj,String filedName) {
        if (Objects.isNull(obj) || Objects.isNull(filedName)) {
            return null;
        }

        String methodName = "get" + StringUtils.capitalize(filedName);
        Method method;
        try {
            method = obj.getClass().getMethod(methodName);
            return (T)method.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
