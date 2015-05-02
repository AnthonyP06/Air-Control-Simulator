package Aeroport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import Avion.Avion;
import Avion.Flight;
import Executable.Main;
import Reseau.Communication;

/**
 * Objet Aeroport qui centralise toutes les fonctions de celui-ci.
 * 
 * @author C.Mourard & A.Pagliai
 * @version 1.0 - 10.01.2015
 *
 */

public class Aeroport extends Thread
{
	public String name;								//Nom de l'aeroport
	public int coordX;								//[km] Coordonnee horizontale de l'aeroport
	public int coordY;								//[km] Coordonnee verticale de l'aeroport
	public APP app = new APP();						//APP de l'aeroport
	public CCR ccr = new CCR();						//CCR de l'aeroport
	public CircuitAttente circuit;					//Circuit d'attente de l'aeroport
	public Communication comm = new Communication();//Module reseau de l'aeroport
	public String[] vols;							//Informations a propos de tous les vols
	public static boolean ouvert = true; 			//true : l'aeroport est ouvert (i.e. il peut recevoir des donnees)
	//public Avion[] avions = new Avion[1000];
	//public static Avion[] avionsStatic = new Avion[1000];
	
	public Aeroport(String name, int x, int y)
	{
		this.name = name;
		coordX = x;
		coordY = y;
		
		//Lecture du fichier CSV d'entree
		try {vols = comm.lecture(name+".csv");} 
			catch (Exception e) {e.printStackTrace();}
	}
	
	/**
	 * Setter pour l'attribut serveur
	 * 
	 * @param bool - true : aeroport se comporte comme un serveur - false : aeroport se comporte comme un client
	 */
	public void setOuvert(boolean bool)
	{
		ouvert = bool;
	}
	
	/**
	 * Echanger des donnees d'un aeroport a un autre. 
	 * Ce programme permet a tout aeroport exterieur de se connecter a celui-la afin d'envoyer des donnees de vol vers cet aeroport
	 * 
	 * @param args
	 * @throws IOException - lorsque le port TCP est occupe
	 */
	public void run()
	{
		try 
		{
			String clientSentence = "";
			
			//Ouverture d'une socket pour etre pret a recevoir des donnees 
			ServerSocket welcomeSocket = new ServerSocket(AeroportEnum.valueOf(Main.aeroportName).getPort());
			System.out.println("Aeroport de "+Main.aeroportName+" ouvert.");
			
			//Boucle infinie : on peut se connecter tant que la simulation tourne
			while(ouvert)
			{	
				//Aeroport pret pour qu'un autre aeroport se connecte et lui envoie des donnees
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Connexion reussie");

				//Flux entrant
				BufferedReader inFromClient = new BufferedReader(new 
				InputStreamReader(connectionSocket.getInputStream()));
				
				//Lecture des donnees envoyees par l'autre aeroport
				clientSentence = inFromClient.readLine();
				
				//Decryptage des donnees et creation du nouveau thread
				String[] data = clientSentence.split(Main.aeroport.comm.separateur);		//Taille : 14
				
				//Critere d'arret : ouvert == false
				if(data[0].equals("FIN"))
				{
					welcomeSocket.close();
					System.out.println("Aeroport de "+Main.aeroportName+" ferme.");
					Main.aeroport.setOuvert(false);
				}
					
				else
				{
					//Creation du vol (Flight) avec les parametres recuperes du fichier CSV
					Flight flight = new Flight(Main.aeroport, data[0], data[1], data[13], 
							Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), 
							Integer.parseInt(data[5]), Integer.parseInt(data[6]), Integer.parseInt(data[7]));
					
					flight.positionX = Double.valueOf(data[8]);
					flight.positionY = Double.valueOf(data[9]);
					flight.cap = Double.valueOf(data[10]);
					flight.altitudeAvion = Integer.parseInt(data[11]);
					flight.vitesseAvion = Integer.parseInt(data[12]);
					
					//Affichage de la bonne reception des donnees
					System.out.println("Vol "+data[13]+" connecte au CCR de "+Main.aeroportName);
					
					//Creation de l'avion
					Avion avion = new Avion();
					avion.flight = flight;
					avion.departure = false;
					Main.avions[Main.compteur] = avion;
					Main.compteur++;
						
					//Lancement de l'avion
					avion.start();
				}
			}
		} 	
		catch (NumberFormatException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}		
	}
}
