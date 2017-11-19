import java.util.Set;


/**
 * This class contains a set of methods that are useful for testing
 * Java disassemblers and control-flow graph generators.
 * 
 * @author Matthias.Hauswirth@unisi.ch
 */
public class ExampleClass {
	
	
	
	public static void main(String [] args){
		int local;
		double anotherLocal;
	}
	//--- conditionals
//	public int ifMethod(int i) {
//		int j = 0;
//		if (i<0) {
//			j = 1; 
//		}
//		return j;
//	}
//	
//	public int ifElseMethod(int i) {
//		int j = 0;
//		if (i>0) {
//			j = 0; 
//		} else {
//			j = i;
//		}
//		return j;
//	}
//	
	public static int switchMethod(int i) {
		int j = 0;
		switch (i) {
		case 0: j = 0; break;
		case 1: j = 1; break;
		case 2: j = 2; break;
		default: j = -1;
		}
		return j;
	}
	
	 public static void testLocals(boolean one) {
		    String two = "hello local variables";
		    one = true;
		    int three = 64;
		}
	
	public int switchMethod2(int i) {
		int j = 0;
		switch (i) {
		case 0: j = 0; break;
		case 1000: j = 1; break;
		case 2000: j = 2; break;
		default: j = -1;
		}
		return j;
	}
	
	public int switchMethod3(int i) {
		int j = 0;
		switch (i) {
		default: j = -1;
		}
		return j;
	}
	
	public int trycatchMethod(String s){
		int i = 0;
		try{
			i = Integer.valueOf(s).intValue();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return i;
		}
	}
	
	public void throwMethod() throws Exception{
		throw new Exception("new exception");
		
	}
	
	//--- loops
//	public int forMethod(int i) {
//		int sum = 0;
//		for (int j=0; j<i; i++) {
//			sum += j;
//		}
//		return sum;
//	}
//	
//	public int whileMethod(int i) {
//		int sum = 0;
//		while (i>0) {
//			sum +=i;
//			i--;
//		}
//		return sum;
//	}
//	
//	public int doWhileMethod(int i) {
//		int sum = 0;
//		do {
//			sum += i;
//			i--;
//		} while (i>0);
//		return sum;
//	}
//	
//	public int forEachArrayMethod(String[] a) {
//		int sum = 0;
//		for (String s : a) {
//			sum++;
//		}
//		return sum;
//	}
//	
//	public int forEachCollectionMethod(Set<String> a) {
//		int sum = 0;
//		for (String s : a) {
//			sum++;
//		}
//		return sum;
//	}
//	
//	public int forWithBreakMethod(int n) {
//		int sum = 0;
//		for (int i=0; i<n; i++) {
//			if (i==10) {
//				break;
//			}
//			sum += i;
//		}
//		return sum;
//	}
//	
//	public int forWithContinueMethod(int n) {
//		int sum = 0;
//		for (int i=0; i<n; i++) {
//			if (i==10) {
//				continue;
//			}
//			sum += i;
//		}
//		return sum;
//	}
//	
//	public int whileTrueMethod(int n) {
//		while (true) {
//			n++;
//		}
//	}
//	
//	public int doWhileTrue(int n) {
//		do {
//			n++;
//		} while (true);
//	}
//	
//	public int forEver(int n) {
//		for (int i=0; true; i++) {
//		}
//	}
//
//	public int nestedFor(int n) {
//		int sum = 0;
//		for (int i=0; i<n; i++) {
//			for (int j=0; j<i; j++) {
//				sum += j;
//			}
//		}
//		return sum;
//	}
	
	
	
	//--- calls
//	public int staticCallMethod(int i) {
//		staticCallTarget();
//		return 2;
//	}
//	
//	public int instanceCallMethod(ExampleClass i) {
//		i.instanceCallTarget();
//		return 2;
//	}
//	
//	public int privateInstanceCallMethod(ExampleClass i) {
//		i.privateInstanceCallTarget();
//		return 2;
//	}
//	
//	public int interfaceCallMethod(Interface i) {
//		i.interfaceCallTarget();
//		return 2;
//	}
//	
//	static interface Interface {
//		public void interfaceCallTarget();
//	}
//	
//	static class Implementation implements Interface {
//		public void interfaceCallTarget() {
//			return;
//		}
//	}
//	
//	public static void staticCallTarget() {
//	}
//	
//	public void instanceCallTarget() {	
//	}
//	
//	private void privateInstanceCallTarget() {	
//	}
	
	
	
//	//--- field and array accesses
//	private String field;
//	
//	public String fieldReadMethod() {
//		return field;
//	}
//	
//	public void fieldWriteMethod(String s) {
//		field = s;
//	}
//	
//	private static String staticField;
//	
//	public String staticFieldReadMethod() {
//		return staticField;
//	}
//	
//	public void staticFieldWriteMethod(String s) {
//		staticField = s;
//	}
//	
//	public int arrayLengthMethod(String[] a) {
//		return a.length;
//	}
//	
//	public String arrayReadMethod(String[] a) {
//		return a[0];
//	}
//	
//	public void arrayWriteMethod(String[] a, String s) {
//		a[0] = s;
//	}
//	
//	
//	
//	//--- allocation
//	public Object allocObjectMethod() {
//		return new Object();
//	}
//	
//	public int[] allocIntArrayMethod() {
//		return new int[3];
//	}
//	
//	public Object[] allocObjectArrayMethod() {
//		return new Object[3];
//	}
//	
//	public int[][] alloc2dArrayMethod() {
//		return new int[2][3];
//	}
//	
//	public int[][] allocIncomplete2dArrayMethod() {
//		return new int[2][];
//	}
//	
//	public int[][][] alloc2Of3dArrayMethod() {
//		return new int[2][3][];
//	}
//	
//	public int[] allocAndInitIntArrayMethod() {
//		return new int[] {1, 2};
//	}
//	
//	public Object[] allocAndInitObjectArrayMethod() {
//		return new Object[] {"1", "2"};
//	}
//	
//	public int[][] allocAndInit2dArrayMethod() {
//		return new int[][] {{1}};
//	}
	
	
	
	//--- more conditionals
//	public int condMethod(int a, int b) {
//		return a>b?a:b;
//	}
//	
//	public int shortCircuitMethod(int i, int j, int k) {
//		if (i>j && i<k) {
//			return 1;
//		}
//		return 0;
//	}
//	
//	public int nonShortCircuitMethod(int i, int j, int k) {
//		if (i>j & i<k) {
//			return 1;
//		}
//		return 0;
//	}
	
}
