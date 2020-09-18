package org.insightcentre.nlp.saffron.config;

import org.insightcentre.nlp.saffron.data.SaffronPath;

/**
 * Configuration for the Knowledge Graph Extraction step
 * 
 * @author Bianca Pereira
 *
 */
public class KnowledgeGraphExtractionConfiguration {

	public SaffronPath kerasModelFile = new SaffronPath("../models/model_keras.h5");
	
	public SaffronPath bertModelFile = new SaffronPath("../models/bert_model");
	
	public double synonymyThreshold = 0.5;
	
	public double meronomyThreshold = 0.25;

	public boolean enableSynonymyNormalisation = false;
}
