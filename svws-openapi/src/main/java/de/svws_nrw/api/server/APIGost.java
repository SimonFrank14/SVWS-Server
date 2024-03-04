package de.svws_nrw.api.server;

import de.svws_nrw.core.abschluss.gost.AbiturdatenManager;
import de.svws_nrw.core.abschluss.gost.GostBelegpruefungErgebnis;
import de.svws_nrw.core.abschluss.gost.GostBelegpruefungsArt;
import de.svws_nrw.core.data.SimpleOperationResponse;
import de.svws_nrw.core.data.gost.Abiturdaten;
import de.svws_nrw.core.data.gost.GostBelegpruefungsErgebnisse;
import de.svws_nrw.core.data.gost.GostBeratungslehrer;
import de.svws_nrw.core.data.gost.GostFach;
import de.svws_nrw.core.data.gost.GostJahrgang;
import de.svws_nrw.core.data.gost.GostJahrgangFachkombination;
import de.svws_nrw.core.data.gost.GostJahrgangFachwahlen;
import de.svws_nrw.core.data.gost.GostJahrgangFachwahlenHalbjahr;
import de.svws_nrw.core.data.gost.GostJahrgangsdaten;
import de.svws_nrw.core.data.gost.GostLaufbahnplanungBeratungsdaten;
import de.svws_nrw.core.data.gost.GostLaufbahnplanungDaten;
import de.svws_nrw.core.data.gost.GostLeistungen;
import de.svws_nrw.core.data.gost.GostSchuelerFachwahl;
import de.svws_nrw.core.data.gost.GostStatistikFachwahl;
import de.svws_nrw.core.data.schueler.SchuelerListeEintrag;
import de.svws_nrw.core.types.ServerMode;
import de.svws_nrw.core.types.benutzer.BenutzerKompetenz;
import de.svws_nrw.core.utils.gost.GostFaecherManager;
import de.svws_nrw.data.MultipleBinaryMultipartBody;
import de.svws_nrw.data.SimpleBinaryMultipartBody;
import de.svws_nrw.data.benutzer.DBBenutzerUtils;
import de.svws_nrw.data.faecher.DBUtilsFaecherGost;
import de.svws_nrw.data.gost.DBUtilsGost;
import de.svws_nrw.data.gost.DBUtilsGostAbitur;
import de.svws_nrw.data.gost.DataGostAbiturjahrgangFachwahlen;
import de.svws_nrw.data.gost.DataGostBeratungslehrer;
import de.svws_nrw.data.gost.DataGostFaecher;
import de.svws_nrw.data.gost.DataGostJahrgangFachkombinationen;
import de.svws_nrw.data.gost.DataGostJahrgangLaufbahnplanung;
import de.svws_nrw.data.gost.DataGostJahrgangSchuelerliste;
import de.svws_nrw.data.gost.DataGostJahrgangsdaten;
import de.svws_nrw.data.gost.DataGostJahrgangsliste;
import de.svws_nrw.data.gost.DataGostSchuelerLaufbahnplanung;
import de.svws_nrw.data.gost.DataGostSchuelerLaufbahnplanungBeratungsdaten;
import de.svws_nrw.data.schule.SchulUtils;
import de.svws_nrw.db.dto.current.schild.schule.DTOEigeneSchule;
import de.svws_nrw.db.utils.OperationError;
import de.svws_nrw.module.reporting.pdf.base.PdfFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse spezifiziert die OpenAPI-Schnittstelle für den Zugriff auf die SVWS-Datenbank in Bezug auf die gymnasiale Oberstufe.
 * Ein Zugriff erfolgt über den Pfad https://{hostname}/db/{schema}/gost/.
 */
@Path("/db/{schema}/gost")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Server")
public class APIGost {


	/**
	 * Liefert eine Liste aller Abiturjahrgänge, welche in der Datenbank für die Laufbahnplanung angelegt sind.
	 * Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen von Kataloginformationen
	 * besitzt.
	 *
	 * @param schema   das Schema aus dem die Leistungsdaten des Schülers kommen sollen
	 *
	 * @param request  die Informationen zur HTTP-Anfrage
	 *
	 * @return eine Liste der Abiturjahrgänge
	 */
	@GET
	@Path("/abiturjahrgaenge")
	@Operation(summary = "Liefert eine Liste aller Abiturjahrgänge, welche in der Datenbank für die Laufbahnplanung angelegt sind.",
	           description = "Liefert eine Liste aller Abiturjahrgänge, welche in der Datenbank für die Laufbahnplanung angelegt sind."
	           		       + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen von Kataloginformationen "
	           		       + "besitzt.")
    @ApiResponse(responseCode = "200",
    			 description = "Die Liste der Abiturjahrgänge.",
    			 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GostJahrgang.class))))
	@ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Liste der Abiturjahrgänge auszulesen.")
	@ApiResponse(responseCode = "404", description = "Kein Abiturjahrgang gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
	public Response getGostAbiturjahrgaenge(@PathParam("schema") final String schema, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangsliste(conn).getAll(),
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.KEINE);
	}


    /**
     * Die OpenAPI-Methode für das Erstellen eines neuen Abiturjahrgangs der gymnasialen Oberstufe.
     *
     * @param schema       das Datenbankschema, in welchem die Blockung erstellt wird
     * @param jahrgangID   die ID des Jahrgangs, für welchen der Abitur-Jahrgang erstellt werden soll
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort mit dem neu angelegten Abiturjahr
     */
    @POST
    @Path("/abiturjahrgang/new/{jahrgangid}")
    @Operation(summary = "Erstellt einen neuen Abiturjahrgang und gibt das Abiturjahr zurück.",
    description = "Erstellt einen neuen Abiturjahrgang und gibt das Abiturjahr zurück."
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen eine Abiturjahrgangs "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Der Abiturjahrgang wurde erfolgreich angelegt.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Integer.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen Abiturjahrgang anzulegen.")
    @ApiResponse(responseCode = "404", description = "Keine Daten beim angegebenen Jahrgang gefunden, um einen Abiturjahrgang anzulegen")
    @ApiResponse(responseCode = "409", description = "Der Abiturjahrgang existiert bereits")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response createGostAbiturjahrgang(@PathParam("schema") final String schema, @PathParam("jahrgangid") final long jahrgangID, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangsliste(conn).create(jahrgangID),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }


    /**
     * Die OpenAPI-Methode für das Löschen eines Abiturjahrgangs der gymnasialen Oberstufe,
     * sofern er nicht abgeschlossen ist oder bereits Leistungsdaten bei Schülern eingetragen
     * sind.
     *
     * @param schema       das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr   das Abiturjahr
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort mit dem Status und ggf. der gelöschten Regel zur Fachkombination
     */
    @DELETE
    @Path("/abiturjahrgang/{abiturjahr : \\d+}")
    @Operation(summary = "Entfernt den Abiturjahrgang, sofern er nicht abgeschlossen ist oder bereits Leistungsdaten bei Schülern eingetragen sind.",
               description = "Entfernt den Abiturjahrgang, sofern er nicht abgeschlossen ist oder bereits Leistungsdaten bei Schülern eingetragen sind. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Entfernen besitzt.")
    @ApiResponse(responseCode = "204", description = "Der Abiturjahrgang wurde wurde erfolgreich entfernt.")
    @ApiResponse(responseCode = "400", description = "Es wurde versucht einen Abiturjahrgang, der abgeschlossen ist oder für den bereits Leistungsdaten bei Schülern eingetragen sind, zu entfernen.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um den Abiturjahrgang zu entfernen.")
    @ApiResponse(responseCode = "404", description = "Der Abiturjahrgang wurde nicht gefunden.")
    public Response deleteGostAbiturjahrgang(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangsliste(conn).delete(abiturjahr),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Daten eines Jahrgangs der gymnasialen Oberstufe.
     *
     * @param schema      das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr  das Jahr, in welchem der Jahrgang Abitur machen wird
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return die Daten des Jahrganges
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}")
    @Operation(summary = "Liefert zu dem Jahr, in welchem der Jahrgang Abitur machen wird, die zugehörigen Grunddaten.",
    description = "Liest die Grunddaten des Jahrgangs der gymnasialen Oberstufe zu dem Jahr, in welchem der Jahrgang Abitur "
    		    + "machen wird, aus der Datenbank und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Jahrgangsinformationen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Grunddaten des Jahrgangs der gymnasialen Oberstufe",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostJahrgangsdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Grunddaten der Gymnasialen Oberstufe anzusehen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für einen Jahrgang der gymnasialen Oberstufe mit dem angegebenen Abiturjahr gefunden")
    public Response getGostAbiturjahrgang(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
    		                                    @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangsdaten(conn).get(abiturjahr),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.KEINE);
    }



    /**
     * Die OpenAPI-Methode für das Anpassen der Daten eines Jahrgangs der gymnasialen Oberstufe.
     *
     * @param schema    das Datenbankschema, auf welches der Patch ausgeführt werden soll
     * @param abiturjahr  das Jahr, in welchem der Jahrgang Abitur machen wird
     * @param is        der InputStream, mit dem JSON-Patch-Objekt nach RFC 7386
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @PATCH
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}")
    @Operation(summary = "Passt die Daten des Abiturjahrgangs der Gymnasialen Oberstufe an.",
    description = "Passt die Daten des Jahrganges der gymnasialen Oberstufe zu dem Jahr an, in welchem der Jahrgang Abitur "
    		    + "machen wird. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Anpassen der Jahrgangsinformationen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Der Patch wurde erfolgreich in die Jahrgangsdaten integriert.")
    @ApiResponse(responseCode = "400", description = "Der Patch ist fehlerhaft aufgebaut.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Jahrgangsdaten zu ändern.")
    @ApiResponse(responseCode = "404", description = "Kein Abiturjahrgangs-Eintrag mit der angegebenen ID gefunden")
    @ApiResponse(responseCode = "409", description = "Der Patch ist fehlerhaft, da zumindest eine Rahmenbedingung für einen Wert nicht erfüllt wurde")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response patchGostAbiturjahrgang(
    		@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
    		@RequestBody(description = "Der Patch für die Abiturjahrgangsdaten", required = true, content =
    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GostJahrgangsdaten.class))) final InputStream is,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangsdaten(conn).patch(abiturjahr, is),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.ABITUR_AENDERN_ALLGEMEIN,
    			BenutzerKompetenz.ABITUR_AENDERN_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Hinzufügen eines Beratungslehrers zu einem Abiturjahrgang.
     *
     * @param schema       das Datenbankschema
     * @param abiturjahr   das Jahr, in welchem der Jahrgang Abitur machen wird
     * @param idLehrer     die ID des Lehrers, der hinzugefügt werden soll
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort mit dem Beratungslehrer
     */
    @POST
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/beratungslehrer/add")
    @Operation(summary = "Fügt einen Lehrer als Beratungslehrer zu einem Abiturjahrgang der Gymnasialen Oberstufe hinzu.",
               description = "Fügt einen Lehrer als Beratungslehrer zu einem Abiturjahrgang der Gymnasialen Oberstufe hinzu."
    		               + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Hinzufügen eines Beratungslehrers hat.")
    @ApiResponse(responseCode = "200", description = "Der hinzugefügte Beratungslehrer",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = GostBeratungslehrer.class)))
    @ApiResponse(responseCode = "400", description = "Der Lehrer ist bereits als Beratungslehrer eingetragen.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen Beratungslehrer hinzuzufügen.")
    @ApiResponse(responseCode = "404", description = "Der Abiturjahrgang oder der Lehrer ist nicht vorhanden")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response addGostAbiturjahrgangBeratungslehrer(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
            @RequestBody(description = "Die ID des Lehrers", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Long.class))) final long idLehrer,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostBeratungslehrer(conn, abiturjahr).add(idLehrer),
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Erntfernen eines Beratungslehrers aus einem Abiturjahrgang.
     *
     * @param schema       das Datenbankschema
     * @param abiturjahr   das Jahr, in welchem der Jahrgang Abitur machen wird
     * @param idLehrer     die ID des Lehrers, der entfernt werden soll
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort mit dem entfernten Beratungslehrer
     */
    @POST
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/beratungslehrer/remove")
    @Operation(summary = "Entfernt einen Lehrer als Beratungslehrer aus einem Abiturjahrgang der Gymnasialen Oberstufe.",
               description = "Entfernt einen Lehrer als Beratungslehrer aus einem Abiturjahrgang der Gymnasialen Oberstufe."
    		               + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Entfernen eines Beratungslehrers hat.")
    @ApiResponse(responseCode = "200", description = "Der entfernte Beratungslehrer",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = GostBeratungslehrer.class)))
    @ApiResponse(responseCode = "400", description = "Der Lehrer ist nicht als Beratungslehrer eingetragen.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um einen Beratungslehrer zu entfernen.")
    @ApiResponse(responseCode = "404", description = "Der Abiturjahrgang oder der Lehrer ist nicht vorhanden")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response removeGostAbiturjahrgangBeratungslehrer(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
            @RequestBody(description = "Die ID des Lehrers", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = Long.class))) final long idLehrer,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostBeratungslehrer(conn, abiturjahr).remove(idLehrer),
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Liste der Schüler der gymnasialen Oberstufe
     * im angegebenen Schema für den angegebenen Abitur-Jahrgang.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Liste der Schüler - allerdings ohne Informationen zu Kursen
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/schueler")
    @Operation(summary = "Gibt eine Übersicht von allen Schülern des Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Erstellt eine Liste aller Schüler, welche zu dem angegebenen Abitur-Jahrgang gehören (ohne Informationen zu Kursen). "
               		       + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Schülerdaten "
               		       + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Eine Liste von Schüler-Listen-Einträgen",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SchuelerListeEintrag.class))))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Schülerdaten anzusehen.")
    @ApiResponse(responseCode = "404", description = "Keine Schüler gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangSchueler(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		if (abiturjahr < 0)
		    			return OperationError.NOT_FOUND.getResponse("Schüler können dem Vorlagen-Abiturjahrgang nicht zugewiesen sein.");
		    		return (new DataGostJahrgangSchuelerliste(conn, abiturjahr)).getList();
    			},
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Liste der Fächer der gymnasialen Oberstufe
     * im angegebenen Schema für den angegebenen Abitur-Jahrgang.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Liste der Fächer der gymnasialen Oberstufe mit der ID des Faches
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/faecher")
    @Operation(summary = "Gibt eine Übersicht von allen Fächern des Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Erstellt eine Liste aller in der Datenbank vorhanden Fächer der gymnasialen Oberstufe, "
                           + "welche für den angegebenen Abitur-Jahrgang festgelegt wurden.. "
               		       + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Fächerdaten "
               		       + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Eine Liste von Fächer-Listen-Einträgen",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GostFach.class))))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fächerdaten anzusehen.")
    @ApiResponse(responseCode = "404", description = "Keine Fächer-Einträge gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangFaecher(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostFaecher(conn, abiturjahr).getList(),
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.KEINE);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Daten eines Faches in Bezug auf die gymnasiale Oberstufe
     * und in Bezug auf einen Abiturjahrgang.
     *
     * @param schema      das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr  der Abitur-Jahrgang
     * @param id          die ID des Faches
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return die Abiturjahrgangs-spezifischen Daten des Faches der gymnasialen Oberstufe
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fach/{id : \\d+}")
    @Operation(summary = "Liefert die Informationen bezüglich eines Abiturjahrgangs der gymnasialen Oberstufe zu dem Fach mit der angegebenen ID.",
    description = "Liefert die Informationen bezüglich eines Abiturjahrgangs der gymnasialen Oberstufe zu dem Fach mit der angegebenen ID. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Fachinformationen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Daten des Faches bezüglich eines Abiturjahrgangs der gymnasialen Oberstufe",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostFach.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachinformationen der Gymnasialen Oberstufe anzusehen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für das Fach der gymnasialen Oberstufe mit der angegebenen ID gefunden")
    public Response getGostAbiturjahrgangFach(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostFaecher(conn, abiturjahr).get(id),
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.KEINE);
    }



    /**
     * Die OpenAPI-Methode für das Anpassen der Daten eines Faches in Bezug auf den
     * Abiturjahrgang der gymnasialen Oberstufe.
     *
     * @param schema       das Datenbankschema, auf welches der Patch ausgeführt werden soll
     * @param abiturjahr   der Abitur-Jahrgang
     * @param id           die ID des Faches
     * @param is           der InputStream, mit dem JSON-Patch-Objekt nach RFC 7386
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @PATCH
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fach/{id : \\d+}")
    @Operation(summary = "Passt die Daten eines Faches in Bezug den Abiturjahrgang der Gymnasiale Oberstufe an.",
    description = "Passt die Daten eines Faches in Bezug auf den Abiturjahrgang der Gymnasiale Oberstufe an. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Anpassen der Fachinformationen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Der Patch wurde erfolgreich in die Fachinformationen integriert.")
    @ApiResponse(responseCode = "400", description = "Der Patch ist fehlerhaft aufgebaut.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachdaten zu ändern.")
    @ApiResponse(responseCode = "404", description = "Kein Fach-Eintrag mit der angegebenen ID gefunden")
    @ApiResponse(responseCode = "409", description = "Der Patch ist fehlerhaft, da zumindest eine Rahmenbedingung für einen Wert nicht erfüllt wurde")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response patchGostAbiturjahrgangFach(
    		@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @PathParam("id") final long id,
    		@RequestBody(description = "Der Patch für die Fachdaten", required = true, content =
    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GostFach.class))) final InputStream is,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostFaecher(conn, abiturjahr).patch(id, is),
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.KATALOG_EINTRAEGE_AENDERN);
    }



    /**
     * Die OpenAPI-Methode für die Abfrage der Fachwahlstatistik für den angegebenen Abitur-Jahrgang der
     * gymnasialen Oberstufe im angegebenen Schema.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Fachwahlstatistik
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachwahlstatistik")
    @Operation(summary = "Gibt eine Übersicht von allen Fachwahlen des Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Erstellt eine Liste aller in der Datenbank für den angegebenen Abitur-Jahrgang vorhanden "
               		       + "Fachwahlen der gymnasialen Oberstufe. "
               		       + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Fächerdaten "
               		       + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Eine Fachwahlstatistik",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GostStatistikFachwahl.class))))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachwahlen anzusehen.")
    @ApiResponse(responseCode = "404", description = "Keine Fachwahlen gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangFachwahlstatistik(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		if (abiturjahr < 0)
		    			return OperationError.NOT_FOUND.getResponse("Fachwahlen sind für den Vorlagen-Abiturjahrgang nicht verfügbar.");
		    		return (new DataGostAbiturjahrgangFachwahlen(conn, abiturjahr)).getList();
    			},
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }



    /**
     * Die OpenAPI-Methode für die Abfrage aller Fachwahlen für den angegebenen Abitur-Jahrgang der
     * gymnasialen Oberstufe im angegebenen Schema.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Fachwahlen der gymnasialen Oberstufe
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachwahlen")
    @Operation(summary = "Gibt eine Übersicht von allen Fachwahlen des Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Erstellt eine Liste aller in der Datenbank für den angegebenen Abitur-Jahrgang vorhanden "
                           + "Fachwahlen der gymnasialen Oberstufe. "
                           + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Fächerdaten "
                           + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Eine Liste der Fachwahlen",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = GostJahrgangFachwahlen.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachwahlen anzusehen.")
    @ApiResponse(responseCode = "404", description = "Keine Fachwahlen gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangFachwahlen(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
            @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		if (abiturjahr < 0)
		    			return OperationError.NOT_FOUND.getResponse("Fachwahlen sind für den Vorlagen-Abiturjahrgang nicht verfügbar.");
		            return (new DataGostAbiturjahrgangFachwahlen(conn, abiturjahr)).getSchuelerFachwahlenResponse();
    			},
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Fachwahlen für den angegebenen Abitur-Jahrgang der
     * gymnasialen Oberstufe im angegebenen Schema.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param halbjahr      das Halbjahr, für welches die Fachwahlen ermittelt werden sollen.
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Liste der Fachwahlen der gymnasialen Oberstufe
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/halbjahr/{halbjahr : \\d+}/fachwahlen")
    @Operation(summary = "Gibt eine Übersicht von allen Fachwahlen des Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Erstellt eine Liste aller in der Datenbank für den angegebenen Abitur-Jahrgang vorhanden "
                           + "Fachwahlen der gymnasialen Oberstufe. "
                           + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Fächerdaten "
                           + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Eine Liste der Fachwahlen",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = GostJahrgangFachwahlenHalbjahr.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachwahlen anzusehen.")
    @ApiResponse(responseCode = "404", description = "Keine Fachwahlen gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangHalbjahrFachwahlen(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
            @PathParam("halbjahr") final int halbjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		if (abiturjahr < 0)
		    			return OperationError.NOT_FOUND.getResponse("Fachwahlen sind für den Vorlagen-Abiturjahrgang nicht verfügbar.");
		            return (new DataGostAbiturjahrgangFachwahlen(conn, abiturjahr)).getSchuelerFachwahlenResponseHalbjahr(halbjahr);
    			},
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_ANSEHEN_FUNKTION,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage alle Laufbahnplanungsfehler/-rückmeldungen
     * der Gesamt-Belegprüfungen zu den Schüler-Laufbahnen eines Abitur-Jahrgangs der
     * gymnasialen Oberstufe im angegebenen Schema.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Belegprüfungsergebnisse
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/belegpruefung/gesamt")
    @Operation(summary = "Gibt die (Fehler-)Rückmeldungen der Gesamt-Belegprüfung zu den Schüler-Laufbahnen eines Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Gibt die (Fehler-)Rückmeldungen der Gesamt-Belegprüfung zu den Schüler-Laufbahnen eines "
                           + "Abitur-Jahrganges der gymnasialen Oberstufe zurück."
                           + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung dazu hat.")
    @ApiResponse(responseCode = "200", description = "Eine Liste mit den Schülern und den zugehörigen Belegprüfungsfehlern.",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GostBelegpruefungsErgebnisse.class))))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Rückmeldungen abzufragen.")
    @ApiResponse(responseCode = "404", description = "Keine und unvollständige Daten für die Belegprüfung gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangBelegpruefungsergebnisseGesamt(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		if (abiturjahr < 0)
		    			return OperationError.NOT_FOUND.getResponse("Eine Belegprüfung ist für den Vorlagen-Abiturjahrgang nicht möglich.");
		            return (new DataGostSchuelerLaufbahnplanung(conn)).pruefeBelegungAbiturjahrgang(abiturjahr, GostBelegpruefungsArt.GESAMT);
		    	},
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage alle Laufbahnplanungsfehler/-rückmeldungen
     * der EF1-Belegprüfungen zu den Schüler-Laufbahnen eines Abitur-Jahrgangs der
     * gymnasialen Oberstufe im angegebenen Schema.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Belegprüfungsergebnisse
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/belegpruefung/EF1")
    @Operation(summary = "Gibt die (Fehler-)Rückmeldungen der EF1-Belegprüfung zu den Schüler-Laufbahnen eines Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Gibt die (Fehler-)Rückmeldungen der EF1-Belegprüfung zu den Schüler-Laufbahnen eines "
                           + "Abitur-Jahrganges der gymnasialen Oberstufe zurück."
                           + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung dazu hat.")
    @ApiResponse(responseCode = "200", description = "Eine Liste mit den Schülern und den zugehörigen Belegprüfungsfehlern.",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GostBelegpruefungsErgebnisse.class))))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Rückmeldungen abzufragen.")
    @ApiResponse(responseCode = "404", description = "Keine und unvollständige Daten für die Belegprüfung gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangBelegpruefungsergebnisseEF1(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		if (abiturjahr < 0)
		    			return OperationError.NOT_FOUND.getResponse("Eine Belegprüfung ist für den Vorlagen-Abiturjahrgang nicht möglich.");
		            return (new DataGostSchuelerLaufbahnplanung(conn)).pruefeBelegungAbiturjahrgang(abiturjahr, GostBelegpruefungsArt.EF1);
    			},
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


	/**
     * Die OpenAPI-Methode für die Abfrage der Default-Laufbahnplanungsdaten der gymnasialen Oberstufe für einen
     * Abiturjahrgang.
     *
     * @param schema      das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param abiturjahr  das Abiturjahr
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return die Laufbahnplanungsdaten des Abiturjahrgangs
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/laufbahnplanung")
    @Operation(summary = "Liest die Laufbahnplanungsdaten für die gymnasiale Oberstufe zu dem Abiturjahrgang aus.",
               description = "Liest die Laufbahnplanungsdaten für die gymnasiale Oberstufe zu dem Abiturjahrgang "
    		    + "aus der Datenbank aus und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen der Laufbahnplanungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Laufbahnplanungsdaten der gymnasialen Oberstufe des angegebenen Abiturjahrgangs",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Abiturdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Laufbahnplanungsdaten der Gymnasialen Oberstufe eines Abiturjahrgangs auszulesen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für den angegebenen Abiturjahrgangs mit Laufbahnplanungsdaten der gymnasialen Oberstufe gefunden")
    public Response getGostAbiturjahrgangLaufbahnplanung(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangLaufbahnplanung(conn).get(abiturjahr),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage einer Fachwahl der gymnasialen Oberstufe in der Vorlage
     * eines Abiturjahrgangs.
     *
     * @param schema       das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param abiturjahr   das Abiturjahr
     * @param fach_id      die ID des Faches
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die Fachwahlen des Abiturjahrgangs zu dem Fach
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachwahl/{fachid : \\d+}")
    @Operation(summary = "Liest für die gymnasiale Oberstufe die Fachwahlen zu einem Fach von dem angegebenen Abiturjahrgang aus.",
               description = "Liest für die gymnasiale Oberstufe die Fachwahlen zu einem Fach von dem angegebenen Abiturjahrgang aus. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen der Fachwahlen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Fachwahlen der gymnasialen Oberstufe für das angegebene Fach und den angegebenen Abiturjahrgang",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostSchuelerFachwahl.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachwahlen der Gymnasialen Oberstufe eines Abiturjahrgang auszulesen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für einen Abiturjahrgang mit Laufbahnplanungsdaten der gymnasialen Oberstufe für die angegebene ID gefunden")
    public Response getGostAbiturjahrgangFachwahl(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @PathParam("fachid") final long fach_id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangLaufbahnplanung(conn).getFachwahl(abiturjahr, fach_id),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Anpassen der Fachwahlen zu einem Fach der gymnasialen Oberstufe
     * für die Vorlage des angegebenen Abiturjahrgangs.
     *
     * @param schema       das Datenbankschema, auf welches der Patch ausgeführt werden soll
	 * @param abiturjahr   das Abiturjahr
     * @param fach_id      die ID des Faches
     * @param is           der InputStream, mit dem JSON-Patch-Objekt nach RFC 7386
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @PATCH
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachwahl/{fachid : \\d+}")
    @Operation(summary = "Passt die Fachwahl der Vorlage des angegebenen Abiturjahrgangs in Bezug auf ein Fach der Gymnasiale Oberstufe an.",
    description = "Passt die Fachwahl der Vorlage des angegebenen Abiturjahrgangs in Bezug auf ein Fach der Gymnasiale Oberstufe an. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Anpassen der Fachwahlen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "203", description = "Der Patch wurde erfolgreich in die Fachwahlen integriert.")
    @ApiResponse(responseCode = "400", description = "Der Patch ist fehlerhaft aufgebaut.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachwahlen zu ändern.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für einen Abiturjahrgang mit Laufbahnplanungsdaten der gymnasialen Oberstufe oder für das Fach gefunden")
    @ApiResponse(responseCode = "409", description = "Der Patch ist fehlerhaft, da zumindest eine Rahmenbedingung für einen Wert nicht erfüllt wurde")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response patchGostAbiturjahrgangFachwahl(
    		@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @PathParam("fachid") final long fach_id,
    		@RequestBody(description = "Der Patch für die Fachwahl", required = true, content =
    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GostSchuelerFachwahl.class))) final InputStream is,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangLaufbahnplanung(conn).patchFachwahl(abiturjahr, fach_id, is),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Zurücksetzen der Fachwahlen in der Vorlage
     * für die Laufbahnplanung in dem angegebenen Abiturjahrgang.
     *
     * @param schema       das Datenbankschema
     * @param abiturjahr   der Abitur-Jahrgang dessen Fachwahlen zurückgesetzt werden
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @POST
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachwahl/reset")
    @Operation(summary = "Setzt die Fachwahlen in der Vorlage für eine Laufbahnplanung in dem angegebenen Abiturjahrgang zurück.",
    description = "Setzt die Fachwahlen in der Vorlage für eine Laufbahnplanung in dem angegebenen Abiturjahrgang zurück."
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Zurücksetzen der Fachwahlen besitzt.")
    @ApiResponse(responseCode = "203", description = "Die Fachwahlen wurden erfolgreich zurückgesetzt.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachwahlen zurückzusetzen.")
    @ApiResponse(responseCode = "404", description = "Der Abiturjahrgang wurde nicht gefunden.")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response resetGostAbiturjahrgangFachwahlen(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangLaufbahnplanung(conn).reset(abiturjahr),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Zurücksetzen aller Fachwahlen von allen Schülern
     * des Abiturjahrgangs. Hierbei bleiben die Fachwahlen für Halbjahre erhalten,
     * für die bereits Leistungsdaten vorhanden sind. Alle anderen Halbjahre werden
     * geleert.
     * Für den Fall, dass noch keine Leistungsdaten vorhanden sind, so wird die
     * Laufbahnplanung mit den Fachwahlen aus der Vorlage initialisiert.
     *
     * @param schema       das Datenbankschema
     * @param abiturjahr   der Abitur-Jahrgang, bei dessen Schüler die Fachwahlen zurückgesetzt werden
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @POST
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachwahl/schueler/reset")
    @Operation(summary = "Setzt die Fachwahlen aller Schüler des angegebenen Abiturjahrgang zurück.",
    description = "Setzt die Fachwahlen aller Schüler des angegebenen Abiturjahrgang zurück. "
    			+ "Die Fachwahlen werden von allen Halbjahren ohn Leistungsdaten entfernt. Sollten danach "
    			+ "keine Fachwahlen bei einem Schüler vorhanden sein, so wird die Laufbahnplanung mit der Fachwahlen-Vorlage des "
    			+ "Abiturjahrgangs initialisiert."
    		    + "Außerdem wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Zurücksetzen der Fachwahlen besitzt.")
    @ApiResponse(responseCode = "203", description = "Die Fachwahlen wurden erfolgreich zurückgesetzt.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachwahlen zurückzusetzen.")
    @ApiResponse(responseCode = "404", description = "Der Abiturjahrgang wurde nicht gefunden.")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response resetGostAbiturjahrgangSchuelerFachwahlen(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangLaufbahnplanung(conn).resetSchuelerAlle(abiturjahr),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Laufbahnplanungsdaten der gymnasialen Oberstufe eines Schülers.
     *
     * @param schema      das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param id          die ID des Schülers zu dem die Laufbahnplanungsdaten ausgelesen werden
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return die Laufbahnplanungsdaten des Schülers
     */
    @GET
    @Path("/schueler/{id : \\d+}/laufbahnplanung")
    @Operation(summary = "Liest die Laufbahnplanungsdaten für die gymnasiale Oberstufe zu dem Schüler mit der angegebenen ID aus.",
               description = "Liest die Laufbahnplanungsdaten für die gymnasiale Oberstufe zu dem Schüler mit der angegebenen ID "
    		    + "aus der Datenbank aus und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen der Laufbahnplanungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Laufbahnplanungsdaten der gymnasialen Oberstufe des angegebenen Schülers",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Abiturdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Laufbahnplanungsdaten der Gymnasialen Oberstufe eines Schülers auszulesen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für einen Schüler mit Laufbahnplanungsdaten der gymnasialen Oberstufe für die angegebene ID gefunden")
    public Response getGostSchuelerLaufbahnplanung(@PathParam("schema") final String schema, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).get(id),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Beratungsdaten für die Laufbahnplanung der gymnasialen Oberstufe eines Schülers.
     *
     * @param schema      das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param id          die ID des Schülers zu dem die Beratungsdaten ausgelesen werden
     * @param request     die Informationen zur HTTP-Anfrage
     *
     * @return die Beratungsdaten zu dem Schüler
     */
    @GET
    @Path("/schueler/{id : \\d+}/laufbahnplanung/beratungsdaten")
    @Operation(summary = "Liest die Beratungsdaten für die Laufbahnplanung der gymnasiale Oberstufe zu dem Schüler mit der angegebenen ID aus.",
               description = "Liest die Beratungsdaten für die Laufbahnplanung der gymnasiale Oberstufe zu dem Schüler mit der angegebenen ID "
    		    + "aus der Datenbank aus und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen der Beratungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Beratungsdaten der gymnasialen Oberstufe des angegebenen Schülers",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostLaufbahnplanungBeratungsdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Beratungsdaten der Gymnasialen Oberstufe eines Schülers auszulesen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für einen Schüler mit Beratungsdaten der gymnasialen Oberstufe für die angegebene ID gefunden")
    public Response getGostSchuelerLaufbahnplanungBeratungsdaten(@PathParam("schema") final String schema, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanungBeratungsdaten(conn).get(id),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }

    /**
     * Die OpenAPI-Methode für das Anpassen der Beratungsdaten für die Laufbahnplanung der
     * gymnasialen Oberstufe eines Schülers.
     *
     * @param schema        das Datenbankschema, auf welches der Patch ausgeführt werden soll
     * @param schueler_id   die ID des Schülers
     * @param is            der InputStream, mit dem JSON-Patch-Objekt nach RFC 7386
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @PATCH
    @Path("/schueler/{schuelerid : \\d+}/laufbahnplanung/beratungsdaten")
    @Operation(summary = "Passt die Beratungsdaten für die Laufbahnplanung der gymnasiale Oberstufe zu dem Schüler mit der angegebenen ID an.",
    description = "Passt die Beratungsdaten für die Laufbahnplanung der gymnasiale Oberstufe zu dem Schüler mit der angegebenen ID an. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Anpassen der Beratungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Der Patch wurde erfolgreich integriert.")
    @ApiResponse(responseCode = "400", description = "Der Patch ist fehlerhaft aufgebaut.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Beratungsdaten zu ändern.")
    @ApiResponse(responseCode = "404", description = "Kein Schüler-Eintrag mit der angegebenen ID gefunden")
    @ApiResponse(responseCode = "409", description = "Der Patch ist fehlerhaft, da zumindest eine Rahmenbedingung für einen Wert nicht erfüllt wurde")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response patchGostSchuelerLaufbahnplanungBeratungsdaten(
    		@PathParam("schema") final String schema, @PathParam("schuelerid") final long schueler_id,
    		@RequestBody(description = "Der Patch für die Beratungsdaten", required = true, content =
    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GostLaufbahnplanungBeratungsdaten.class))) final InputStream is,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanungBeratungsdaten(conn).patch(schueler_id, is),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


	/**
	 * Die OpenAPI-Methode für die Abfrage der Anlage 12 APO-GOSt (Abiturzeugnis) als PDF-Datei.
	 *
	 * @param schema      	das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param idsSchueler   Liste der IDs der SuS, deren Kurse-Schienen-Zuordnung erstellt werden soll. Werden keine IDs übergeben, so wird die Matrix allgemein für das Blockungsergebnis erstellt.
	 * @param request     	die Informationen zur HTTP-Anfrage
	 *
	 * @return Die zu den übergebenen IDs gehörigen Anlage 12 APO-GOSt (Abiturzeugnis) der gymnasialen Oberstufe
	 */
	@POST
	@Produces("application/pdf")
	@Path("/schueler/pdf/gostanlage12")
	@Operation(
		summary = "Erstellt die Anlage 12 (Abiturzeugnis) zu den Schülern mit den angegebenen IDs.",
		description = "Erstellt die Anlage 12 (Abiturzeugnis)  der gymnasialen Oberstufe zu den Schülern mit der angegebenen IDs als PDF-Datei. "
			+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen des Wahlbogens besitzt.")
	@ApiResponse(
		responseCode = "200",
		description = "Die PDF-Datei mit den Abiturzeugnissen der gymnasialen Oberstufe.",
		content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary", description = "Anlage 12 - Abiturzeugnis")))
	@ApiResponse(
		responseCode = "403",
		description = "Der SVWS-Benutzer hat keine Rechte, um die Anlage 12 (Abiturzeugnis) für die Gymnasialen Oberstufe eines Schülers zu erstellen.")
	@ApiResponse(
		responseCode = "404",
		description = "Kein Eintrag zu den angegebenen IDs gefunden.")
	@ApiResponse(
		responseCode = "500",
		description = "Es ist ein unbekannter Fehler aufgetreten.",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
	public Response pdfGostAnlage12(
		@PathParam("schema") final String schema,
		@RequestBody(
			description = "Schüler-IDs, für die die Anlage 12 erstellt werden soll.",
			required = true,
			content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
		final List<Long> idsSchueler,
		@Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
				(new PdfFactory(conn, "de/svws_nrw/module/reporting/gost/apogostanlagen/APOGOStAnlage12.html", "de/svws_nrw/module/reporting/gost/apogostanlagen/APOGOStAnlage12.css", false, false, false, null, new ArrayList<>(), idsSchueler, true, false, 0))
					.createPdf(),
			request, ServerMode.DEV,
			BenutzerKompetenz.ABITUR_ANSEHEN_FUNKTIONSBEZOGEN,
			BenutzerKompetenz.ABITUR_ANSEHEN_ALLGEMEIN);
	}


	/**
	 * Die OpenAPI-Methode für die Abfrage von Wahlbögen zur Laufbahnplanung der gymnasialen Oberstufe als PDF-Datei.
	 *
	 * @param schema      	das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param idsSchueler   Liste der IDs der SuS, deren Laufbahnwahlbögen erstellt werden soll.
	 * @param detaillevel	gibt an, welche Detailinformationen die Liste enthalten soll: 0 = nur belegte Fächer, 1 = alle Fächer
	 * @param request 		die Informationen zur HTTP-Anfrage
	 *
	 * @return Die zu den übergebenen IDs gehörigen Wahlbögen zur Laufbahnplanung der gymnasialen Oberstufe
	 */
	@POST
	@Produces("application/pdf")
	@Path("/schueler/pdf/laufbahnplanungwahlbogen/{detaillevel : \\d+}")
	@Operation(
		summary = "Erstellt die Wahlbögen für die gymnasiale Oberstufe zu den Schülern mit den angegebenen IDs.",
		description = "Erstellt die Wahlbogen für die Laufbahnplanung der gymnasialen Oberstufe zu den Schülern mit der angegebenen IDs als PDF-Datei. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen des Wahlbogens besitzt.")
	@ApiResponse(
		responseCode = "200",
		description = "Die PDF-Datei mit den Wahlbögen zur Laufbahnplanung der gymnasialen Oberstufe.",
		content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary", description = "Wahlbogen Laufbahnplanung")))
	@ApiResponse(
		responseCode = "403",
		description = "Der SVWS-Benutzer hat keine Rechte, um den Wahlbogen für die Gymnasialen Oberstufe eines Schülers zu erstellen.")
	@ApiResponse(
		responseCode = "404",
		description = "Kein Eintrag zu den angegebenen IDs gefunden.")
	@ApiResponse(
		responseCode = "500",
		description = "Es ist ein unbekannter Fehler aufgetreten.",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
	public Response pdfGostLaufbahnplanungSchuelerWahlbogen(
            @PathParam("schema") final String schema,
            @RequestBody(
                    description = "Schüler-IDs, für die die Wahlbögen erstellt werden soll.",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
            final List<Long> idsSchueler,
			@PathParam("detaillevel") final int detaillevel,
            @Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
				(new PdfFactory(conn, "de/svws_nrw/module/reporting/gost/laufbahnplanung/GostLaufbahnplanungWahlbogen.html", "de/svws_nrw/module/reporting/gost/laufbahnplanung/GostLaufbahnplanungWahlbogen.css", false, false, false, null, new ArrayList<>(), idsSchueler, false, true, detaillevel))
					.createPdf(),
			request, ServerMode.STABLE,
			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
	}


	/**
	 * Die OpenAPI-Methode für die Abfrage einer Ergebnisliste zur Laufbahnplanung der gymnasialen Oberstufe als PDF-Datei.
	 *
	 * @param schema      		Das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
	 * @param idsSchueler       Liste der IDs der SuS, deren Ergebnisübersicht der Laufbahnplanung für die gymnasiale Oberstufe erstellt werden soll.
	 * @param detaillevel		gibt an, welche Detailinformationen die Liste enthalten soll: 0 = Summen, 1 = Summen und Fehler, 2 = Summen, Fehler und Hinweise
	 * @param request     		die Informationen zur HTTP-Anfrage
	 *
	 * @return Die zu den übergebenen IDs zugehörige Ergebnisübersicht der Laufbahnplanung der gymnasialen Oberstufe
	 */
	@POST
	@Produces("application/pdf")
	@Path("/schueler/pdf/laufbahnplanungergebnisuebersicht/{detaillevel : \\d+}")
	@Operation(
		summary = "Erstellt eine Ergebnisübersicht der Laufbahnplanung für die gymnasiale Oberstufe zu den Schülern mit den angegebenen IDs.",
		description = "Erstellt eine Ergebnisübersicht der Laufbahnplanung für die Laufbahnplanung der gymnasialen Oberstufe zu den Schülern mit der angegebenen IDs als PDF-Datei. "
					+ "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen des Wahlbogens besitzt.")
	@ApiResponse(
		responseCode = "200",
		description = "Die PDF-Datei mit der Ergebnisübersicht der Laufbahnplanung der gymnasialen Oberstufe.",
		content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary", description = "Ergebnisliste Laufbahnplanung")))
	@ApiResponse(
		responseCode = "403",
		description = "Der SVWS-Benutzer hat keine Rechte, um die Ergebnisliste Laufbahnplanung für die gymnasialen Oberstufe zu erstellen.")
	@ApiResponse(
		responseCode = "404",
		description = "Kein Eintrag zu den angegebenen IDs gefunden.")
	@ApiResponse(
		responseCode = "500",
		description = "Es ist ein unbekannter Fehler aufgetreten.",
		content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
	public Response pdfGostLaufbahnplanungSchuelerErgebnisuebersicht(
            @PathParam("schema") final String schema,
            @RequestBody(description = "Schüler-IDs, für die die Ergebnisliste Laufbahnplanung erstellt werden soll.",
                    required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
            final List<Long> idsSchueler,
            @PathParam("detaillevel") final int detaillevel,
            @Context final HttpServletRequest request) {
		return DBBenutzerUtils.runWithTransaction(conn ->
				(new PdfFactory(conn, "de/svws_nrw/module/reporting/gost/laufbahnplanung/GostLaufbahnplanungErgebnisuebersicht.html", "de/svws_nrw/module/reporting/gost/laufbahnplanung/GostLaufbahnplanungErgebnisuebersicht.css", false, false, false, null, new ArrayList<>(), idsSchueler, false, true, detaillevel))
					.createPdf(),
			request, ServerMode.STABLE,
			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
	}


    /**
     * Die OpenAPI-Methode für die Abfrage einer Fachwahl der gymnasialen Oberstufe eines Schülers.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param schueler_id   die ID des Schülers
     * @param fach_id       die ID des Faches
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return die Fachwahlen des Schülers zu dem Fach
     */
    @GET
    @Path("/schueler/{schuelerid : \\d+}/fachwahl/{fachid : \\d+}")
    @Operation(summary = "Liest für die gymnasiale Oberstufe die Fachwahlen zu einem Fach von dem angegebenen Schüler aus.",
               description = "Liest für die gymnasiale Oberstufe die Fachwahlen zu einem Fach von dem angegebenen Schüler aus. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Auslesen der Fachwahlen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Fachwahlen der gymnasialen Oberstufe für das angegebene Fach und den angegebenen Schüler",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Abiturdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachwahlen der Gymnasialen Oberstufe eines Schülers auszulesen.")
    @ApiResponse(responseCode = "404", description = "Kein Eintrag für einen Schüler mit Laufbahnplanungsdaten der gymnasialen Oberstufe für die angegebene ID gefunden")
    public Response getGostSchuelerFachwahl(@PathParam("schema") final String schema, @PathParam("schuelerid") final long schueler_id, @PathParam("fachid") final long fach_id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).getFachwahl(schueler_id, fach_id),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Anpassen der Fachwahlen eines Schülers
     * zu einem Fach der gymnasialen Oberstufe.
     *
     * @param schema        das Datenbankschema, auf welches der Patch ausgeführt werden soll
     * @param schueler_id   die ID des Schülers
     * @param fach_id       die ID des Faches
     * @param is            der InputStream, mit dem JSON-Patch-Objekt nach RFC 7386
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @PATCH
    @Path("/schueler/{schuelerid : \\d+}/fachwahl/{fachid : \\d+}")
    @Operation(summary = "Passt die Fachwahl eines Schüler in Bezug auf ein Fach der Gymnasiale Oberstufe an.",
    description = "Passt die Fachwahl eines Schüler in Bezug auf ein Fach der Gymnasiale Oberstufe an. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Anpassen der Fachwahlen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Der Patch wurde erfolgreich in die Fachwahlen integriert.")
    @ApiResponse(responseCode = "400", description = "Der Patch ist fehlerhaft aufgebaut.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachwahlen zu ändern.")
    @ApiResponse(responseCode = "404", description = "Kein Schüler- oder Fach-Eintrag mit der angegebenen ID gefunden")
    @ApiResponse(responseCode = "409", description = "Der Patch ist fehlerhaft, da zumindest eine Rahmenbedingung für einen Wert nicht erfüllt wurde")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response patchGostSchuelerFachwahl(
    		@PathParam("schema") final String schema, @PathParam("schuelerid") final long schueler_id, @PathParam("fachid") final long fach_id,
    		@RequestBody(description = "Der Patch für die Fachdaten", required = true, content =
    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GostSchuelerFachwahl.class))) final InputStream is,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).patchFachwahl(schueler_id, fach_id, is),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Zurücksetzen der Fachwahlen eines Schülers.
     *
     * @param schema       das Datenbankschema
     * @param schuelerid   die ID des Schülers, dessen Fachwahlen zurückgesetzt werden
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @POST
    @Path("/schueler/{schuelerid : \\d+}/fachwahl/reset")
    @Operation(summary = "Setzt die Fachwahlen des Schülers mit der angegebenen ID zurück.",
    description = "Setzt die Fachwahlen des Schülers mit der angegebenen ID zurück."
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Zurücksetzen der Fachwahlen besitzt.")
    @ApiResponse(responseCode = "203", description = "Die Fachwahlen wurden erfolgreich zurückgesetzt.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachwahlen zurückzusetzen.")
    @ApiResponse(responseCode = "404", description = "Der Schüler bzw. der zugehörige Abiturjahrgang wurde nicht gefunden.")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response resetGostSchuelerFachwahlen(@PathParam("schema") final String schema, @PathParam("schuelerid") final long schuelerid,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).reset(schuelerid),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }


	/**
	 * Liest die Leistungsdaten in Bezug auf die gymnasiale Oberstufe des Schülers mit der angegebene ID aus der Datenbank und liefert diese zurück.
	 * Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Leistungsdaten besitzt.
	 *
	 * @param schema   das Schema aus dem die Leistungsdaten des Schülers kommen sollen
	 * @param id       die ID des Schülers zu dem die Leistungsdaten geliefert werden sollen
	 *
	 * @param request  die Informationen zur HTTP-Anfrage
	 *
	 * @return die Leistungsdaten in der gymnasialen Oberstufe für den Schüler mit der
	 *         angegebenen ID und die Berechtigung des Datenbank-Users
	 */
    @GET
    @Path("/schueler/{id : \\d+}/leistungsdaten")
    @Operation(summary = "Liefert zu der ID des Schülers die zugehörigen Leistungsdaten in Bezug auf die gymnasiale Oberstufe.",
    description = "Liest die Leistungsdaten in Bezug auf die gymnasiale Oberstufe des Schülers mit der angegebene ID aus der "
    		    + "Datenbank und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Leistungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Leistungsdaten des Schülers",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostLeistungen.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Leistungsdaten anzusehen.")
    @ApiResponse(responseCode = "404", description = "Kein Schüler-Eintrag mit der angegebenen ID gefunden")
    public Response getGostSchuelerLeistungsdaten(@PathParam("schema") final String schema, @PathParam("id") final long id,
    		                                            @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(DBUtilsGost.getLeistungsdaten(conn, id)).build(),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN);
    }



	/**
	 * Liest die Abiturdaten aus den Leistungsdaten der gymnasialen Oberstufe des Schülers mit der angegebene ID aus der Datenbank und liefert diese zurück.
	 * Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Leistungsdaten besitzt.
	 *
	 * @param schema   das Schema aus dem die Abiturdaten des Schülers kommen sollen
	 * @param id       die ID des Schülers zu dem die Abiturdaten geliefert werden sollen
	 *
	 * @param request  die Informationen zur HTTP-Anfrage
	 *
	 * @return die Abiturdaten in der gymnasialen Oberstufe für den Schüler mit der
	 *         angegebenen ID und die Berechtigung des Datenbank-Users
	 */
    @GET
    @Path("/schueler/{id : \\d+}/abiturdatenAusLeistungsdaten")
    @Operation(summary = "Liefert zu der ID des Schülers die zugehörigen Abiturdaten, die aus den Leistungsdaten der Oberstufe gewonnen werden können.",
    description = "Liest die Abiturdaten aus den Leistungsdaten der gymnasiale Oberstufe des Schülers mit der angegebene ID aus der "
    		    + "Datenbank und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Leistungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Abiturdaten des Schülers",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Abiturdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Leistungsdaten anzusehen.")
    @ApiResponse(responseCode = "404", description = "Kein Schüler-Eintrag mit der angegebenen ID gefunden")
    public Response getGostSchuelerAbiturdatenAusLeistungsdaten(@PathParam("schema") final String schema, @PathParam("id") final long id,
    		                                                       @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(DBUtilsGostAbitur.getAbiturdatenAusLeistungsdaten(conn, id)).build(),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.ABITUR_ANSEHEN_ALLGEMEIN,
    			BenutzerKompetenz.ABITUR_ANSEHEN_FUNKTIONSBEZOGEN);
    }



	/**
	 * Liest die Abiturdaten aus den Abiturtabellen des Schülers mit der angegebene ID und liefert diese zurück.
	 * Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Leistungsdaten besitzt.
	 *
	 * @param schema   das Schema aus dem die Abiturdaten des Schülers kommen sollen
	 * @param id       die ID des Schülers zu dem die Abiturdaten geliefert werden sollen
	 *
	 * @param request  die Informationen zur HTTP-Anfrage
	 *
	 * @return die Abiturdaten in der gymnasialen Oberstufe für den Schüler mit der
	 *         angegebenen ID und die Berechtigung des Datenbank-Users
	 */
    @GET
    @Path("/schueler/{id : \\d+}/abiturdaten")
    @Operation(summary = "Liefert zu der ID des Schülers die zugehörigen Abiturdaten aus den entsprechenden Abiturtabellen der SVWS-DB.",
    description = "Liest die Abiturdaten aus den Abiturtabellen des Schülers mit der angegebene ID und liefert diese zurück. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Leistungsdaten "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Abiturdaten des Schülers",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = Abiturdaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Leistungsdaten anzusehen.")
    @ApiResponse(responseCode = "404", description = "Kein Schüler-Eintrag mit der angegebenen ID gefunden")
    public Response getGostSchuelerAbiturdaten(@PathParam("schema") final String schema, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(DBUtilsGostAbitur.getAbiturdaten(conn, id)).build(),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.ABITUR_ANSEHEN_ALLGEMEIN,
    			BenutzerKompetenz.ABITUR_ANSEHEN_FUNKTIONSBEZOGEN);
    }



    /**
     * Die OpenAPI-Methode für die Prüfung der Belegprüfung der Abiturdaten.
     *
     * @param schema        das Schema aus dem die Fächerdaten kommen sollen
     * @param abidaten      zu übergebende Fächerdaten für die Prüfung der Belegung im Abitur
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              das Ergebnis der Prüfung der Belegprüfung der Abiturdaten
     */
    @POST
    @Path("abitur/belegpruefung/gesamt")
    @Operation(summary = "Führt eine Belegprüfung anhand der übergebenen Abiturdaten durch.",
               description = "Prüft anhand der übergeben Abiturdaten, ob die Belegung in den Abiturdaten korrekt ist oder nicht. Es werden ggf. auch "
               		+ "Belegungsfehler und Hinweise zur Belegung zurückgegeben.")
    @ApiResponse(responseCode = "200", description = "Das Ergebnis der Belegprüfung, ggf. mit Belegungsfehlern",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostBelegpruefungErgebnis.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Belegprüfung durchzuführen.")
    @ApiResponse(responseCode = "404", description = "Es wurden keine Fächerdaten zur gymnasialen Oberstufe gefunden")
    public Response getGostAbiturBelegpruefungGesamt(@PathParam("schema") final String schema,
    		@RequestBody(description = "Die Abiturdaten für die Belegprüfung", required = true,
    					 content = @Content(mediaType = MediaType.APPLICATION_JSON,
    					 schema = @Schema(implementation = Abiturdaten.class))) final Abiturdaten abidaten,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		final @NotNull DTOEigeneSchule schule = SchulUtils.getDTOSchule(conn);
			    	if (!schule.Schulform.daten.hatGymOb)
			    		throw OperationError.NOT_FOUND.exception();
			    	final @NotNull GostJahrgangsdaten jahrgangsdaten = DataGostJahrgangsdaten.getJahrgangsdaten(conn, abidaten.abiturjahr);
			    	// Prüfe die Belegung der Kurse mithilfe des Abiturdaten-Managers und gib das Ergebnis der Belegprüfung zurück.
			    	GostFaecherManager faecherManager = DBUtilsFaecherGost.getFaecherManager(conn, abidaten.abiturjahr);
			    	if (faecherManager.isEmpty())
			    		faecherManager = DBUtilsFaecherGost.getFaecherManager(conn, null);
			    	faecherManager.addFachkombinationenAll(DataGostJahrgangFachkombinationen.getFachkombinationen(conn, abidaten.abiturjahr));
					final AbiturdatenManager manager = new AbiturdatenManager(abidaten, jahrgangsdaten, faecherManager, GostBelegpruefungsArt.GESAMT);
					return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(manager.getBelegpruefungErgebnis()).build();
    			},
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.KEINE);
    }


    /**
     * Die OpenAPI-Methode für die Prüfung der Belegprüfung der Abiturdaten nur für die EF_1.
     *
	 * @param schema        das Schema aus dem die Fächerdaten kommen sollen
     * @param abidaten      zu übergebende Fächerdaten für die Prüfung der Belegung nur für die EF_1
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              das Ergebnis der Prüfung der Belegprüfung nur für die EF_1
     */
    @POST
    @Path("abitur/belegpruefung/EF1")
    @Operation(summary = "Führt eine Belegprüfung nur für die EF.1 anhand der übergebenen Abiturdaten durch.",
               description = "Prüft anhand der übergeben Abiturdaten, ob die Belegung in den Abiturdaten korrekt ist oder nicht. Es werden ggf. auch "
               		+ "Belegungsfehler und Hinweise zur Belegung zurückgegeben.")
    @ApiResponse(responseCode = "200", description = "Das Ergebnis der Belegprüfung, ggf. mit Belegungsfehlern",
                 content = @Content(mediaType = "application/json",
                 schema = @Schema(implementation = GostBelegpruefungErgebnis.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Belegprüfung durchzuführen.")
    @ApiResponse(responseCode = "404", description = "Es wurden keine Fächerdaten zur gymnasialen Oberstufe gefunden")
    public Response getGostAbiturBelegpruefungEF1(@PathParam("schema") final String schema,
    		@RequestBody(description = "Die Abiturdaten für die Belegprüfung", required = true,
    		             content = @Content(mediaType = MediaType.APPLICATION_JSON,
					     schema = @Schema(implementation = Abiturdaten.class))) final Abiturdaten abidaten,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> {
		    		final @NotNull DTOEigeneSchule schule = SchulUtils.getDTOSchule(conn);
			    	if (!schule.Schulform.daten.hatGymOb)
			    		throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());
			    	final @NotNull GostJahrgangsdaten jahrgangsdaten = DataGostJahrgangsdaten.getJahrgangsdaten(conn, abidaten.abiturjahr);
			    	// Prüfe die Belegung der Kurse mithilfe des Abiturdaten-Managers und gib das Ergebnis der Belegprüfung zurück.
			    	GostFaecherManager faecherManager = DBUtilsFaecherGost.getFaecherManager(conn, abidaten.abiturjahr);
			    	if (faecherManager.isEmpty())
			    		faecherManager = DBUtilsFaecherGost.getFaecherManager(conn, null);
			    	faecherManager.addFachkombinationenAll(DataGostJahrgangFachkombinationen.getFachkombinationen(conn, abidaten.abiturjahr));
					final AbiturdatenManager manager = new AbiturdatenManager(abidaten, jahrgangsdaten, faecherManager, GostBelegpruefungsArt.EF1);
					return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(manager.getBelegpruefungErgebnis()).build();
		    	},
        		request, ServerMode.STABLE,
    			BenutzerKompetenz.KEINE);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Liste der Fachkombinationen für die Laufbahnplanung
     * der gymnasialen Oberstufe für den angegebenen Abitur-Jahrgang.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param abiturjahr    der Abitur-Jahrgang
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return              die Liste der Fachkombinationen
     */
    @GET
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachkombinationen")
    @Operation(summary = "Gibt die Informationen zu den Fachkombinationen für die Laufbahnplanung des Abitur-Jahrganges der gymnasialen Oberstufe zurück.",
               description = "Erstellt eine Liste mit den Informationen zu den Fachkombinationen für die Laufbahnplanung des Abitur-Jahrganges der gymnasialen Oberstufe. "
               		       + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Ansehen der Fachkombinationen besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Liste der Fachkombinationen",
                 content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GostJahrgangFachkombination.class))))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Fachkombinationen anzusehen.")
    @ApiResponse(responseCode = "404", description = "Keine Fachkombinationen gefunden oder keine gymnasiale Oberstufe bei der Schulform vorhanden")
    public Response getGostAbiturjahrgangFachkombinationen(@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangFachkombinationen(conn, abiturjahr).getList(),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Anpassen einer Fachkombination für Fächer der gymnasialen Oberstufe.
     *
     * @param schema     das Datenbankschema, auf welches der Patch ausgeführt werden soll
     * @param id         die ID der Regel für die Fachkombination
     * @param is         der InputStream, mit dem JSON-Patch-Objekt nach RFC 7386
     * @param request    die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort
     */
    @PATCH
    @Path("/fachkombination/{id : \\d+}")
    @Operation(summary = "Passt die Fachkombination mit der angegebenen ID an.",
    description = "Passt die Fachkombination mit der angegebenen ID an."
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Anpassen von Fachkombinationen "
    		    + "besitzt.")
    @ApiResponse(responseCode = "200", description = "Der Patch wurde erfolgreich in die Fachkombination integriert.")
    @ApiResponse(responseCode = "400", description = "Der Patch ist fehlerhaft aufgebaut.")
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um Fachkombinationen zu ändern.")
    @ApiResponse(responseCode = "404", description = "Keine Fachkombination mit der angegebenen ID gefunden oder es wurden kein gültiges Fach als ID übergeben.")
    @ApiResponse(responseCode = "409", description = "Der Patch ist fehlerhaft, da zumindest eine Rahmenbedingung für einen Wert nicht erfüllt wurde")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response patchGostFachkombination(
    		@PathParam("schema") final String schema, @PathParam("id") final long id,
    		@RequestBody(description = "Der Patch für die Fachkombination", required = true, content =
    			@Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GostJahrgangFachkombination.class))) final InputStream is,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangFachkombinationen(conn, -1 /*nicht relevant*/).patch(id, is),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Löschen einer Regel zu einer Fächkombination
     * der gymnasialen Oberstufe.
     *
     * @param schema    das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param id        die ID der Regel der Fachkombination
     * @param request   die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort mit dem Status und ggf. der gelöschten Regel zur Fachkombination
     */
    @DELETE
    @Path("/fachkombination/{id : \\d+}")
    @Operation(summary = "Entfernt eine Regel zu einer Fachkombination in der Gymnasialen Oberstufe.",
               description = "Entfernt eine Regel zu einer Fachkombination in der Gymnasialen Oberstufe. "
    		    + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Entfernen besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Regel wurde wurde erfolgreich gelöscht.",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = GostJahrgangFachkombination.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Regel zu der Fachkombination zu löschen.")
    @ApiResponse(responseCode = "404", description = "Die Regel zu der Fachkombination wurde nicht gefunden.")
    public Response deleteGostFachkombination(@PathParam("schema") final String schema, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangFachkombinationen(conn, -1 /* nicht relevant */).delete(id),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für das Hinzufügen einer weiteren Fachkombination der gymnasialen Oberstufe mit dem angegebenen Typ
     * bei dem angegebenen Abiturjahrgang.
     *
     * @param schema       das Datenbankschema, in welchem die Fachkombination erstellt wird
     * @param abiturjahr   der Abitur-Jahrgang
     * @param typ          der Typ der Fachkombination (0: Wahl von Fach 2 ist in Kombination mit Fach 1 unzulässig, 1: Wahl von Fach 2 ist bei Wahl von Fach 1 nötig)
     * @param request      die Informationen zur HTTP-Anfrage
     *
     * @return die HTTP-Antwort mit der Schiene der Blockung der gymnasialen Oberstufe
     */
    @POST
    @Path("/abiturjahrgang/{abiturjahr : -?\\d+}/fachkombinationen/add/{typ : \\d+}")
    @Operation(summary = "Fügt eine Regel zu einer Fachkombination der Gymnasialen Oberstufe bei dem angegebenen Abiturjahrgang hinzu.",
               description = "Fügt eine Regel zu einer Fachkombination der Gymnasialen Oberstufe bei dem angegebenen Abiturjahrgang hinzu."
    		               + "Dabei wird geprüft, ob der SVWS-Benutzer die notwendige Berechtigung zum Erstellen einer solchen Fachkombination hat.")
    @ApiResponse(responseCode = "200", description = "Die Regel zur Fachkombination bezüglich der gymnasialen Oberstufe wurde erstellt",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = GostJahrgangFachkombination.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um eine Regel für eine Fachkombination hinzuzufügen.")
    @ApiResponse(responseCode = "404", description = "Abiturjahr nicht vorhanden")
    @ApiResponse(responseCode = "409", description = "Die übergebenen Daten sind fehlerhaft")
    @ApiResponse(responseCode = "500", description = "Unspezifizierter Fehler (z.B. beim Datenbankzugriff)")
    public Response addGostAbiturjahrgangFachkombination(
    		@PathParam("schema") final String schema, @PathParam("abiturjahr") final int abiturjahr, @PathParam("typ") final int typ,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostJahrgangFachkombinationen(conn, abiturjahr).add(typ),
        		request, ServerMode.STABLE,
        		BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_ALLGEMEIN,
    			BenutzerKompetenz.OBERSTUFE_KURSPLANUNG_FUNKTIONSBEZOGEN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Laufbahnplanungsdaten der gymnasialen Oberstufe
     * in Bezug auf den angegebenen Schüler als GZIP-Json.
     *
     * @param schema    das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param id        die ID des Schülers
     * @param request   die Informationen zur HTTP-Anfrage
     *
     * @return die Laufbahnplanungsdaten
     */
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Path("/schueler/{id : \\d+}/laufbahnplanung/export")
    @Operation(summary = "Liefert die Laufbahnplanungsdaten der gymnasialen Oberstufe für den angegebenen Schüler (GZip-komprimiert).",
    	description = "Liest die Laufbahnplanungsdaten der gymnasialen Oberstufe für den angegebenen Schüler aus der Datenbank "
    			+ "und liefert diese GZip-komprimiert zurück. Dabei wird geprüft, ob der SVWS-Benutzer die "
    			+ "notwendige Berechtigung zum Auslesen der Daten besitzt.")
    @ApiResponse(responseCode = "200", description = "Die GZip-komprimierten Laufbahndaten der gymnasialen Obertufe",
                 content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM,
                 schema = @Schema(type = "string", format = "binary", description = "Die GZip-komprimierten Laufbahndaten der gymnasialen Obertufe")))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Laufbahndaten auszulesen.")
    @ApiResponse(responseCode = "404", description = "Es wurden nicht alle benötigten Daten für das Erstellen der Laufbahn-Daten gefunden.")
    public Response exportGostSchuelerLaufbahnplanung(@PathParam("schema") final String schema, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).exportGZip(id),
        		request, ServerMode.STABLE, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }

    /**
     * Die OpenAPI-Methode für den Import von Laufbahnplanungsdaten eines Schülers der gymnasialen Oberstufe
     * mit der angegebenen ID.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param id            die ID des Schülers
     * @param multipart     Die Laufbahnplanungsdatei als GZIP-komprimiertes JSON
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return Rückmeldung, ob die Operation erfolgreich war mit dem Log der Operation
     */
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/schueler/{id : \\d+}/laufbahnplanung/import")
    @Operation(summary = "Importiert die Laufbahndaten aus der übergebenen Laufbahnplanungsdatei.",
               description = "Importiert die Laufbahndaten aus der übergebenen Laufbahnplanungsdatei")
    @ApiResponse(responseCode = "200", description = "Der Log vom Import der Laufbahndaten",
    			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "409", description = "Es ist ein Fehler beim Import aufgetreten. Ein Log vom Import wird zurückgegeben.",
    			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Der Benutzer hat keine Berechtigung, um die Laufbahndaten zu importieren.")
    public Response importGostSchuelerLaufbahnplanung(@PathParam("schema") final String schema,
    		@PathParam("id") final long id,
    		@RequestBody(description = "Die Laufbahnplanungsdatei", required = true, content =
			@Content(mediaType = MediaType.MULTIPART_FORM_DATA)) @MultipartForm final SimpleBinaryMultipartBody multipart,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).importGZip(id, multipart.data),
        		request, ServerMode.STABLE, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Laufbahnplanungsdaten der gymnasialen Oberstufe
     * in Bezug auf den angegebenen Schüler.
     *
     * @param schema    das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param id        die ID des Schülers
     * @param request   die Informationen zur HTTP-Anfrage
     *
     * @return die Laufbahnplanungsdaten
     */
    @GET
    @Path("/schueler/{id : \\d+}/laufbahnplanung/daten")
    @Operation(summary = "Liefert die Laufbahnplanungsdaten der gymnasialen Oberstufe für den angegebenen Schüler.",
    	description = "Liest die Laufbahnplanungsdaten der gymnasialen Oberstufe für den angegebenen Schüler aus der Datenbank "
    			+ "und liefert diese zurück. Dabei wird geprüft, ob der SVWS-Benutzer die "
    			+ "notwendige Berechtigung zum Auslesen der Daten besitzt.")
    @ApiResponse(responseCode = "200", description = "Die Laufbahndaten der gymnasialen Oberstufe",
			content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = GostLaufbahnplanungDaten.class)))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Laufbahndaten auszulesen.")
    @ApiResponse(responseCode = "404", description = "Es wurden nicht alle benötigten Daten für das Erstellen der Laufbahn-Daten gefunden.")
    public Response exportGostSchuelerLaufbahnplanungsdaten(@PathParam("schema") final String schema, @PathParam("id") final long id, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).exportJSON(id),
        		request, ServerMode.STABLE, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }

    /**
     * Die OpenAPI-Methode für den Import von Laufbahnplanungsdaten eines Schülers der gymnasialen Oberstufe
     * mit der angegebenen ID.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param id            die ID des Schülers
     * @param daten         die Laufbahnplanungsdaten
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return Rückmeldung, ob die Operation erfolgreich war mit dem Log der Operation
     */
    @POST
    @Path("/schueler/{id : \\d+}/laufbahnplanung/daten")
    @Operation(summary = "Importiert die Laufbahndaten aus den übergebenen Laufbahnplanungsdaten.",
               description = "Importiert die Laufbahndaten aus den übergebenen Laufbahnplanungsdaten")
    @ApiResponse(responseCode = "200", description = "Der Log vom Import der Laufbahndaten",
    			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "409", description = "Es ist ein Fehler beim Import aufgetreten. Ein Log vom Import wird zurückgegeben.",
    			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Der Benutzer hat keine Berechtigung, um die Laufbahndaten zu importieren.")
    public Response importGostSchuelerLaufbahnplanungsdaten(@PathParam("schema") final String schema,
    		@PathParam("id") final long id,
            @RequestBody(description = "Die Laufbahnplanungsdaten", required = false, content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = GostLaufbahnplanungDaten.class))) final GostLaufbahnplanungDaten daten,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).importJSON(id, daten),
        		request, ServerMode.STABLE, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }


    /**
     * Die OpenAPI-Methode für den Import von Laufbahnplanungsdaten mehrerer Schüler der gymnasialen Oberstufe.
     *
     * @param schema        das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param multipart     Die Laufbahnplanungsdateien als GZIP-komprimierte JSON
     * @param request       die Informationen zur HTTP-Anfrage
     *
     * @return Rückmeldung, ob die Operation erfolgreich war mit dem Log der Operation
     */
    @POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/laufbahnplanung/import")
    @Operation(summary = "Importiert die Laufbahndaten aus den übergebenen Laufbahnplanungsdatein.",
               description = "Importiert die Laufbahndaten aus den übergebenen Laufbahnplanungsdatein")
    @ApiResponse(responseCode = "200", description = "Der Log vom Import der Laufbahndaten",
    			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "409", description = "Es ist ein Fehler beim Import aufgetreten. Ein Log vom Import wird zurückgegeben.",
    			content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleOperationResponse.class)))
    @ApiResponse(responseCode = "403", description = "Der Benutzer hat keine Berechtigung, um die Laufbahndaten zu importieren.")
    public Response importGostSchuelerLaufbahnplanungen(@PathParam("schema") final String schema,
    		@RequestBody(description = "Die Laufbahnplanungsdatein", required = true, content =
			@Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = MultipleBinaryMultipartBody.class))) @MultipartForm final MultipartFormDataInput multipart,
    		@Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).importGZip(multipart),
        		request, ServerMode.STABLE, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }


    /**
     * Die OpenAPI-Methode für die Abfrage der Laufbahnplanungsdaten der gymnasialen Oberstufe
     * in Bezug auf die angegebenen Schüler als GZIP-Json.
     *
     * @param schema    das Datenbankschema, auf welches die Abfrage ausgeführt werden soll
     * @param ids       die Liste der IDs der Schüler
     * @param request   die Informationen zur HTTP-Anfrage
     *
     * @return die Laufbahnplanungsdaten
     */
    @POST
    @Produces("application/zip")
    @Path("/laufbahnplanung/export")
    @Operation(summary = "Liefert die Laufbahnplanungsdaten der gymnasialen Oberstufe für die angegebenen Schüler (GZip-komprimiert).",
    	description = "Liest die Laufbahnplanungsdaten der gymnasialen Oberstufe für die angegebenen Schüler aus der Datenbank "
    			+ "und liefert diese GZip-komprimiert zurück. Dabei wird geprüft, ob der SVWS-Benutzer die "
    			+ "notwendige Berechtigung zum Auslesen der Daten besitzt.")
    @ApiResponse(responseCode = "200", description = "Die GZip-komprimierten Laufbahndaten der gymnasialen Obertufe",
                 content = @Content(mediaType = "application/zip",
                 schema = @Schema(type = "string", format = "binary", description = "Die GZip-komprimierten Laufbahndaten der gymnasialen Obertufe")))
    @ApiResponse(responseCode = "403", description = "Der SVWS-Benutzer hat keine Rechte, um die Laufbahndaten auszulesen.")
    @ApiResponse(responseCode = "404", description = "Es wurden nicht alle benötigten Daten für das Erstellen der Laufbahn-Daten gefunden.")
    public Response exportGostSchuelerLaufbahnplanungen(@PathParam("schema") final String schema,
    		@RequestBody(description = "Die Liste der IDs der Schüler", required = true, content =
				@Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = Long.class))))
    		final List<Long> ids, @Context final HttpServletRequest request) {
    	return DBBenutzerUtils.runWithTransaction(conn -> new DataGostSchuelerLaufbahnplanung(conn).exportGZip(ids),
        		request, ServerMode.STABLE, BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN);
    }


}
