package classes.org.incava.diffj;

import java.io.IOException;
import java.util.Scanner;

public class Khayam {
	public int a;
	
	

	private static void numP( int n) throws IOException{
			Scanner s = new Scanner(System.in);
		int number = s.nextInt();
		numP(number);
	}
	static int main (String[] args) {
		int n = 0;
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
				array[i][j] = 1;
			}
		}
		for(int i = 1; i <= n ; i++) {
			for(int j = 1; j <= i; j++) {
				System.out.print(array[i][j]+" ");
			}
			System.out.println();
		}
		return 0;
	}
}