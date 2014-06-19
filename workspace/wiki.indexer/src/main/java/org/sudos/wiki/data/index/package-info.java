/*
 * This package contains implementation of Index interface.
 * OnDiskIndex is a inverted index implementation, that stores posting lists on disk;
 * Index on disk is scattered across several segment files to facilitate indexing and retrieval.
 * Cached index is supposed to be in memory cache of particular postings 
 * 
 */
package org.sudos.wiki.data.index;

