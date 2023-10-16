package de.svws_nrw.api.privileged;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import de.svws_nrw.config.SVWSKonfiguration;
import de.svws_nrw.core.data.BenutzerKennwort;
import de.svws_nrw.core.data.SimpleOperationResponse;
import de.svws_nrw.core.data.db.MigrateBody;
import de.svws_nrw.core.data.db.SchemaListeEintrag;
import de.svws_nrw.core.logger.LogConsumerConsole;
import de.svws_nrw.core.logger.LogConsumerList;
import de.svws_nrw.core.logger.LogLevel;
import de.svws_nrw.core.logger.Logger;
import de.svws_nrw.core.types.ServerMode;
import de.svws_nrw.core.types.benutzer.BenutzerKompetenz;
import de.svws_nrw.data.benutzer.DBBenutzerUtils;
import de.svws_nrw.data.privileged.DBUtilsPrivileged;
import de.svws_nrw.db.Benutzer;
import de.svws_nrw.db.DBConfig;
import de.svws_nrw.db.DBDriver;
import de.svws_nrw.db.DBEntityManager;
import de.svws_nrw.db.DBException;
import de.svws_nrw.db.schema.SchemaRevisionen;
import de.svws_nrw.db.schema.dto.DTOInformationSchema;
import de.svws_nrw.db.schema.dto.DTOInformationUser;
import de.svws_nrw.db.utils.OperationError;
import de.svws_nrw.db.utils.schema.DBMigrationManager;
import de.svws_nrw.db.utils.schema.DBRootManager;
import de.svws_nrw.db.utils.schema.DBSchemaManager;
import de.svws_nrw.db.utils.schema.DBSchemaStatus;
import de.svws_nrw.db.utils.schema.DBSchemaVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

/**
 * Die Klasse spezifiziert die OpenAPI-Schnittstelle für den Zugriff auf Schemaoperationen mit Root-Rechten.
 * Ein Zugriff erfolgt über den Pfad https://{Hostname}/db/{schema}/api/schema/root/...
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "SchemaPrivileged")
public class APISchemaPrivileged {

	private static final Random random = new Random();

	/**
	 * Erzeugt eine einfache Anwort mit der Angabe, ob die Operation erfolgreich war und
	 * mit dem Log derOperation.
	 *
	 * @param success   gibt an, ob die Operation erfolgreich war oder nicht
	 * @param log       der Log der Operation
	 *
	 * @return das Response-Objekt
	 */
	private static SimpleOperationResponse simpleResponse(final boolean success, final LogConsumerList log) {
		final SimpleOperationResponse response = new SimpleOperationResponse();
		response.success = success;
		response.log = log.getStrings();
		return response;
	}


    /**
     * Die OpenAPI-Methode für die Abfrage der Liste aller SVWS-Schemata im DBMS.
     *
     * @param request   die Informationen zur HTTP-Anfrage
     *
     * @return die Liste der vorhandenen SVWS-Schemata in der Datenbank
     */
    @GET
    @Path("/api/schema/root/svwsliste")
    @Operation(summary = "Liefert eine Liste der SVWS-Schemata.",
    description = "Liefert eine Liste der SVWS-Schemata. Hierfür werden root-Rechte auf der Datenbank benötigt.")
    @ApiResponse(responseCode = "200", description = "Die Schema-Liste mit den Namen und den Versionsinformationen des Schemas",
    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SchemaListeEintrag.class))))
	@ApiResponse(responseCode = "403", description = "Der angegebene Benutzer besitzt nicht die Rechte, um die SVWS-Schema-Liste der Datenbank auszulesen. Hierfür werden root-Rechte benötigt")
    public List<SchemaListeEintrag> getSVWSSchemaListe(@Context final HttpServletRequest request) {
    	final Benutzer user = DBBenutzerUtils.getSVWSUser(request, ServerMode.STABLE, BenutzerKompetenz.KEINE);
    	try (DBEntityManager conn = user.getEntityManager()) {
			// Lese zunächst alle Schemata in der DB ein. Dies können auch Schemata sein, die keine SVWS-Server-Schemata sind!
			final List<String> all = DTOInformationSchema.queryNames(conn);
			final ArrayList<SchemaListeEintrag> result = new ArrayList<>();
			// Filtere alle Schemata heraus, die gültige SVWS-Schemata sind.
			for (final String schemaname : all) {
				final DBSchemaStatus status = DBSchemaStatus.read(user, schemaname);
				final DBSchemaVersion version = status.getVersion();
				if (version == null) // Kein gültiges SVWS-Schema, prüfe das nächste Schema...
					continue;
				if (version.getRevisionOrDefault(Integer.MIN_VALUE) != Integer.MIN_VALUE) {
					final SchemaListeEintrag schemainfo = new SchemaListeEintrag();
					schemainfo.name = schemaname;
					schemainfo.revision = version.getRevisionOrDefault(-1);
					schemainfo.isTainted = version.isTainted();
					result.add(schemainfo);
				}
			}
			return result;
    	}
    }



    /**
     * Die OpenAPI-Methode für die Abfrage der Liste aller Schemata im DBMS.
     *
     * @param request   die Informationen zur HTTP-Anfrage
     *
     * @return          die Liste der vorhandenen Schemata in der Datenbank
     */
    @GET
    @Path("/api/schema/root/liste")
    @Operation(summary = "Liefert eine Liste der Schemata.",
    description = "Liefert eine Liste der Schemata. Hierfür werden root-Rechte auf der Datenbank benötigt.")
    @ApiResponse(responseCode = "200", description = "Die Liste mit allen sichtbaren Schema-Namen in der Datenbank",
    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))))
	@ApiResponse(responseCode = "403", description = "Der angegebene Benutzer besitzt nicht die Rechte, um die Schema-Liste der Datenbank auszulesen. Hierfür werden root-Rechte benötigt")
    public List<String> getSchemaListe(@Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
			return DTOInformationSchema.queryNames(conn);
    	}
    }



    /**
     * Die OpenAPI-Methode für die Abfrage ob ein Datenbankschema mit bestimmtem Namen bereits existiert.
     *
     * @param schemaname    das Datenbankschema, auf das geprüft werden soll
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              Rückmeldung, ob das angegebene Schema existiert
     */
	@GET
	@Path("/api/schema/root/exists/{schema}")
	@Operation(summary = "Liefert die Information, ob ein Schema existiert.",
		description = "Liefert die Information, ob ein Schema existiert. Hierfür werden root-Rechte auf der Datenbank benötigt.")
	@ApiResponse(responseCode = "200", description = "true, wenn das Schema existiert",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
	@ApiResponse(responseCode = "403", description = "Der angegebene Benutzer besitzt nicht die Rechte, um die Schema-Liste der Datenbank auszulesen. Hierfür werden root-Rechte benötigt")
	public boolean existsSchema(@PathParam("schema") final String schemaname, @Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	final List<String> schemata = DTOInformationSchema.queryNames(conn);
			return schemata.contains(schemaname.toLowerCase());
    	}
	}



    /**
     * Die OpenAPI-Methode für die Abfrage ob ein Datenbankuser mit bestimmtem Namen bereits existiert.
     *
     * @param username    der Datenbankusername, auf den geprüft werden soll
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return            Rückmeldung, ob der angegebene User existiert
     */
    @GET
    @Path("/api/schema/root/user/{user}/exists")
    @Operation(summary = "Liefert die Information, ob ein DBMS-User existiert.",
    description = "Liefert die Information, ob ein DBMS-User existiert. Hierfür werden root-Rechte auf der Datenbank benötigt.")
    @ApiResponse(responseCode = "200", description = "true, wenn der Benutzer existiert",
    	content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
	@ApiResponse(responseCode = "403", description = "Der angegebene Benutzer besitzt nicht die Rechte, um die Schema-Liste der Datenbank auszulesen. Hierfür werden root-Rechte benötigt")
    public boolean existsUser(@PathParam("user") final String username, @Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	final List<String> userlist = DTOInformationUser.queryNames(conn);
			return userlist.contains(username);
    	}
    }

    /**
     * Die OpenAPI-Methode für das angegebene Password für einen Datenbankuser korrekt ist.
     *
     * @param kennwort    der Username und das Kennwort im json, das überprüft werden soll
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return            Rückmeldung, ob das angegebene Kennwort korrekt ist
     */
	@POST
	@Path("/api/schema/root/user/checkpwd")
	@Operation(summary = "Prüft, ob das übergebene Kennwort für den Datenbankbenutzer gültig ist.",
		description = "Prüft, ob das übergebene Kennwort für den Datenbankbenutzer gültig ist. Zur Prüfung werden root-Rechte auf der Datenbank benötigt")
	@ApiResponse(responseCode = "200", description = "true, wenn das Kennwort und der Benutzername korrekt sind und den priviligierten Zugriff auf die Datenbankschema erlauben.",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
	@ApiResponse(responseCode = "403", description = "Der angegebene Benutzer besitzt nicht die Rechte, um die Schema-Liste der Datenbank auszulesen. Hierfür werden root-Rechte benötigt")
    public boolean checkDBPassword(@RequestBody(description = "Der Benutzername und das Kennwort für den Datenbankbenutzer", required = true, content =
			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BenutzerKennwort.class))) final BenutzerKennwort kennwort,
    		@Context final HttpServletRequest request) {
    	return DBUtilsPrivileged.checkDBPassword(kennwort);
    }



    /**
     * Die OpenAPI-Methode für das Anlegen eines Schemas mit angegebenem Namen und in der angegebenen Revision.
     *
     * @param schemaname    der Name des Schemas, das angelegt werden soll
     * @param revision      die Revisionsnummer, auf die das Schema angehoben werden soll
     * @param kennwort      Benutzername und Kennwort mit Root-Rechten
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              Rückmeldung, ob das angegebene Kennwort korrekt ist
     */
    @POST
    @Path("/api/schema/root/create/{schema}/{revision : \\d+}")
    @Operation(summary = "Erstellt ein neues Schema der angegebenen Revision und dem angegebenen Namen.",
		description = "Erstellt ein neues Schema der angegebenen Revision und dem angegebenen Namen, falls keine Schema mit dem angebenen Namen bereits existiert.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Anlegen des Schemas",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
	@ApiResponse(responseCode = "400", description = "Es wurde ein nicht erlaubter Schema-Name, Benutzername oder eine ungültige Revision angegeben.")
	@ApiResponse(responseCode = "403", description = "Der angemeldete Benutzer verfügt nicht über die notwendigen Rechte zum Anlegen eines Schemas.")
	@ApiResponse(responseCode = "404", description = "Die Schema-Datenbank konnte nicht geladen werden. Die Server-Konfiguration ist fehlerhaft.")
    @ApiResponse(responseCode = "500", description = "Der Datenbankzugriff auf das neue Schema mit dem neuen zugehörigen Admin-Benutzer ist fehlgeschlagen oder das SVWS-Schema mit der Revision konnte nicht angelegt werden.",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse createSchema(@PathParam("schema") final String schemaname,
    						 @PathParam("revision") final long revision,
    						 @RequestBody(description = "Der Benutzername und das Kennwort für den administrativen Zugang zum Schema", required = true, content =
    									@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BenutzerKennwort.class))) final BenutzerKennwort kennwort,
    						 @Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	final Logger logger = new Logger();
	    	final LogConsumerList log = new LogConsumerList();
	    	logger.addConsumer(log);
	    	logger.addConsumer(new LogConsumerConsole());

			final long max_revision = SchemaRevisionen.maxRevision.revision;
			long rev = revision;
			if (rev < 0)
				rev = max_revision;
			if (rev > max_revision)
				throw OperationError.BAD_REQUEST.exception(simpleResponse(false, log));

			final DBRootManager root_manager = DBRootManager.create(conn);
			if (root_manager == null)
				throw OperationError.FORBIDDEN.exception(simpleResponse(false, log));

			if ((DBRootManager.isReservedSchemaName(schemaname)) || DBRootManager.isReservedUserName(kennwort.user))
				throw OperationError.BAD_REQUEST.exception(simpleResponse(false, log));

			logger.logLn("Prüfe, ob das Schema bereits existiert...");
			logger.modifyIndent(2);
			if (root_manager.dbSchemaExists(schemaname)) {
				logger.logLn("Fehler: Schema ist bereits vorhanden und kann deswegen nicht neu angelegt werden!");
				return simpleResponse(false, log);
			}
			logger.logLn("ist noch nicht vorhanden");
			logger.modifyIndent(-2);

			final DBConfig dbconfig = new DBConfig(conn.getDBDriver(), conn.getDBLocation(), schemaname, conn.useDBLogin(), kennwort.user, kennwort.password, true, true, 0, 0);
			final boolean success = DBSchemaManager.createNewSchema(dbconfig, conn.getUser().getUsername(), conn.getUser().getPassword(), revision, logger);
			return simpleResponse(success, log);
    	}
    }



    /**
     * Die OpenAPI-Methode für das Anlegen eines Schemas mit angegebenem Namen und in der aktuellen Revision.
     *
     * @param schemaname    der Name des Schemas, das angelegt werden soll
     * @param kennwort      Benutzername und Kennwort mit Root-Rechten
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/create/{schema}")
    @Operation(summary = "Erstellt ein neues Schema der aktuellen Revision mit dem angegebenen Namen.",
               description = "Erstellt ein neues Schema der aktuellen Revision mit dem angegebenen Namen, falls keines mit dem angebenen Namen bereits existiert.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Anlegen des Schemas",
    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Es wurde ein nicht erlaubter Schema-Name oder Benutzername angegeben.")
	@ApiResponse(responseCode = "403", description = "Der angemeldete Benutzer verfügt nicht über die notwendigen Rechte zum Anlegen eines Schemas.")
    @ApiResponse(responseCode = "404", description = "Die Schema-Datenbank konnte nicht geladen werden. Die Server-Konfiguration ist fehlerhaft.")
    @ApiResponse(responseCode = "500", description = "Der Datenbankzugriff auf das neue Schema mit dem neuen zugehörigen Admin-Benutzer ist fehlgeschlagen oder das SVWS-Schema mit der aktuellen Revision konnte nicht angelegt werden.")
    public SimpleOperationResponse createSchemaCurrent(@PathParam("schema") final String schemaname,
    						        @RequestBody(description = "Der Benutzername und das Kennwort für den administrativen Zugang zum Schema", required = true, content =
    						    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BenutzerKennwort.class))) final BenutzerKennwort kennwort,
    						        @Context final HttpServletRequest request) {
    	return createSchema(schemaname, -1, kennwort, request);
    }



    /**
     * Die OpenAPI-Methode für das Löschen eines Schemas mit angegebenem Namen, wenn es existiert.
     *
     * @param schemaname    der Name des Schemas, das gelöscht werden soll
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/destroy/{schema}")
    @Operation(summary = "Entfernt das bestehende Schema mit dem angegebenen Namen.",
               description = "Entfernt das Schema mit dem angegebenen Namen, falls es existiert.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Löschen des Schemas",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class))))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht gelöscht werden.")
    @ApiResponse(responseCode = "404", description = "Das angegebene Schema wurde nicht gefunden.")
    public List<String> destroySchema(@PathParam("schema") final String schemaname, @Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	// Erzeuge einen Root-Manager zum Löschen des Schemas
			final DBRootManager root_manager = DBRootManager.create(conn);
			if (root_manager == null)
				throw new WebApplicationException(Status.FORBIDDEN.getStatusCode());

	    	// Prüfe ob das Schema existiert und lösche das Schema mit dem Root-Manager
			if (!root_manager.dropDBSchemaIfExists(schemaname))
				throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());

			// TODO logging
			return null;
    	}
    }



    /**
     * Die OpenAPI-Methode für das Migrieren einer MDB in ein Schema mit angegebenen Namen.
     *
     * @param multipart     Daten der MDB, MDB-Datenbankkennwort, DB-Username und Passwort
     * @param schemaname    Name des Schemas, in das hinein migriert werden soll
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/api/schema/root/migrate/mdb/{schema}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der Access-MDB-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
				 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    public SimpleOperationResponse migrateMDB2Schema(@PathParam("schema") final String schemaname,
    		@RequestBody(description = "Die MDB-Datei", required = true, content =
			@Content(mediaType = MediaType.MULTIPART_FORM_DATA)) @MultipartForm final DBMultipartBody multipart,
    		@Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	final Logger logger = new Logger();
	    	final LogConsumerList log = new LogConsumerList();
	    	logger.addConsumer(log);
	    	logger.addConsumer(new LogConsumerConsole());

	    	// Erstelle temporär eine MDB-Datei aus dem übergebenen Byte-Array
	    	final String mdbdirectory = SVWSKonfiguration.get().getTempPath();
	        final String mdbFilename = schemaname +  "_" + random.ints(48, 123)  // from 0 to z
	          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // filter some unicode characters
	          .limit(40)
	          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	          .toString() + ".mdb";
	        logger.logLn("Erstelle eine temporäre Access-Datenbank unter dem Namen \"" + mdbdirectory + "/" + mdbFilename + "\"");
	    	try {
	    		Files.createDirectories(Paths.get(mdbdirectory));
				Files.write(Paths.get(mdbdirectory + "/" + mdbFilename), multipart.database, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
			} catch (@SuppressWarnings("unused") final IOException e) {
				logger.logLn(2, "Fehler beim Erstellen der temporären Access-Datenbank unter dem Namen \"" + mdbdirectory + "/" + mdbFilename + "\"");
				throw OperationError.INTERNAL_SERVER_ERROR.exception(simpleResponse(false, log));
			}

	    	logger.logLn("Migriere in die " + conn.getDBDriver() + "-Datenbank unter " + conn.getDBLocation() + ":");
	    	logger.logLn(2, "- verwende den root-benutzer: " + conn.getUser().getUsername());
	    	logger.logLn(2, "- erstelle das DB-Schema: " + schemaname);
	    	logger.logLn(2, "- erstelle den Benutzer \"" + multipart.schemaUsername + "\" für den administrativen Zugriff auf das DB-Schema.");

			final DBConfig srcConfig = new DBConfig(DBDriver.MDB, mdbdirectory + "/" + mdbFilename, "PUBLIC", false, "admin", multipart.databasePassword, true, false, 0, 0);
			final DBConfig tgtConfig = new DBConfig(conn.getDBDriver(), conn.getDBLocation(), schemaname, false, multipart.schemaUsername, multipart.schemaUserPassword, true, true, 0, 0);
			if (!DBMigrationManager.migrate(srcConfig, tgtConfig, conn.getUser().getUsername(), conn.getUser().getPassword(), -1, false, null, logger)) {
				logger.logLn(LogLevel.ERROR, 2, "Fehler bei der Migration (driver='" + tgtConfig.getDBDriver() + "', location='" + tgtConfig.getDBLocation() + "', user='" + tgtConfig.getUsername() + "')");
				throw OperationError.INTERNAL_SERVER_ERROR.exception(simpleResponse(false, log));
			}

			// Entferne die temporär angelegte Datenbank wieder...
			logger.logLn("Löschen der temporären Access-Datenbank unter dem Namen \"" + mdbdirectory + "/" + mdbFilename + "\".");
			try {
				Files.delete(Paths.get(mdbdirectory + "/" + mdbFilename));
			} catch (@SuppressWarnings("unused") final IOException e) {
				logger.logLn(2, "[FEHLER]");
			}

			logger.logLn("Migration abgeschlossen.");
			return simpleResponse(true, log);
    	}
    }


    /**
     * Die OpenAPI-Methode für den Import einer SQLite-Datenbank in ein Schema mit dem angegebenen Namen.
     *
     * @param multipart     SQLite-Datenbank im Binärformat, DB-Username und Passwort für das neue Schema
     * @param schemaname    Name des Schemas, in das hinein migriert werden soll
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return Rückmeldung, ob die Operation erfolgreich war mit dem Log der Operation
     */
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/api/schema/root/import/sqlite/{schema}")
    @Operation(summary = "Importiert die übergebene SQLite-Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Importiert die übergebene SQLite-Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Import der SQLite-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht importiert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei dem Import der SQLite-Datenbank mit dem Log des fehlgeschlagenen Imports.",
	 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse importSQLite2Schema(@PathParam("schema") final String schemaname,
    		@RequestBody(description = "Die SQLite-Datei", required = true, content =
			@Content(mediaType = MediaType.MULTIPART_FORM_DATA)) @MultipartForm final DBMultipartBodyWithoutDBPassword multipart,
    		@Context final HttpServletRequest request) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	final Logger logger = new Logger();
	    	final LogConsumerList log = new LogConsumerList();
	    	logger.addConsumer(log);
	    	logger.addConsumer(new LogConsumerConsole());

	    	// Erstelle temporär eine SQLite-Datei aus dem übergebenen Byte-Array
	    	final String tmpDirectory = SVWSKonfiguration.get().getTempPath();
	        final String tmpFilename = schemaname +  "_" + random.ints(48, 123)  // from 0 to z
	          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // filter some unicode characters
	          .limit(40)
	          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	          .toString() + ".sqlite";
	        logger.logLn("Erstelle eine SQLite-Datenbank unter dem Namen \"" + tmpDirectory + "/" + tmpFilename + "\"");
	    	try {
	    		Files.createDirectories(Paths.get(tmpDirectory));
				Files.write(Paths.get(tmpDirectory + "/" + tmpFilename), multipart.database, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
			} catch (@SuppressWarnings("unused") final IOException e) {
				logger.logLn(2, "Fehler beim Erstellen der temporären SQLite-Datenbank unter dem Namen \"" + tmpDirectory + "/" + tmpFilename + "\"");
				throw OperationError.INTERNAL_SERVER_ERROR.exception(simpleResponse(false, log));
			}

	    	logger.logLn("Importiere in die " + conn.getDBDriver() + "-Datenbank unter " + conn.getDBLocation() + ":");
	    	logger.logLn(2, "- verwende den root-benutzer: " + conn.getUser().getUsername());
	    	logger.logLn(2, "- erstelle das DB-Schema: " + schemaname);
	    	logger.logLn(2, "- erstelle den Benutzer \"" + multipart.schemaUsername + "\" für den administrativen Zugriff auf das DB-Schema.");

			final int maxUpdateRevision = 3;
			final DBConfig srcConfig = new DBConfig(DBDriver.SQLITE, tmpDirectory + "/" + tmpFilename, null, false, null, null, true, false, 0, 0);
			final DBConfig tgtConfig = new DBConfig(conn.getDBDriver(), conn.getDBLocation(), schemaname, false, multipart.schemaUsername, multipart.schemaUserPassword, true, true, 0, 0);

			final Benutzer srcUser = Benutzer.create(srcConfig);
			try (DBEntityManager srcConn = srcUser.getEntityManager()) {
				if (srcConn == null) {
					logger.logLn(0, " [Fehler]");
					throw new DBException("Fehler beim Verbinden zur SQLite-Export-Datenbank");
				}
				logger.logLn(0, " [OK]");

				final DBSchemaManager srcManager = DBSchemaManager.create(srcUser, true, logger);
				logger.modifyIndent(2);
				if (!srcManager.backup.importDB(tgtConfig, conn.getUser().getUsername(), conn.getUser().getPassword(), maxUpdateRevision, false, logger))
					throw OperationError.INTERNAL_SERVER_ERROR.exception(simpleResponse(false, log));
				logger.modifyIndent(-2);
			} catch (@SuppressWarnings("unused") final DBException e) {
				throw OperationError.INTERNAL_SERVER_ERROR.exception(simpleResponse(false, log));
			}

			// Entferne die temporär angelegte Datenbank wieder...
			logger.logLn("Löschen der temporären SQLite-Datenbank unter dem Namen \"" + tmpDirectory + "/" + tmpFilename + "\".");
			try {
				Files.delete(Paths.get(tmpDirectory + "/" + tmpFilename));
			} catch (@SuppressWarnings("unused") final IOException e) {
				logger.logLn(2, "[FEHLER]");
			}

			logger.logLn("Import abgeschlossen.");
			return simpleResponse(true, log);
    	}
    }


    /**
     * Die OpenAPI-Methode für das Migrieren einer bestehenden MariaDB in ein Schema mit angegebenen Namen.
     *
     * @param dbMigrationInfos   Zugangsdaten zur MariaDB, Name des Schema, das angelegt werden soll, Schmea-Username und Passwort
     * @param schemaname         Name des Schemas, in das hinein migriert werden soll
     * @param request            die Informationen zur HTTP-Anfrage
     *
     * @return                   Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/migrate/mariadb/{schema}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der MariaDB-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
	 			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse migrateMariaDB2Schema(@PathParam("schema") final String schemaname,
    		@RequestBody(description = "Die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration", required = true, content =
    				@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MigrateBody.class))) final MigrateBody dbMigrationInfos,
    		@Context final HttpServletRequest request) {
    	return migrate2Schema(schemaname, DBDriver.MARIA_DB, dbMigrationInfos, request, null);
    }


    /**
     * Die OpenAPI-Methode für das Migrieren aus einer Schild2-MariaDB-Datenbank
     * in ein Schema mit angegebenen Namen, wobei nur Daten für die angegebene Schulnummer
     * übertragen werden.
     *
     * @param dbMigrationInfos   Zugangsdaten zur MariaDB, Name des Schema, das angelegt werden soll, Schmea-Username und Passwort
     * @param schemaname         Name des Schemas, in das hinein migriert werden soll
     * @param request            die Informationen zur HTTP-Anfrage
     * @param schulnummer        die Schulnummer, für die die Migration durchgeführt wird.
     *
     * @return                   Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/migrate/mariadb/{schema}/{schulnummer:\\d{6}}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der MariaDB-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
	 			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse migrateMariaDB2SchemaSchulnummer(@PathParam("schema") final String schemaname, @PathParam("schulnummer") final int schulnummer,
    		@RequestBody(description = "Die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration", required = true, content =
    				@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MigrateBody.class))) final MigrateBody dbMigrationInfos,
    		@Context final HttpServletRequest request) {
    	return migrate2Schema(schemaname, DBDriver.MARIA_DB, dbMigrationInfos, request, schulnummer);
    }


    /**
     * Die OpenAPI-Methode für das Migrieren einer bestehenden MySQL in ein Schema mit angegebenen Namen.
     *
     * @param dbMigrationInfos   Zugangsdaten zur MySQL, Name des Schema, das angelegt werden soll, Schmea-Username und Passwort
     * @param schemaname         Name des Schemas, in das hinein migriert werden soll
     * @param request            die Informationen zur HTTP-Anfrage
     *
     * @return                   Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/migrate/mysql/{schema}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der MySQL-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
	 			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse migrateMySQL2Schema(@PathParam("schema") final String schemaname,
    		@RequestBody(description = "Die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration", required = true, content =
    				@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MigrateBody.class))) final MigrateBody dbMigrationInfos,
    		@Context final HttpServletRequest request) {
    	return migrate2Schema(schemaname, DBDriver.MYSQL, dbMigrationInfos, request, null);
    }


    /**
     * Die OpenAPI-Methode für das Migrieren aus einer Schild2-MySQL-Datenbank
     * in ein Schema mit angegebenen Namen, wobei nur Daten für die angegebene Schulnummer
     * übertragen werden.
     *
     * @param dbMigrationInfos   Zugangsdaten zur MySQL, Name des Schema, das angelegt werden soll, Schmea-Username und Passwort
     * @param schemaname         Name des Schemas, in das hinein migriert werden soll
     * @param request            die Informationen zur HTTP-Anfrage
     * @param schulnummer        die Schulnummer, für die die Migration durchgeführt wird.
     *
     * @return                   Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/migrate/mysql/{schema}/{schulnummer:\\d{6}}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der MySQL-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
	 			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse migrateMySQL2SchemaSchulnummer(@PathParam("schema") final String schemaname, @PathParam("schulnummer") final int schulnummer,
    		@RequestBody(description = "Die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration", required = true, content =
    				@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MigrateBody.class))) final MigrateBody dbMigrationInfos,
    		@Context final HttpServletRequest request) {
    	return migrate2Schema(schemaname, DBDriver.MYSQL, dbMigrationInfos, request, schulnummer);
    }


    /**
     * Die OpenAPI-Methode für das Migrieren einer bestehenden MSSQL in ein Schema mit angegebenen Namen.
     *
     * @param dbMigrationInfos   Zugangsdaten zur MSSQL, Name des Schema, das angelegt werden soll, Schmea-Username und Passwort
     * @param schemaname         Name des Schemas, in das hinein migriert werden soll
     * @param request            die Informationen zur HTTP-Anfrage
     *
     * @return                   Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/migrate/mssql/{schema}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der MS-SQL-Server-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
	 			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse migrateMSSQL2Schema(@PathParam("schema") final String schemaname,
    		@RequestBody(description = "Die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration", required = true, content =
    				@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MigrateBody.class))) final MigrateBody dbMigrationInfos,
    		@Context final HttpServletRequest request) {
    	return migrate2Schema(schemaname, DBDriver.MSSQL, dbMigrationInfos, request, null);
    }



    /**
     * Die OpenAPI-Methode für das Migrieren aus einer Schild2-MSSQL-Datenbank
     * in ein Schema mit angegebenen Namen, wobei nur Daten für die angegebene Schulnummer
     * übertragen werden.
     *
     * @param dbMigrationInfos   Zugangsdaten zur MSSQL, Name des Schema, das angelegt werden soll, Schmea-Username und Passwort
     * @param schemaname         Name des Schemas, in das hinein migriert werden soll
     * @param request            die Informationen zur HTTP-Anfrage
     * @param schulnummer        die Schulnummer, für die die Migration durchgeführt wird.
     *
     * @return                   Rückmeldung, ob die Operation erfolgreich war
     */
    @POST
    @Path("/api/schema/root/migrate/mssql/{schema}/{schulnummer:\\d{6}}")
    @Operation(summary = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen.",
               description = "Migriert die übergebene Datenbank in das Schema mit dem angegebenen Namen. Sollte "
               		       + "ein Schema mit dem Namen bereits bestehen, so wird es ersetzt.")
    @ApiResponse(responseCode = "200", description = "Der Log vom Migrieren der MS-SQL-Server-Datenbank",
    			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Das Schema darf nicht migriert werden.")
    @ApiResponse(responseCode = "500", description = "Fehler bei der Migration mit dem Log der fehlgeschlagenen Migration.",
	 			 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    public SimpleOperationResponse migrateMSSQL2SchemaSchulnummer(@PathParam("schema") final String schemaname, @PathParam("schulnummer") final int schulnummer,
    		@RequestBody(description = "Die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration", required = true, content =
    				@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MigrateBody.class))) final MigrateBody dbMigrationInfos,
    		@Context final HttpServletRequest request) {
    	return migrate2Schema(schemaname, DBDriver.MSSQL, dbMigrationInfos, request, schulnummer);
    }



    /**
     * Führt eine Migration in das angegebene Ziel-Schema mit den übergebenen Migrations-Informtionen durch.
     *
     * @param schemaname         der Name des Ziel-Schemas, in welches migriert wird
     * @param srcDbDriver        das DBMS der Quell-Datenbank
     * @param dbMigrationInfos   die Informationen zum Zugriff auf die Quell- und Zieldatenbank bei der Migration
     * @param request            der HTTP-Request
     * @param schulnummer        die Schulnummer, für die die Migration durchgeführt wird oder null, falls keine Filterung bezüglich
     *                           der Schulnummer erfolgen soll
     *
     * @return die Antwort auf die Migrationsanfrage, mit der Information, ob die Migration erfolgreich war oder nicht und dem Log zur Migration
     */
	private static SimpleOperationResponse migrate2Schema(final String schemaname, final DBDriver srcDbDriver, final MigrateBody dbMigrationInfos, @Context final HttpServletRequest request, final Integer schulnummer) {
    	try (DBEntityManager conn = DBBenutzerUtils.getDBConnection(request, ServerMode.STABLE, BenutzerKompetenz.KEINE)) {
	    	final Logger logger = new Logger();
	    	final LogConsumerList log = new LogConsumerList();
	    	logger.addConsumer(log);
	    	logger.addConsumer(new LogConsumerConsole());

	    	// Prüfe das angegebene Datenbanksystem für die Quelldatenbank
	    	switch (srcDbDriver) {
				case MARIA_DB, MSSQL, MYSQL:
					logger.logLn("Es wird aus dem Datenbankformat '" + srcDbDriver + "' migriert.");
					break;
				case MDB, SQLITE:
				default:
					logger.logLn("Eine Migration aus dem angegebenen Datenbankformat '" + srcDbDriver + "' wird über diese Schnittstelle nicht unterstützt.");
					return simpleResponse(false, log);
	    	}

	    	logger.logLn("Migriere in die " + conn.getDBDriver() + "-Datenbank unter " + conn.getDBLocation() + ":");
	    	logger.logLn(2, "- verwende den root-benutzer: " + conn.getUser().getUsername());
	    	logger.logLn(2, "- erstelle das DB-Schema: " + schemaname);
	    	logger.logLn(2, "- erstelle den Benutzer \"" + dbMigrationInfos.schemaUsername + "\" für den administrativen Zugriff auf das DB-Schema.");

			final DBConfig srcConfig = new DBConfig(srcDbDriver, dbMigrationInfos.srcLocation, dbMigrationInfos.srcSchema, false, dbMigrationInfos.srcUsername, dbMigrationInfos.srcPassword, true, false, 0, 0);
			final DBConfig tgtConfig = new DBConfig(conn.getDBDriver(), conn.getDBLocation(), schemaname, false, dbMigrationInfos.schemaUsername, dbMigrationInfos.schemaUserPassword, true, true, 0, 0);
			if (!DBMigrationManager.migrate(srcConfig, tgtConfig, conn.getUser().getUsername(), conn.getUser().getPassword(), -1, false, schulnummer, logger)) {
				logger.logLn(LogLevel.ERROR, 2, "Fehler bei der Migration (driver='" + tgtConfig.getDBDriver() + "', location='" + tgtConfig.getDBLocation() + "', user='" + tgtConfig.getUsername() + "')");
				throw OperationError.INTERNAL_SERVER_ERROR.exception(simpleResponse(false, log));
			}

			logger.logLn("Migration abgeschlossen.");
			return simpleResponse(true, log);
    	}
    }


}