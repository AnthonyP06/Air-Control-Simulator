package Reseau;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import Aeroport.AeroportEnum;
import Avion.Avion;

public class Communication 
{
	String line = "";									//Une ligne du fichier CSV -> 1 vol
	public String separateur = ";";						//Separateur des infos dans le fichier CSV
	public Semaphore accesReseau = new Semaphore(1);	//Un seul avion peut etre 'envoyer' a la fois sur le reseau

	/**
	 * Lecture d'un fichier CSV dont le chemin d'acces (local au projet) est donne en parametre
	 * 
	 * @param accesFichier - Chemin d'acces du fichier CSV
	 * 
	 * @return les informations contenues dans ce fichier CSV
	 * @throws Exception - si le fichier specifie par le chemin d'acces en parametre est introuvable
	 */
	public String[] lecture(String accesFichier) throws Exception
	{
		String[] vols = new String[10000];	//10000 : grand nombre, pas tres elegant mais plus simple dans un premier temps
		int compteur = 0;					//Pour remplir le tableau de vols
		BufferedReader fichier = new BufferedReader(new FileReader(accesFichier));
		line = fichier.readLine();     		//La premiere ligne ne contient pas de donnees de vol
		while((line = fichier.readLine()) != null)
		{
			String[] vol = line.split(separateur);
			for(int i = 0; i<vol.length; i++)
			{
				vols[compteur] = vol[i];
				compteur++;
			}
		}
		fichier.close();
		return vols;
	}
		
	/**
	 * Ecriture dans le fichier CSV de l'aeroport d'arrivee des donnees de vol d'un avion qui a termine son vol
	 * 
	 * @param avion - l'avion dont on ecrit les donnees dans le fichier CSV
	 * @throws IOException - si le fichier specifie au chemin d'acces mentionne dans le programme n'existe pas et qu'on essaye d'ecrire dedans 
	 */
	public void ecriture(Avion avion) throws IOException
	{
		//Ouverture du fichier CSV (s'il existe deja), sinon on le cree
		FileWriter writer = new FileWriter(new File(avion.flight.aeroportActuel.name+"Arrivees.csv"), true);
		
		//Ecriture dans le fichier des differentes informations
			//Aeroport de depart
			writer.write(avion.flight.aeroportDepart+";");
			
			//Heure de depart
			writer.write(String.valueOf(avion.flight.heureDepart[0])+";");
			writer.write(String.valueOf(avion.flight.heureDepart[1])+";");
			writer.write(String.valueOf(avion.flight.heureDepart[2])+";");
			
			//Aeroport d'arrivee
			writer.write(avion.flight.aeroportArrivee+";");
			
			//Heure d'arrivee
			writer.write(String.valueOf(avion.flight.heureArrivee[0])+";");
			writer.write(String.valueOf(avion.flight.heureArrivee[1])+";");
			writer.write(String.valueOf(avion.flight.heureArrivee[2])+";");
		
			//Numero du vol + retour a la ligne
			writer.write(String.valueOf(avion.flight.numeroVol)+"\n");
		
		//Fermeture du fichier
		writer.close();
	}
	
	/**
	 * Envoi d'un avion depuis cet aeroport vers l'aeroport d'arrivee pour ce vol
	 * 
	 * @param avion - l'avion dont on veut envoyer les donnees
	 * 
	 * @throws Exception
	 */
	public void envoyer(Avion avion) throws Exception
	{
		//Verification que le reseau est libre
		try 
		{accesReseau.acquire();} 
		catch (InterruptedException e) 
		{e.printStackTrace();}
		
		//Donnees a envoyer
		String data = avion.flight.data(avion.flight) + '\n';
		
		//Adresse IP de l'ordinateur qui heberge sur lequel l'autre aeroport est connecte
		InetAddress host = InetAddress.getByName(AeroportEnum.valueOf(avion.flight.aeroportArrivee).getHostname());
		
		//Tentative de connexion avec l'autre aeroport
		Socket clientSocket = new Socket(host, AeroportEnum.valueOf(avion.flight.aeroportArrivee).getPort());
		
		//Flux sortant
		DataOutputStream outData = new DataOutputStream(clientSocket.getOutputStream());
		
		//Envoi des donnees
		outData.writeBytes(data);
		
		//Fermeture socket
		clientSocket.close();
		
		//Reseau a nouveau libre
		accesReseau.release();
	}
}
