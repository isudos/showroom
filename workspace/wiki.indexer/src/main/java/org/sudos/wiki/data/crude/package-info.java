/*
 * The classes of given package serves to organize intial importing of the data to be indexed.
 * CrudeDataImporter scans the data and loads it into a memory. Then, loaded data is delegated
 * to a chain of postprocessors.
 * It is assumed, that the data structure is unknown, and CrudeDataImporter is responsible 
 * for analysis and distribution of the data to unstructured parts and structured parts.
 * 
 * 
 */
package org.sudos.wiki.data.crude;

