package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServeurJeuxMT extends Thread {
	private int nbrClients;
	private boolean isActive=true;
	private int nombreSecret;
	private boolean fin;
	private String gagnant;
	
	
	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(115);
			//generer un nombre secret
			nombreSecret = new Random().nextInt(1000);
			System.out.println("le nombre est : "+ nombreSecret);
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
				 pw.println("Deviner le nombre secret......? ");
				 
				 while(true) {
					 boolean correct;
					 String req = br.readLine();
					 int nbr = 0;
					 try {
					     nbr = Integer.parseInt(req);
					     correct =true;
					 }catch(Exception e) {
						 
						 correct =false;
					 }
                     if(correct) {
					 System.out.println(IP + " a  envoyé "+req);
					 if(fin == false) {
						 if(nbr > nombreSecret) {
							 pw.println("Le nombre que vous avez saisie est plus grand");
						 }else if(nbr<nombreSecret) {
							 pw.println("Le Nombre que vous avez saise est plus petit");
						 }
						 else{
							 pw.println(" BRAVO ***Vous etes Le Gagnant*** : ");
							 gagnant = IP;
							 System.out.println("Le gagnant est : "+ gagnant);
							 fin =true;
						 }
					 }else {
						 pw.println("JEU TERMINER, LE GAGNANT EST : " + gagnant );
						 
					 }			 
				}
                     else {
                    	 pw.println("Enter  an number");
                    	 
                     }
				 }
				
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
		}
	}
		
	public static void main(String[] args) {
		new ServeurJeuxMT().start();
		
	}
}
