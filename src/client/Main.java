package client;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main{

	static double brutto=0;
	static double tara = 0;

	static String inline;
	static String indtDisp ="";
	static String toWeight;

	static int portnumber = 8000;
	static int batchNumber;
	static int nextRaavare;;

	static boolean loop1 = true;
	static boolean loop2 = true;
	static boolean loop3 = true;

	static boolean measured = false;

	static Scanner sc = new Scanner(System.in);

	public static void main(String argv[]) throws IOException{

		//This allows you to change the desired port the program will run on. 
		//It will be changed when launching the program through cmd: java -jar *NameOfFile*.jar *DesiredPort (>1024)* 
		if(argv.length > 0){			
			try{		
				int foo = 0;		
				foo = Integer.parseInt(argv[0]);		
				if(foo> 1024 ){		
					portnumber = foo;		
					System.out.println(argv[0]);		
				}		
			}	catch(InputMismatchException e){		
				System.out.println(e);		
			}		
		}	
		ASEController aseC = new ASEController(portnumber);
	
		try{
			boolean run = true;
			while(run){		
				aseC.run();
			}

		}
		catch(Exception e){
			System.out.println("Hovedmenu, Exception: "+e.getMessage());
			e.printStackTrace();
		}

	}
}