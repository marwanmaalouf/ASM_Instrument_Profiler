package com.asmproj;

import java.lang.reflect.Field;
import java.util.Random;



public class Testing_2 {

	private static Scheduler myscheduler;

	interface myInterface{
		Object execute();
	}

	class myclass implements myInterface{
		public Object execute(){
			Integer value = new Random().nextInt(42);
			return value;
		}
	}

	class Scheduler{
		myInterface element = new myclass();
		int test_element = 10;
		
		String someString = "test passed !";
		
		public Scheduler(){
			Object result = element.execute();
			System.out.println(result);
		}
	}

	private static Integer myInteger = new Integer(10);
	
	public  static void main(String[] args){
		Integer newInteger = myInteger.MAX_VALUE;
		
		int [] array_of_integers = {1, 2, 3, 4, 5};
		int x = 1;
		double b = 10.5;
		float c = 1.0f;
		long e = 100l;
		b = b + 0.5;

		foo(x);
		Testing_2 test = new Testing_2();
		myscheduler = test.new Scheduler();
		System.out.println(myscheduler.test_element);
		System.out.println(myscheduler.element.toString());
		System.out.println(myscheduler.someString);
	     
	}

	public static String  foo(int x)
	{   
		String input1 = "abc";     
		String input2 = "efg";     
		String input3 = input1 + input2;

		if (x > 1) {
			return input1;
		}
		else {
			return input2;
		}        
	}

}
