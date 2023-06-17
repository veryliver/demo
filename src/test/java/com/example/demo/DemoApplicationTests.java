package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.test.autoObject;
import com.example.demo.test.test;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
		test testFunction = new test();
		autoObject autoObject = new autoObject();
		String code = "public class Solution {\r\n" + //
				"    public int[] twoSum(int[] nums, int target) {\r\n" + //
				"        int n = nums.length;\r\n" + //
				"        for (int i = 0; i < n; ++i) {\r\n" + //
				"            for (int j = i + 1; j < n; ++j) {\r\n" + //
				"                if (nums[i] + nums[j] == target) {\r\n" + //
				"                    return new int[]{i, j};\r\n" + //
				"                }\r\n" + //
				"            }\r\n" + //
				"        }\r\n" + //
				"        return new int[0];\r\n" + //
				"    }\r\n" + //
				"}";

		// Object[] data = { "babad" };
		String[] stringArray = { "2,4,3", "5,6,4" };
		Object[] data = autoObject.generateObjectArray(stringArray);
		testFunction.compileAndRunCode(code, data);
	}

}
