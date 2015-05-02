package Executable;

import Aeroport.AeroportEnum;
import Aeroport.Aeroport;
import Avion.Avion;
import Avion.Flight;

public class Main 
{
	public static long startTime = System.currentTimeMillis();	//[ms] Heure du debut de la simulation
	public static int vitesseSimu = 60;							//1 : temps reel
	public static String aeroportName = "Toulouse";				//Modifier ce champ pour changer l'aeroport de la simu
	public static Aeroport aeroport = new Aeroport(aeroportName,
			AeroportEnum.valueOf(aeroportName).getX(),AeroportEnum.valueOf(aeroportName).getY());
	public static Avion[] avions = new Avion[1000];				//Les avions crees sont places dans ce tableau de taille "infinie" au vu de notre simu
	public static int compteur = 0;								//Pour savoir a quel rang il faut placer un nouvel avion cree dans avions
	
	public static void main(String[] args) throws Exception
	{
		//Aeroport ouvert
		aeroport.start();
		
		//Creation des avions et des vols associes sur mon aeroport de depart
		int i = 0;
		while(aeroport.vols[i] != null)
		{
			//Creation du vol (Flight) avec les parametres recuperes du fichier CSV
			Flight flight = new Flight(aeroport, aeroport.name, aeroport.vols[i+4], aeroport.vols[i+8],
					Integer.parseInt(aeroport.vols[i+1]),Integer.parseInt(aeroport.vols[i+2]),
					Integer.parseInt(aeroport.vols[i+3]), Integer.parseInt(aeroport.vols[i+5]),
					Integer.parseInt(aeroport.vols[i+6]),Integer.parseInt(aeroport.vols[i+7]));
			
			//Creation de l'avion
			Avion avion = new Avion();
			avion.flight = flight;
			avions[compteur] = avion;
			i=i+9;
			compteur++;
		}
		
		//Lancement des threads - des avions -
		i=0;
		while(avions[i] != null)
		{
			avions[i].start();
			i++;
		}
		
		//Creation du fichier CSV pour les avions a l'arrivee
		i=0;
		Thread.sleep(avions[0].flight.heureToMillis(4,17,0));	//On attend 4h17min : arbitraire ... c'est l'heure d'arrivee du dernier vol de notre simu
		while(avions[i]!=null)
		{
			System.out.println("Numero de vol :"+avions[i].flight.numeroVol);
			//Avion au depart, on n'ecrit pas dans le CSV
			if (avions[i].departure)
			{
				System.out.println("Thread vivant ?"+avions[i].isAlive());
				//On attend que l'avion au depart soit interrompu, sinon on attend 1s
				if(!avions[i].isAlive()){i++;}
				else{Thread.sleep(1000);}
			}
			//Avion a l'arrivee
			else
			{
				avions[i].join();
				if(!avions[i].departure)
				{
					aeroport.comm.ecriture(avions[i]);
					i++;
				}
			}
		}
		
		//Fermeture de l'aeroport 
		Avion fin = new Avion();
		Flight finalFlight = new Flight(aeroport,"FIN",aeroportName,"",0,0,0,0,0,0);
		fin.flight = finalFlight;
		aeroport.comm.envoyer(fin);
	}
}
