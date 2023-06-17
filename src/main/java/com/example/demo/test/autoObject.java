package com.example.demo.test;

import org.springframework.stereotype.Service;

@Service
public class autoObject {
    public static Object[] generateObjectArray(String[] stringArray) {
        Object[] objectArray = new Object[stringArray.length];

        for (int i = 0; i < stringArray.length; i++) {
            String stringValue = stringArray[i];
            // 使用字符串值的类型名称获取相应的类对象
            Class<?> type = getType(stringValue);
            // 根据类对象创建数组，并将字符串值转换为相应的类型
            Object value = convertToType(stringValue, type);
            objectArray[i] = value;
        }

        return objectArray;
    }

    // 根据字符串值判断其类型并返回相应的类对象
    private static Class<?> getType(String value) {
        if (value.matches("-?\\d+")) {
            return int.class;
        } else if (value.matches("-?\\d+\\.\\d+")) {
            return double.class;
        } else {
            return String.class;
        }
    }

    // 将字符串值转换为指定的类型
    private static Object convertToType(String value, Class<?> type) {
        if (type == int.class) {
            return Integer.parseInt(value);
        } else if (type == double.class) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }
}
