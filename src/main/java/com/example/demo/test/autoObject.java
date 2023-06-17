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
        if (value.matches("\\d+(,\\d+)+")) {
            return int[].class;  // 字符串中包含逗号，并且除了逗号外的值都为数字，则类型为整数数组
        } else if (value.matches("-?\\d+")) {
            return int.class;  // 字符串为整数
        } else if (value.matches("-?\\d+\\.\\d+")) {
            return double.class;  // 字符串为浮点数
        } else {
            return String.class;  // 默认为字符串类型
        }
    }

    // 将字符串值转换为指定的类型
    private static Object convertToType(String value, Class<?> type) {
        if (type == int[].class) {
            // 字符串为逗号分隔的整数值，将其转换为整数数组
            String[] values = value.split(",");
            int[] array = new int[values.length];
            for (int i = 0; i < values.length; i++) {
                array[i] = Integer.parseInt(values[i]);
            }
            return array;
        } else if (type == int.class) {
            // 字符串为整数值，将其转换为整数类型
            return Integer.parseInt(value);
        } else if (type == double.class) {
            // 字符串为浮点数值，将其转换为浮点数类型
            return Double.parseDouble(value);
        } else {
            // 默认情况下，将字符串保持为字符串类型
            return value;
        }
    }
}


