package Avion;

import Aeroport.Aeroport;

/**
 * Gestion des parametres de vol.
 * 
 * @author C.Mourard & A.Pagliai
 * @version 1.0 - 10.01.2015
 */

public class Flight 
{
	public Aeroport aeroportActuel;				//aeroport qui controle le vol effectivement
	public String aeroportDepart;				//nom de l'aeroport de depart
	public String aeroportArrivee;				//nom de l'aeroport d'arrivee
	public int[] heureDepart = new int[3];		//[h,min,s]
	public int[] heureArrivee = new int[3];		//[h,min,s]
	public double positionX;					//[km] Coordonnee horizontale de l'avion pendant le vol
	public double positionY;					//[km] Coordonnee verticale de l'avion pendant le vol
	public double cap;							//[rad] Cap suivi par l'avion pendant le vol : 0 a l'EST et > 0 dans le sens trigo
	public int altitudeAvion = 0;				//[m] Altitude pendant le vol
	public int vitesseAvion = 0;				//[km/h] Vitesse pendant le vol
	public String numeroVol;					//Numero de vol
	
	/**
	 * Constructeur du plan de vol
	 * 
	 * @param aeroport - aeroport sur lequel se trouve l'avion
	 * @param numero - le numero du vol
	 * @param hD - heure du depart
	 * @param minD - minute du depart
	 * @param sD - seconde du depart
	 * @param hA - heure de l'arrivee
	 * @param minA - minute de l'arrivee
	 * @param sA - seconde de l'arrivee
	 */
	public Flight(Aeroport aeroport, String depart, String arrivee, String numero, int hD, int minD, int sD, int hA, int minA, int sA)
	{
		aeroportActuel = aeroport;
		aeroportDepart = depart;
		aeroportArrivee = arrivee;
		numeroVol = numero;
		heureDepart[0] = hD;
		heureDepart[1] = minD;
		heureDepart[2] = sD;
		heureArrivee[0] = hA;
		heureArrivee[1] = minA;
		heureArrivee[2] = sA;
	}
	
	/**
	 * Convertir une duree en millisecondes en une heure de la forme [h,min,s]
	 * 
	 * @param timeMillis - la duree en millisecondes
	 * 
	 * @return l'heure associe a cette duree au format [h,min,s]
	 */
	public int[] millisToHeure(long timeMillis)
	{
		int[] heure = new int[3];
		heure[2] = (int)(timeMillis/1000)%60;					//[s]
		heure[1] = (int)(timeMillis/(1000*60))%60;				//[min]
		heure[0] = (int)(timeMillis/(1000*3600));				//[h]
		return heure;
	}
	
	/**
	 * Convertir une heure de la forme [h,min,s] en une duree en millisecondes
	 * 
	 * @param h - nombre d'heures
	 * @param min - nombre de minutes
	 * @param s - nombre de secondes
	 * 
	 * @return la duree en millisecondes
	 */
	public int heureToMillis(int h, int min, int s)
	{
		int ms = 0;
		ms = 1000*(s+60*min+3600*h);
		return ms;
	}
	
	/**
	 * Calcul une difference entre deux heures au format [h,min,s]. Si le resultat est negatif, c'est que l'heure de debut [hD,minD,sD] est plus tard que l'heure de fin. 
	 * 
	 * @param hD - heures debut
	 * @param minD - minutes debut
	 * @param sD - secondes debut
	 * @param hF - heures fin
	 * @param minF - minutes fin
	 * @param sF - secondes fin
	 * 
	 * @return la difference entre les deux heures en millisecondes
	 */
	public int deltaTemps(int hD, int minD, int sD, int hF, int minF, int sF)
	{
		int delta = 0;	//[ms]
		
		//Secondes
		if (sF-sD >=0)
		{delta = delta + 1000*(sF-sD);}
		else
		{delta = delta + 1000*(sF+60-sD);
			minF--;}
		
		//Minutes
		if (minF-minD >=0)
		{delta = delta + 1000*60*(minF-minD);}
		else
		{delta = delta + 1000*60*(minF+60-minD);
			hF--;}
		
		//Heures
		delta = delta + 1000*3600*(hF-hD);
		
		return delta;
	}
	
	/**
	 * Ecrire les attributs dans un long String ou chaque attribut est separe d'un autre attribut par une virgule. Cette methode nous sera utile pour envoyer
	 * les donnees de vol d'un aeroport vers un autre.
	 * 
	 * @param flight - le vol dont on veut envoyer les donnees
	 * 
	 * @return les donnees du vol sous forme d'un long String
	 */
	public String data(Flight flight)
	{
		String data = "";
		
		//Aeroports de depart et d'arrivee
		data = data+flight.aeroportDepart;
		data = data+";"+flight.aeroportArrivee;
		
		//Heure de depart
		data = data+";"+String.valueOf(flight.heureDepart[0]);
		data = data+";"+String.valueOf(flight.heureDepart[1]);
		data = data+";"+String.valueOf(flight.heureDepart[2]);
		
		//Heure d'arrivee
		data = data+";"+String.valueOf(flight.heureArrivee[0]);
		data = data+";"+String.valueOf(flight.heureArrivee[1]);
		data = data+";"+String.valueOf(flight.heureArrivee[2]);
		
		//Donnees de vol de l'avion 
		data = data+";"+String.valueOf(flight.positionX);
		data = data+";"+String.valueOf(flight.positionY);
		data = data+";"+String.valueOf(flight.cap);
		data = data+";"+String.valueOf(flight.altitudeAvion);
		data = data+";"+String.valueOf(flight.vitesseAvion);
		
		//Numero de vol
		data = data+";"+flight.numeroVol;
		
		return data;
	}
}
