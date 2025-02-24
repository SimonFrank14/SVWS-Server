package de.svws_nrw.server.jetty;

import java.util.function.Function;

import javax.security.auth.Subject;
import jakarta.ws.rs.WebApplicationException;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.security.Password;

import de.svws_nrw.data.benutzer.BenutzerApiPrincipal;
import de.svws_nrw.db.utils.ApiOperationException;



/**
 * Diese Klasse implementiert den {@link LoginService} des Jetty-Services und stellt
 * die wesentlichen Teile des Authentifizierungs-Prozesses im SVWS-Server zur Verfügung. <br>
 * Hierbei ist insbesondere die Implementierung der login-Methode von Beudeutung.
 */
public final class SVWSLoginService extends AbstractLifeCycle implements LoginService {

	/** der Identity-Service, welcher in diesem LoginService genútzt wird. */
	protected IdentityService userIdentityService = new SVWSIdentityService();

	/** Der Name dieses Authentifizierungs-Dienstes */
	protected String serviceName;



	/**
	 * Erzeugt einen neuen Authentifizierungs-Dienst mit dem angegebenen Dienst-Namen.
	 *
	 * @param serviceName   der Name des Authentifizierungs-Dienstes
	 */
	public SVWSLoginService(final String serviceName) {
		this.serviceName = serviceName;
	}


	@Override
	public String getName() {
		return serviceName;
	}


	@Override
	public UserIdentity login(final String username, final Object credentials, final Request req, final Function<Boolean, Session> getOrCreateSession) {
		// Akzeptiere nur HTTP-Anfragen der richtigen Versionen
		final HttpVersion version = req.getConnectionMetaData().getHttpVersion();
		if ((version != HttpVersion.HTTP_1_0) && (version != HttpVersion.HTTP_1_1) && (version != HttpVersion.HTTP_2) && (version != HttpVersion.HTTP_3)) {
			System.err.println("Fehler bei dem Verbindungsaufbau. Die Protokolle Version %s wird nicht unterstützt.".formatted(version.toString()));
			return null;
		}

		// Wandle die Crendentials in einen Passwort-String um
		final String password;
		if (credentials instanceof char[])
			password = new String((char[]) credentials);
		else if ((credentials instanceof String) || (credentials instanceof Password))
			password = credentials.toString();
		else {
			System.err.println("Fehler beim Prüfen des Kennwortes! " + credentials.getClass());
			return null;
		}

		// Prüfe, ob ein Login bei der OpenAPI-Applikation erfolgreich ist
		final BenutzerApiPrincipal principal;
		try {
			principal = BenutzerApiPrincipal.login(username, password, Request.getPathInContext(req));
			if (principal == null)
				return null;
		} catch (final ApiOperationException e) {
			throw new WebApplicationException(e.getMessage(), e, e.getStatus());
		}

		// Erzeuge UserIdentity zur weiteren Handhabung im Rahmen des Jetty-LoginService, diese erlaubt auch den Zugriff auf den
		// UserPrincpial mit den SchILD-BenutzerInformationen
		final Subject subject = new Subject();
		subject.getPrincipals().add(principal);
		subject.setReadOnly();
		return userIdentityService.newUserIdentity(subject, principal, new String[] { "user" });
	}


	@Override
	public boolean validate(final UserIdentity user) {
		// TODO prüfe, ob der angemeldete Benutzer immer noch gültig angemeldet ist
		return true;
	}


	@Override
	public void logout(final UserIdentity user) {
		// TODO invalidate UserIdentity
	}


	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + serviceName + "]";
	}


	@Override
	public IdentityService getIdentityService() {
		return userIdentityService;
	}


	@Override
	public void setIdentityService(final IdentityService identityService) {
		if (isRunning())
			throw new IllegalStateException("Running");
		userIdentityService = identityService;
	}

}
