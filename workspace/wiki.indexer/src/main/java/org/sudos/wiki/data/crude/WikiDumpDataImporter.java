package org.sudos.wiki.data.crude;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.UUID;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.sudos.wiki.data.indexer.IndexingException;
import org.sudos.wiki.data.model.Document;
import org.sudos.wiki.data.model.FieldSemanticMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author iv
 * 
 *         The initial importer of wiki dump xml data to be indexed.
 *         WikiDumpDataImporter is responsible for: parsing and validating xml,
 *         distinguishing structured data (like title and category list) from
 *         unstructured data (like wiki text); Whether data structured or not is
 *         defined by loading some config file, named fieldSemantics.txt. TODO:
 *         If there is no such file then we consider too long fields as
 *         unstructured
 * 
 *         The importing process is carried out sequentially, to prevent extreme
 *         bulky data on heap. Wiki pages parsed one by one, and on each page a
 *         chain of postprocessors is invoked in a callback fashion.
 * 
 */
public class WikiDumpDataImporter implements CrudeDataImporter {

	private static final String UNSTRUCTURED = "UNSTRUCTURED";

	private static final String STRUCTURED = "STRUCTURED";

	private static final String FIELD_SEMANTIC_TXT = "field_semantics.txt";

	private final InputStream inputStream;

	private final SAXParser saxParser;

	/**
	 * @author iv The handler to process document's elements one by one at the
	 *         moment we pull them form xml. After each document is pulled we
	 *         invoke postprocessor callback.
	 * 
	 * 
	 */
	public class WikiArticleHandler extends DefaultHandler {

		private final DataPostprocessor<Document> postprocessor;
		private final FieldSemanticMap fieldSemanticMap;
		private final DocumentBuilder docBuilder = new DefaultDocumentBuilder();

		private boolean isParsingPage = false;
		private boolean isParsingFieldText = false;

		private StringBuilder currentFieldText = new StringBuilder();

		private Document outputDoc;

		private String currentField;

		private boolean isHandlingStructuredField = false;
		private boolean isHandlingOtherField = false;

		public WikiArticleHandler(DataPostprocessor<Document> postprocessor,
				FieldSemanticMap fieldSemanticMap) {
			this.fieldSemanticMap = fieldSemanticMap;
			this.postprocessor = postprocessor;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName.equals("page")) {
				isParsingPage = true;
			} else if (isParsingPage) {
				if (fieldSemanticMap.containsField(qName)) {
					isHandlingStructuredField = fieldSemanticMap
							.isStructured(qName);
					isHandlingOtherField = false;
				} else {
					isHandlingOtherField = true;
				}
				currentField = qName;
				isParsingFieldText = true;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			if (isParsingFieldText)
				currentFieldText.append(new String(ch, start, length));

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (qName.equals("mediawiki")) {
				isParsingPage = false;
			} else if (qName.equals("page")) {
				isParsingPage = false;

				UUID id = UUID.randomUUID();

				docBuilder.withId(id);
				outputDoc = docBuilder.build();
				System.out.println("Importing document: ["
						+ outputDoc.getTitle() + " with ID:" + id + "]");
				if (postprocessor != null) {
					try {
						postprocessor.postprocess(outputDoc);
					} catch (IndexingException e) {
						System.out
								.println("Can't postprocess and index document: ["
										+ outputDoc.getTitle()
										+ " --- with id: "
										+ outputDoc.getId()
										+ "]. Cause: " + e.getMessage());
					}
				}
			} else if (isParsingFieldText) {
				if (isHandlingOtherField) {
					docBuilder.withOtherField(currentField,
							currentFieldText.toString());
				} else if (isHandlingStructuredField) {
					docBuilder.withStructuredField(currentField,
							currentFieldText.toString());
				} else {
					docBuilder.withUnstructuredField(currentField,
							currentFieldText.toString());
				}
				currentFieldText = new StringBuilder();
				isParsingFieldText = false;
			}
		}

		public Document getDocument() {
			return this.outputDoc;
		}
	}

	public WikiDumpDataImporter(String fileName) throws Exception {

		inputStream = new FileInputStream(new File(fileName));

		SAXParserFactory factory = SAXParserFactory.newInstance();
		this.saxParser = factory.newSAXParser();
	}

	/**
	 * TODO: use this loaded config to manipulate field creation.
	 * 
	 * @return
	 */
	private FieldSemanticMap loadFieldSemanticMap() {
		try {
			String fileName = Thread.currentThread().getContextClassLoader()
					.getResource(FIELD_SEMANTIC_TXT).getFile();

			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					fileName));
			String currentLine;
			FieldSemanticMap result = new FieldSemanticMap();
			while ((currentLine = bufferedReader.readLine()) != null) {
				String parts[] = currentLine.split(":");
				if (parts.length > 0 && parts[0].equals(STRUCTURED)) {
					for (String field : parts[1].split(",")) {
						result.put(field, true);
					}
				}
				if (parts.length > 0 && parts[0].equals(UNSTRUCTURED)) {
					for (String field : parts[1].split(",")) {
						result.put(field, false);
					}
				}
			}

			bufferedReader.close();
		} catch (Exception ex) {
			System.out
					.println("Can't load fieldSemanticsFile. Working in default mode");
		}
		return FieldSemanticMap.defaultInstance();

	}

	public void importData(DataPostprocessor<Document> topPostprocessor)
			throws IndexingException {
		WikiArticleHandler articleHandler = new WikiArticleHandler(
				topPostprocessor, loadFieldSemanticMap());
		try {
			this.saxParser.parse(this.inputStream, articleHandler);
		} catch (Exception e) {
			throw new IndexingException("Whole indexing process failed!", e);
		}
	}
}
