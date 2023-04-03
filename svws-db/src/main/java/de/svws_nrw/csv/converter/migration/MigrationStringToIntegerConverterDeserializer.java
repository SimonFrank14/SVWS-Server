package de.svws_nrw.csv.converter.migration;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import de.svws_nrw.db.converter.current.StringToIntegerConverter;

/**
 * Diese Klasse ist ein Deserialisierer für Strinwerte. Sie deserialisiert die
 * Datenbankdarstellung als Integer.
 */
public final class MigrationStringToIntegerConverterDeserializer extends StdDeserializer<Integer> {

	private static final long serialVersionUID = 899602939694388520L;

	/**
	 * Erzeugt einen neuen Deerialisierer
	 */
	public MigrationStringToIntegerConverterDeserializer() {
		super(Integer.class);
	}

	/**
	 * Erzeugt einen neuen Deserialisierer unter Angabe der {@link Class}
	 *
	 * @param t   das Klassen-Objekt
	 */
	protected MigrationStringToIntegerConverterDeserializer(final Class<Integer> t) {
		super(t);
	}

	@Override
	public Integer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return StringToIntegerConverter.instance.convertToEntityAttribute(p.getText());
	}

}
