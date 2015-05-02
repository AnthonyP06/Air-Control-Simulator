package Aeroport;

import java.util.concurrent.Semaphore;

import Avion.Avion;
import Executable.Main;

/**
 * Centre de controle regional (CCR). Gestion du traffic lorsque l'avion a quitte la piste
 * 
 * @author C.Mourard & A.Pagliai
 * @version 1.0 - 02.02.2015
 *
 */
public class CCR 
{
	public Semaphore circuit = new Semaphore(20);	//assurer que 20 avions au maximum se trouvent sur le circuit de tour de piste.
	/**
	 * Calcul du cap a suivre et rotation de l'avion sur le cercle palier jusqu'à ce cap
	 * 
	 * @param avion
	 */
	public void sortieCircuit(Avion avion)
	{
		try
		{circuit.acquire();}
		catch (InterruptedException e) 
		{e.printStackTrace();}
		
		long tempsDepuisDecollage = 0;
		int deltaX = (AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX()-avion.flight.aeroportActuel.coordX);	//Difference des abscisses entre les aeroports [km]
		int deltaY = (AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY()-avion.flight.aeroportActuel.coordY);	//Difference des ordonnees entre les aeroports [km]
		/*Pour info (a decommenter)
		System.out.println("DeltaX = "+deltaX+" et DeltaY = "+deltaY);
		*/
		
		//Construction de l'angle objectif (cap a suivre pour rejoindre l'aeroport d'arrivee)	[rad]
		double objectif = 2*Math.atan(deltaY/(deltaX+Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY,2))));
		/*Pour info (a decommenter)
		System.out.println("objectif = "+objectif);
		*/
		
		//On tourne sur le premier palier jusqu'a atteindre le bon cap
		while(Math.abs(avion.flight.cap - (objectif+2*Math.PI)%2*Math.PI) > 0.1)
		{
			tempsDepuisDecollage = Main.vitesseSimu*(System.currentTimeMillis() - Main.startTime)
					- avion.flight.heureToMillis(avion.flight.heureDepart[0], avion.flight.heureDepart[1], avion.flight.heureDepart[2]) 
					- APP.dureeDecollage;	//[ms]
			avion.flight.cap = 0.5*(tempsDepuisDecollage/1000)*avion.vitesseDecollage/3600;		// w=V/R=d(cap)/dt => cap=(V/R)t+0
			avion.flight.positionX = AeroportEnum.valueOf(avion.flight.aeroportDepart).getX() + 2*Math.cos(avion.flight.cap);	//Mise a jour de la position en X en suivant le cercle palier
			avion.flight.positionY = AeroportEnum.valueOf(avion.flight.aeroportDepart).getY() + 2*Math.sin(avion.flight.cap);	//Mise a jour de la position en Y en suivant le cercle palier
		
			/* //Pour info (a decommenter) /!\ 1 affichage par seconde et par avion
			System.out.println("Temps: "+tempsDepuisDecollage+" // cap: "+avion.flight.cap+" // Position: ("+avion.flight.positionX+","+avion.flight.positionY);
			try{Thread.sleep(1000);} 
			catch (InterruptedException e)
			{e.printStackTrace();}
			*/
		}
		avion.flight.cap = objectif;
		circuit.release();
		
		//Affichage pour l'utilisateur
		System.out.println("Le vol "+avion.flight.numeroVol+" est sorti du circuit de l'aeroport de "
				+avion.flight.aeroportDepart+" et suit le cap "+(int)((360+90-(objectif*180/Math.PI))%360)); 
	}
	
	/**
	 * Accelere et eleve l'avion jusqu'aux parametres de croisiere
	 * 
	 * @param avion
	 */
	public void montee(Avion avion)
	{
		// On prend comme origine des temps l'instant d'entree dans la navigation
		long tempsDebutNav = System.currentTimeMillis();		// [ms]
		double xFinCercle = avion.flight.positionX;				// Position en X ou l'on quitte le cercle palier
		double yFinCercle = avion.flight.positionY;				// Position en Y ou l'on quitte le cercle palier
		double distanceAVoler = Math.sqrt(Math.pow(AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX() - avion.flight.aeroportActuel.coordX,2) + Math.pow(AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY() - avion.flight.aeroportActuel.coordY,2));	//Calcul la distance restante avant d'atteindre l'aeroport d'arrivee 
		double distanceDebutDescente = Math.min(200, distanceAVoler/2);	//Calcul de la distance minimale pour entamer la descente
		
		/*Pour info... (decommentez le bloc du dessous)
		System.out.println("Distance restante :"+distanceAVoler+" // Distance minimale pour descendre:"+distanceDebutDescente);
		*/
		
		//On accelere et on monte jusqu'a ce que l'on soit a l'altitude et la vitesse de croisiere ou qu'on arrive a la distance de descente
		long tempsDepuisDebutNav = 0;
		
		while(avion.flight.altitudeAvion < avion.altitudeCroisiere && avion.flight.vitesseAvion < avion.vitesseCroisiere && distanceAVoler > distanceDebutDescente)
		{
			tempsDepuisDebutNav = Main.vitesseSimu*(System.currentTimeMillis() - tempsDebutNav);	//Mise a jour du temps
			avion.flight.positionX = ((550*60/19)*Math.pow(tempsDepuisDebutNav/(1000*3600), 2)/2 + 300*(tempsDepuisDebutNav)/(1000*3600))*Math.cos(avion.flight.cap) + xFinCercle;	//Mise a jour de la position en X qui suit une loi uniformement acceleree rectiligne
			avion.flight.positionY = ((550*60/19)*Math.pow(tempsDepuisDebutNav/(1000*3600), 2)/2 + 300*(tempsDepuisDebutNav)/(1000*3600))*Math.sin(avion.flight.cap) + yFinCercle;	//Mise a jour de la position en Y qui suit une loi uniformement acceleree rectiligne
			avion.flight.altitudeAvion = (int)(500 + (9500*60/19)*tempsDepuisDebutNav/(1000*3600));	//Mise a jour de l'altitude de l'avion qui suit une loi lineaire
			avion.flight.vitesseAvion = (int)(300 + (550*60/19)*tempsDepuisDebutNav/(1000*3600));	//Mise a jour de la vitesse de l'avion qui suit une loi lineaire
			distanceAVoler = Math.sqrt(Math.pow(avion.flight.aeroportActuel.coordX + AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX(), 2) + Math.pow(avion.flight.aeroportActuel.coordY + AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY(), 2) - 4); //Recalcul de la distance restante
		
			/* //Pour info (a decommenter) /!\ 1 affichage par seconde et par avion
			System.out.println("Vitesse: "+avion.flight.vitesseAvion+" // Altitude: "+avion.flight.altitudeAvion+" // Position: ("+avion.flight.positionX+","+avion.flight.positionY);
			try{Thread.sleep(1000);} 
			catch (InterruptedException e)
			{e.printStackTrace();}
			*/
		}
			
		//Affichage pour l'utilisateur
		System.out.println("Le vol "+avion.flight.numeroVol+" a atteint la vitesse de "
					+avion.flight.vitesseAvion+"km/h et l'altitude de "+avion.flight.altitudeAvion+"m.");
	}
	
	/**
	 * Calcul les positions suivant X et Y lors du vol en palier croisiere	 * 
	 * @param avion
	 */
	public void croisiere(Avion avion)
	{
		long tempsFinAcc = System.currentTimeMillis();
		long tempsDepuisFinAcc = 0;
		double y = avion.flight.positionY;	//Position suivant X en debut de palier croisiere
		double x = avion.flight.positionX;	//Position suivant Y en debut de palier croisiere
		double distanceAVoler = Math.sqrt(Math.pow(avion.flight.aeroportActuel.coordX - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX(), 2) + Math.pow(avion.flight.aeroportActuel.coordY - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY(), 2) - 4);
		double distanceDebutDescente = Math.min(200, distanceAVoler/2); 
		/* //Pour info (a decommenter)
		System.out.println("Distance Restante: "+distanceAVoler);
		 */
		
		//On continue de croiser tant que l'on n'est pas a la distance de descente
		while(distanceAVoler > distanceDebutDescente)
		{
			tempsDepuisFinAcc = Main.vitesseSimu*(System.currentTimeMillis() - tempsFinAcc);
			avion.flight.altitudeAvion = avion.altitudeCroisiere;	//Vitesse constante
			avion.flight.vitesseAvion = avion.vitesseCroisiere;		//Altitude constance
			avion.flight.positionX = x + 850*tempsDepuisFinAcc*Math.cos(avion.flight.cap)/(1000*3600);	//Mise a jour des positions qui suivent des lois lineaires
			avion.flight.positionY = y + 850*tempsDepuisFinAcc*Math.sin(avion.flight.cap)/(1000*3600);
			distanceAVoler = Math.sqrt(Math.pow(avion.flight.positionX - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX(), 2) + Math.pow(avion.flight.positionY - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY(), 2) - 4);
		
		/*Pour info (a decommenter) /!\ 1 affichage par seconde par avion
		 	System.out.println("Vitesse: "+avion.flight.vitesseAvion+" // Altitude: "+avion.flight.altitudeAvion+" // Position: ("+avion.flight.positionX+","+avion.flight.positionY+")");
			try{Thread.sleep(1000);} 
			catch (InterruptedException e)
			{e.printStackTrace();}
		*/
		}
		
		//Affichage pour l'utilisateur
		System.out.println("Fin du vol en croisiere, le vol "+avion.flight.numeroVol+" va entammer sa descente vers "+avion.flight.aeroportArrivee);	}

	
	/**
	 * Decelere et fait descendre l'avion jusqu'aux parametres d'atterrissage
	 * 
	 * @param avion
	 */
	public void descente(Avion avion)
	{
		//On note le temps et les coordonees a partir de cet instant
		long tempsDebutDescente = System.currentTimeMillis();
		long tempsDepuisDebutDescente = 0;
		double xDebutDescente = avion.flight.positionX;
		double yDebutDescente = avion.flight.positionY;
		double distanceAVoler = Math.sqrt(Math.pow(avion.flight.positionX - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX(), 2) + Math.pow(avion.flight.positionY - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY(), 2) - 4);
		/* //Pour info (a decommenter)
		System.out.println("Distance Restante: "+distanceAVoler+" // Position: ("+avion.flight.positionX+","+avion.flight.positionY+")");
		 */
		
		//Boucle tant qu'on a pas les bons parametres
		while(avion.flight.altitudeAvion > 2000 || avion.flight.vitesseAvion > avion.vitesseAtterrissage || distanceAVoler > 2)
		{
			//Mise a jour du temps et de la distance restante
			distanceAVoler = Math.sqrt(Math.pow(avion.flight.positionX - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX(), 2) + Math.pow(avion.flight.positionY - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY(), 2) - 4);
			tempsDepuisDebutDescente = Main.vitesseSimu*(System.currentTimeMillis() - tempsDebutDescente);
			// Calcul de la position suivant Y
			if(Math.abs(avion.flight.positionY - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY()) <= 2) //Si la bonne position est atteinte, on ne la modifie plus
			{
				avion.flight.positionY = AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY() + 2*Math.sin(avion.flight.cap);
			}
			else //Sinon mise a jour suivant une loi uniformement deceleree
			{
				avion.flight.positionY = ((-600*450/(2*distanceAVoler)*Math.pow(tempsDepuisDebutDescente/(1000*3600),2) + 850*tempsDepuisDebutDescente/(1000*3600)))*Math.sin(avion.flight.cap) + yDebutDescente;	
			}
			//Calcul de la position suivant X
			if(Math.abs(avion.flight.positionX - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX()) <= 2) //Si la bonne position est atteinte, on ne la modifie plus
			{
				avion.flight.positionX = 2*Math.cos(avion.flight.cap)+(AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX());
			}
			else //Sinon mise a jour suivant une loi uniformement deceleree
			{
				avion.flight.positionX = ((-600*450/(2*distanceAVoler)*Math.pow(tempsDepuisDebutDescente/(1000*3600),2) + 850*tempsDepuisDebutDescente/(1000*3600)))*Math.cos(avion.flight.cap) + xDebutDescente;
			}
			//Calcul de l'altitude
			if(avion.flight.altitudeAvion > 2000) //Si l'altitude de palier atterrissage est atteinte, on ne la modifie plus
			{
				avion.flight.altitudeAvion = (int)(avion.altitudeCroisiere - (8000*60/16)*tempsDepuisDebutDescente/(1000*3600));
			}
			else	//Sinon mise a jour avec une loi lineaire
			{
				avion.flight.altitudeAvion = 2000;
			}
			//Calcul de la vitesse
			if(avion.flight.vitesseAvion > avion.vitesseAtterrissage) //Si la vitesse d'atterrissage est atteinte, on ne la modifie plus
			{
				avion.flight.vitesseAvion = (int)(avion.vitesseCroisiere - (600*60/16)*tempsDepuisDebutDescente/(1000*3600));
			}
			else	//Sinon on la met a jour en suivant une loi lineaire
			{
				avion.flight.vitesseAvion = avion.vitesseAtterrissage;
			}
			
		/*Pour info (a decommenter) /!\ 1 affichage par seconde par avion
		System.out.println("Vitesse: "+avion.flight.vitesseAvion+" // Altitude: "+avion.flight.altitudeAvion+" // Position: ("+avion.flight.positionX+","+avion.flight.positionY+")");
		try{Thread.sleep(1000);} 
		catch (InterruptedException e)
		{e.printStackTrace();}
		*/
		}
		
		/*Pour info : normalement, on doit etre sur le cercle d'attente de l'aeroport d'arrivee
		double cercle = Math.pow(avion.flight.positionX - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX(),2)+Math.pow(avion.flight.positionY - AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY(),2)-4;
		System.out.println("Verification, on doit avoir 0, on a: "+cercle);
		*/
		
		//Affichage pour l'utilisateur
		System.out.println("Stabilisation du vol "+avion.flight.numeroVol+" au point ("+(int)avion.flight.positionX+";"+(int)avion.flight.positionY+") a l'altitude de "
				+avion.flight.altitudeAvion+"m et a la vitesse de "+avion.flight.vitesseAvion+"km/h");
	}

	/**
	 * Fait parcourir le cercle de palier atterrissage jusqu'a l'extremite ouest
	 * 
	 * @param avion
	 */
	public void finale(Avion avion)
	{
		try 
		{circuit.acquire();} 
		catch (InterruptedException e) 
		{e.printStackTrace();}
		
		//On prend l'origine du temps
		long tempsDebutCircuit = Main.vitesseSimu*(System.currentTimeMillis()-Main.startTime);
		long tempsDansCircuit = 0;
		
		// On tourne jusqu'a ce que l'avion soit a l'ouest
		while(Math.abs(avion.flight.cap-Math.PI) < 0.1)
		{
			tempsDansCircuit = Main.vitesseSimu*(System.currentTimeMillis()-Main.startTime)-tempsDebutCircuit;	//Mise a jour du temps
			avion.flight.cap = 0.5*(tempsDansCircuit/1000)*avion.vitesseAtterrissage/3600;		// w=V/R=d(cap)/dt => cap=(V/R)t+0
			avion.flight.positionX = AeroportEnum.valueOf(avion.flight.aeroportArrivee).getX() + 2*Math.cos(avion.flight.cap);	//Mise a jour de la position suivant X en suivant le cercle
			avion.flight.positionY = AeroportEnum.valueOf(avion.flight.aeroportArrivee).getY() + 2*Math.sin(avion.flight.cap);	//Mise a jour de la position suivant Y en suivant le cercle
			
		/*	//Pour info (a decommenter) /!\ 1 affichage par seconde par avion
		 	System.out.println("Cap :"+avion.flight.cap+" // Vitesse: "+avion.flight.vitesseAvion+" // Altitude: "+avion.flight.altitudeAvion+" // Position: ("+avion.flight.positionX+","+avion.flight.positionY+")");
			try{Thread.sleep(1000);} 
			catch (InterruptedException e)
			{e.printStackTrace();}
		*/
		}
		
		//Affichage pour les utilisateurs
		System.out.println("Le vol "+avion.flight.numeroVol+" est en finale, pret a atterrir");
		circuit.release();
	}
}

