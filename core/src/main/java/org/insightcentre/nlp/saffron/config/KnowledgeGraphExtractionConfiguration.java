package org.insightcentre.nlp.saffron.config;

import org.insightcentre.nlp.saffron.data.SaffronPath;

import java.io.File;

/**
 * Configuration for the Knowledge Graph Extraction step
 * 
 * @author Bianca Pereira
 *
 */
public class KnowledgeGraphExtractionConfiguration {

	public SaffronPath kerasModelFile = new SaffronPath(System.getenv("KERAS_MODEL"));
	
	public SaffronPath bertModelFile = new SaffronPath(System.getenv("BERT_MODEL"));
	
	public double synonymyThreshold = 0.5;
	
	public double meronomyThreshold = 0.25;

	public boolean enableSynonymyNormalisation = false;
}
