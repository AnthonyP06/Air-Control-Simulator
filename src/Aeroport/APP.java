package Aeroport;

import java.util.concurrent.Semaphore;
import Avion.Avion;
import Executable.Main;

/**
 * Centre de Controle d'Approche (APP). Gestion du parking et de la piste
 * 
 * @author C.Mourard & A.Pagliai
 * @version 1.0 - 10.01.2015
 *
 */
public class APP 
{
	public Semaphore piste = new Semaphore(1); 			//La piste ne peut etre accedee que par un seul avion a la fois
	public static int dureeDecollage = 96000;			//[ms] Duree du decollage
	public static int dureeAtterrissage = 122000;		//[ms] Duree de l'atterrissage

	/**
	 * Procedure de decollage geree par l'APP de l'aeroport de depart
	 * 
	 * @throws InterruptedException - lorsqu'un thread (Avion) qui demande a acceder a la piste est interrompu
	 */
	public void decollage(Avion avion) throws InterruptedException
	{
		//Verification que la piste de depart est degagee
		try 
		{piste.acquire();} 
		catch (InterruptedException e) 
		{e.printStackTrace();}
			
		//Decollage lorsque piste de depart degagee
			//Heure de decollage
			long time = Main.vitesseSimu*(System.currentTimeMillis()-Main.startTime); 				//[ms]
			avion.flight.heureDepart = avion.flight.millisToHeure(time);							//[h,min,s]
			System.out.println("Le vol "+avion.flight.numeroVol+" a decolle de "+avion.flight.aeroportActuel.name+" a "
					+avion.flight.heureDepart[0] + "h "+avion.flight.heureDepart[1]+"min "+avion.flight.heureDepart[2]+"s");
			
			//L'avion decolle effectivement
			Avion.sleep(dureeDecollage/Main.vitesseSimu);
			
			//Altitude juste apres decollage
			avion.flight.altitudeAvion = 500;
			System.out.println(avion.flight.altitudeAvion + " m");
				
			//Vitesse juste apres decollage
			avion.flight.vitesseAvion = 300;
			System.out.println(avion.flight.vitesseAvion + " km/h");
			
			//Positions juste apres le decollage
			avion.flight.positionX = avion.flight.aeroportActuel.coordX+2;
			avion.flight.positionY = avion.flight.aeroportActuel.coordY;

		//Piste de depart degagee pour un autre avion
		piste.release();	
	}
	
	/**
	 * Procedure d'atterrissage geree par l'APP de l'aeroport d'arrivee
	 * 
	 * @throws InterruptedException lorsqu'un thread (Avion) qui demande a atterrir est interrompu
	 */
	public void atterrissage(Avion avion) throws InterruptedException
	{
		//Verification que la piste d'arrivee est degagee
		try 
		{piste.acquire();} 
		catch (InterruptedException e)
		{e.printStackTrace();}
		
		//Atterrissage lorsque la piste d'arrivee est degagee
			//Heure de l'atterrissage
			long time = (Main.vitesseSimu)*(System.currentTimeMillis()-Main.startTime); 			//[ms]
			avion.flight.heureArrivee = avion.flight.millisToHeure(time);							//[h,min,s]
			System.out.println("Le vol "+avion.flight.numeroVol+" a atterri a "+avion.flight.aeroportActuel.name+" a "
					+avion.flight.heureArrivee[0] + "h "+avion.flight.heureArrivee[1]+"min "+avion.flight.heureArrivee[2]+"s");
			
			//L'avion atterri effectivement
			Avion.sleep(dureeAtterrissage/Main.vitesseSimu);
		
			//Altitude juste apres atterrissage
			avion.flight.altitudeAvion = 0;
			System.out.println(avion.flight.altitudeAvion + " m");
		
			//Vitesse juste apres atterrissage
			avion.flight.vitesseAvion = 0;
			System.out.println(avion.flight.vitesseAvion + " km/h");
			
		//Piste degagee pour un autre avion
		piste.release();
	}
}
