package com.asmproj.test;

import java.lang.reflect.Field;
import java.util.Random;

public class Testing_2 {

	private static Scheduler myscheduler;
	private static int testSI;
	private int testI;

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
			System.out.println("error here?");
			Object result = element.execute();
			System.out.println("error here?");
			System.out.println(result);
			System.out.println("error here?");
		}
		
		public String virtualMethod(String a, String b){
			test_element++;
			return a;
		}
	}
	
	public static void main(String[] args){		
		int x = -1;
		
		if(x < 0 )
		{
			x = -5;
			if(x == 1){
				x = -3;
			}else{
				int h1 = x;
			}	
		}
		else{
			x = 2;
		}
		
		int h = 0;
		try{
			h = Integer.valueOf("1").intValue();
		}catch(NumberFormatException e){
			h = -1;
		}
		int z = -1;
		
		int [] array_of_integers = {1, 2, 3, 4, 5};
		double [] arr = new double[5];
		arr[1] = 0.1;
		
		Integer somelist [] = new Integer[2];
		somelist[0] = Integer.MAX_VALUE;
		
		
		char [] array = new char[3];
		int  another_array[] = new int[3];
		x = 1;
		double b = 10.5;
		float c = 1.0f;
		long e = 100l;
		b = b + 0.5;

		foo(x);
		Testing_2 test = new Testing_2();
		
		System.out.println("Error here");
		myscheduler = test.new Scheduler();
		System.out.println("not Error here");
		myscheduler.virtualMethod("a", "b");
		
		myscheduler.virtualMethod("a", "b");
		System.out.println(myscheduler.test_element);
		System.out.println(myscheduler.element.toString());
		System.out.println(myscheduler.someString);
		
		test.testI = 5;
		array[0] = 'c';
		test.testSI = 5;
		System.out.println(array[0]);
		System.out.println(myscheduler.test_element);
		System.out.println(myscheduler.element.toString());
		System.out.println(myscheduler.someString);
		
		foo(2);
		
		foo2();    
	}

	public static void foo2(){
		foo(1);
	}

	public static String  foo(int x)
	{   
		String input1 = "vabc";     
		String input2 = "efg";
		String input3 = "hij";
		String input4 = input2 + input1;
		String input5 = input1 + input2;
		
		System.out.println(input1);
		if (x > 1) {
			return input1;
		}
		else {
			return input2;
		}
	}

//	public static void main(String[] args){
//
////		try{
////			int x  =  Integer.valueOf("1").intValue();
////		}catch (NumberFormatException e){
////			e.printStackTrace();
////		}catch(Exception e1){
////			e1.printStackTrace();
////		}
//	}
}
