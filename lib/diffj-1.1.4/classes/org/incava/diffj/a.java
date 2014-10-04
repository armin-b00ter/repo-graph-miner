package classes.org.incava.diffj;

import java.util.Scanner;

public class a {
	public int a = 0;
	public int b;
	public a() {
		this.a = 2;
	}
	static void numP(int n) {
			Scanner s = new Scanner(System.in);
		int number = s.nextInt();
		numP(number);
	}
	public static void main(String[] args) {
		int n = 1;
		long [][] array = new long[n + 1][];
		for(int i = 1; i <= n; i++) {
			array[i] = new long[i + 1];
			for(int j = 1; j <= i; j++) {
				if(j == 1 || j == i) {
					array[i][j] = 1;
				}
				else {
					array[i][j] = array[i - 1][j] + array[i - 1][j - 1];
				}
				array[i][i] = 1;
			}
		}
		for(int i = 1; i <= n ; i++) {
			for(int j = 1; j <= i; j++) {
				System.out.print(array[i][j]+" ");
			}
			System.out.println();
		}
	}
}