package test;

public class DatabaseTest2 {

	public static dbthread dbt;
	
	public static void main(String[] args) {
		DatabaseTest2 x = new DatabaseTest2();
		dbt = x.new dbthread();
		dbt.start();
		helper h = x.new helper();
		h.start();
	}
	
	public DatabaseTest2() {
		
	}
	
	class dbthread extends Thread {
		
		//public dbthread() {};
		
		public void run() {
			while(true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void DoDBStuff() {
			System.out.println("Doing DB stuffs");
		}
	}
	
	class helper extends Thread {
		public void run() {
			dbt.DoDBStuff();
		}
	}

}
