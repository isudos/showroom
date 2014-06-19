package org.sudos.wiki.data.crude;

import java.util.UUID;

import org.sudos.wiki.data.model.Document;

/**
 * @author iv
 * 
 *         A builder that facilitates creation of document.
 * 
 * 
 */
public interface DocumentBuilder {

	void withId(UUID id);

	void withTitle(String title);

	void withStructuredField(String name, String value);

	void withUnstructuredField(String name, String value);

	void withOtherField(String name, String value);

	Document build();

}
