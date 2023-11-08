package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServeurMT extends Thread {
	private int nbrClients;
	private boolean isActive=true;
	List<Conversation> clients = new ArrayList<Conversation>();
	
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
				Conversation conversation = new Conversation(s,nbrClients);
				clients.add(conversation);
				conversation.start();
				
				
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class Conversation extends Thread{
		protected Socket socket;
		protected int numClient;
		public Conversation(Socket s, int num) {
			// TODO Auto-generated constructor stub
			socket =s;
			numClient = num;
		}
		public void broadcastMessage(String message ,Socket s,int numClient) {		
			try {
				for(Conversation c : clients) {					
					if(c.socket!=s) {
						if(c.numClient==numClient || numClient == -1) {
					    PrintWriter pw = new PrintWriter(c.socket.getOutputStream(),true);
					    pw.println(message);
						}						
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}								
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
				 pw.println("Bienvenu dans le chat, vous etes le client numero " + numClient);
				 
				 while(true) {
					 String req = br.readLine();
					 if(req.contains(":")) {
					 String[] mes = req.split(":");
					 if(mes.length == 2) {
					        String message = mes[1];
					        int numClient = Integer.parseInt(mes[0]);
					        broadcastMessage(message, socket, numClient);
					  }

					 }else {
						 broadcastMessage(req,socket,-1);

					 }
					 System.out.println(IP + " a  envoyé "+req);
				}
			} catch (Exception e) {				
				e.printStackTrace();
			}
			
			
			
			
		}
	}
	
	
	public static void main(String[] args) {
		new ChatServeurMT().start();
		
	}

}
