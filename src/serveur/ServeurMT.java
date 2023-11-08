package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMT extends Thread {
	private int nbrClients;
	private boolean isActive;
	
	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(234);
			//connecter client a n'importe quel moment
			while(isActive) {
				//a chaque conenction
				Socket s = ss.accept();
				++nbrClients;
				// creation d'un nouveau thread a chaque fois une connection est etablie
				new Conversation(s,nbrClients).start();
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class Conversation extends Thread{
		private Socket socket;
		private int numClient;
		public Conversation(Socket s, int num) {
			// TODO Auto-generated constructor stub
			socket =s;
			numClient = num;
		}
		@Override
		//le code qui dois s'executer d'une maniere parallel independament du reste de l'application
		public void run() {
			// code de conversation*
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os,true);
				
				//recupere adresse ip du client
				String IP = socket.getRemoteSocketAddress().toString();		
				
				System.out.println("Connection du client numero : " + numClient + " IP=" + IP  );
				 pw.println("Bienvenu, vous etes le client numero " + numClient);
				 
				 while(true) {
					 String req = br.readLine();
					 System.out.println(IP + " a  envoyé "+req);
					 if(req!=null) {
						 String rep = "Size=" + req.length();
					     pw.println(rep);
					 }
				}
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
			
			
			
		}
	}
	
	
	public static void main(String[] args) {
		new ServeurMT().start();
		
	}

}
