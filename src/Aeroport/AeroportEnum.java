package Aeroport;

/**
 * Recensement de tous les aeroports avec lesquels il est possible de communiquer.  
 * 
 * @author C.Mourard & A.Pagliai
 * @version 1.0 - 04.02.2015
 *
 */
public enum AeroportEnum
{	
	Toulouse(0,0,6800,"montcalm.isae.fr"), Nice(500,20,6789,"uderzo.isae.fr");
	
	private int X;				//[km] Coordonnee horizontale de l'aeroport
	private int Y;				//[km] Coordonnee verticale de l'aeroport
	private int port;			//Port utilise pour la connexion reseau
	private String hostname;	//Nom d'hote pour la connexion reseau
	
	/**
	 * Constructeur d'un element de l'enumeration
	 * 
	 * @param X - Coordonnee horizontale de l'aeroport [km]
	 * @param Y - Coordonnee verticale de l'aeroport [km]
	 * @param port - Port utilise pour la connexion reseau
	 * @param hostname - nom d'hote utilise par l'ordinateur hebergeant l'aeroport
	 */
	private AeroportEnum(int X, int Y, int port, String hostname)
	{
		this.X = X;
		this.Y = Y;
		this.port = port;
		this.hostname = hostname;
	}
	
	/**
	 * Getter pour l'attribut port
	 * 
	 * @return attribut port
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Getter pour l'attribut hostname
	 * 
	 * @return attribut hostname
	 */
	public String getHostname()
	{
		return hostname;
	}
	
	/**
	 * Getter pour l'attribut X
	 * 
	 * @return attribut X
	 */
	public int getX()
	{
		return X;
	}
	
	/**
	 * Getter pour l'attribut Y
	 * 
	 * @return attribut Y
	 */
	public int getY()
	{
		return Y;
	}
}
