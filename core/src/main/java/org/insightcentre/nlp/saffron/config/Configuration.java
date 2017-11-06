package org.insightcentre.nlp.saffron.config;

/**
 * The configuration of a Saffon run
 * @author John McCrae <john@mccr.ae>
 */
public class Configuration {
    public TermExtractionConfiguration termExtraction = new TermExtractionConfiguration();
    public AuthorTopicConfiguration authorTopic = new AuthorTopicConfiguration();
    public AuthorSimilarityConfiguration authorSim = new AuthorSimilarityConfiguration();
    public TopicSimilarityConfiguration topicSim = new TopicSimilarityConfiguration();
    public TaxonomyExtractionConfiguration taxonomy = new TaxonomyExtractionConfiguration();
    
}