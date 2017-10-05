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
	//		System.out.println(result);
		}
		
		public String virtualMethod(String a, String b){
			test_element++;
			return a;
		}
	}

	private static Integer myInteger = new Integer(10);
	
	public  static void main(String[] args){
		Integer newInteger = myInteger.MAX_VALUE;
		
		int [] array_of_integers = {1, 2, 3, 4, 5};
		double [] arr = new double[5];
		arr[1] = 0.1;
		
		Integer somelist [] = new Integer[2];
		somelist[0] = Integer.MAX_VALUE;
		
		
		char [] array = new char[3];
		int  another_array[] = new int[3];
		int x = 1;
		double b = 10.5;
		float c = 1.0f;
		long e = 100l;
		b = b + 0.5;

		foo(x);
		Testing_2 test = new Testing_2();
		myscheduler = test.new Scheduler();
		myscheduler.virtualMethod("a", "b");
	//	System.out.println(myscheduler.test_element);
	//	System.out.println(myscheduler.element.toString());
	//	System.out.println(myscheduler.someString);
	     
	}

	

	public static String  foo(int x)
	{   
		String input1 = "vabc";     
		String input2 = "efg";
		String input3 = "hij";
		//String input4 = input2 + input1;
		String input5 = input1 + input2;
		
		System.out.println(input1);
//		if (x > 1) {
//			return input1;
//		}
//		else {
//			return input2;
//		}
		return input3;
	}
//	public  static void main(String[] args){
//		String s1 = "abc";
//		String s2 = "def";
//		String s3 = s2 + s1;
//		System.out.println(s1);
//		System.out.println(s2);
//		System.out.println(s3);
//		}
	
//	public String foo(int x){
//		String s1 = "abc";
//		String s2 = "def";
//		String s3 = s1 + s2;
//		System.out.println(s1);
//		System.out.println(s2);
//		System.out.println(s3);
//		return s3;
//	}

}
