package ca.etsmtl.gti780.lab2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * Le Servlet d'association retourne les adresses des hotes et le
 * code qui leur est associé.
 * 
 * @author Luc Trudeau
 */
public class AssociatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final List<Host> hosts = new ArrayList<Host>();

	private static final XStream xstream = new XStream(
			new JettisonMappedXmlDriver());

	// Configuration de XStream
	static {
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("Host", Host.class);
		xstream.alias("ActiveHosts", List.class);
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AssociatorServlet() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		final String host = request.getParameter("host");
		final String code = request.getParameter("code");

		if (code == null) {
			System.out.println("Le code est Vide!");
		} else if (host != null && !host.isEmpty()) {
			Host hostObj = new Host(host, code);
			hosts.add(hostObj);
		} else {
			System.out
					.println("Ajoutez l'adresse IP de l'hote que vous voulez enregistrer.");
		}

		for (int i = hosts.size() - 1; i >= 0; i--) {
			if (!HostChecker.isHostActive(hosts.get(i).getIp())) {
				hosts.remove(i);
			}
		}

		PrintWriter out = response.getWriter();
		out.write(xstream.toXML(hosts));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
