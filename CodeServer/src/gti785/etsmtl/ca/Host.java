package gti785.etsmtl.ca;

public class Host {
	/**
	 * Adresse IP de l'hote.
	 */
	private final String ip;
	/**
	 * Code relié à cette adresse.
	 */
	private final String code;

	/**
	 * @param ip
	 *            Adresse IP de l'hote pas obligatoirement sous
	 *            forme numérique.
	 * @param code Code associé à cet hote.
	 */
	public Host(String ip, String code) {
		this.ip = ip;
		this.code = code;
	}

	/**
	 * @return L'adresse IP de l'hote.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return le code de l'hote.
	 */
	public String getCode() {
		return code;
	}
}
