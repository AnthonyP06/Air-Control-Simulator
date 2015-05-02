package Avion;

import Executable.Main;

/**
 * Representation d'un avion au sein du traffic aerien.
 * 
 * @author C.Mourard & A.Pagliai
 * @version 1.0 - 10.01.2015
 */

public class Avion extends Thread
{
	public int vitesseDecollage = 300;			//[km/h]
	public int vitesseAtterrissage = 250;		//[km/h]
	public int vitesseCroisiere = 850;			//[km/h]
	public int altitudeCroisiere = 10000;		//[m]
	public Flight flight;						//Plan de vol contenant les parametres de vol
	public boolean departure = true;			//true : avion au depart - false : avion a l'arrivee dans un autre aeroport
	
	/**
	 * Setter pour l'attribut departure 
	 * 
	 * @param bool - nouvel etat pour l'attribut departure
	 */
	public void setDeparture(boolean bool)
	{
		this.departure = bool;
	}
	
	public void run()
	{	
		if (departure)
		{
			//Attente de l'avion sur le parking jusqu'a son heure de decollage
			int tempsAvantDepart = flight.heureToMillis(flight.heureDepart[0], flight.heureDepart[1], flight.heureDepart[2]);		//[ms]
			try 
			{sleep(tempsAvantDepart/Main.vitesseSimu);} 
			catch (InterruptedException e) 
			{e.printStackTrace();}
		
			//Procedure de decollage geree par l'APP
			try 
			{flight.aeroportActuel.app.decollage(this);} 
			catch (InterruptedException e) 
			{e.printStackTrace();}
			
			//Sortie du circuit d'aeroport geree par le CCR
			flight.aeroportActuel.ccr.sortieCircuit(this);
			
			//Montee de l'avion jusqu'a altitude et vitesse de croisiere geree par le CCR
			flight.aeroportActuel.ccr.montee(this);
			
			//L'avion demande a l'aeroport de depart de basculer sur l'aeroport d'arrivee
			if (flight.aeroportArrivee.equals(flight.aeroportActuel.name) == false)
			{
				try {this.flight.aeroportActuel.comm.envoyer(this);
						this.interrupt();} 
				catch (Exception e) {e.printStackTrace();}
			}
		
			else
			{
				/*
				//Temps de vol
				int tempsDeVol = flight.deltaTemps(flight.heureDepart[0],flight.heureDepart[1],flight.heureDepart[2]
						,flight.heureArrivee[0],flight.heureArrivee[1],flight.heureArrivee[2])
						-APP.dureeDecollage;		//[ms]				
				try 
				{sleep(tempsDeVol/Main.vitesseSimu);} 
				catch (InterruptedException e) 
				{e.printStackTrace();}
				*/
				
				//Vol de croisiere de l'avion gere par le CCR
				flight.aeroportActuel.ccr.croisiere(this);
				
				//Descente de l'avion vers le circuit de l'aeroport d'arrivee geree par le CCR
				flight.aeroportActuel.ccr.descente(this);
				
				//Alignement de l'avion dans l'axe de piste gere par le CCR
				flight.aeroportActuel.ccr.finale(this);
				
				//Procedure d'atterrissage geree par l'APP
				try 
				{flight.aeroportActuel.app.atterrissage(this);} 
				catch (InterruptedException e) 
				{e.printStackTrace();}
				
				//Avion arrive
				this.departure = false;
			}
		}
		
		else //departure : false
		{	
			//Vol de croisiere de l'avion gere par le CCR
			flight.aeroportActuel.ccr.croisiere(this);
			
			//Descente de l'avion vers le circuit de l'aeroport d'arrivee geree par le CCR
			flight.aeroportActuel.ccr.descente(this);
			
			//Alignement de l'avion dans l'axe de piste gere par le CCR
			flight.aeroportActuel.ccr.finale(this);
			
			//Procedure d'atterrissage geree par l'APP
			try 
			{flight.aeroportActuel.app.atterrissage(this);} 
			catch (InterruptedException e) 
			{e.printStackTrace();}
		}
	}
}
