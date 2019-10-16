package org.insightcentre.nlp.saffron.data.connections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * The link between a document and a topic
 * @author John McCrae &lt;john@mccr.ae&gt;
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentTopic {
	
	public static final String JSON_DOCUMENT_ID = "document_id";
	@JsonProperty(JSON_DOCUMENT_ID)
    private final String documentId;
	
	public static final String JSON_TERM_STRING = "term_string";
	@JsonProperty(JSON_TERM_STRING)
    private String termString;
	
	public static final String JSON_OCCURRENCES = "occurrences";
	@JsonProperty(JSON_OCCURRENCES)
    private final int occurrences;
	
	public static final String JSON_PATTERN = "pattern";
	@JsonProperty(JSON_PATTERN)
    private final String pattern;
	
	public static final String JSON_ACRONYM = "acronym";
	@JsonProperty(JSON_ACRONYM)
    private final String acronym;
	
    //public final double score;
	
	public static final String JSON_TF_IDF = "tfidf";
	@JsonProperty(JSON_TF_IDF)
    private Double tfIdf;
	
    //public Integer unembedded_occ;

    @JsonCreator
    public DocumentTopic(@JsonProperty(value=JSON_DOCUMENT_ID,required=true) String document_id, 
                         @JsonProperty(value=JSON_TERM_STRING,required=true) String topic_string, 
                         @JsonProperty(JSON_OCCURRENCES) int occurences, 
                         @JsonProperty(JSON_PATTERN) String pattern, 
                         @JsonProperty(JSON_ACRONYM) String acronym,
                         @JsonProperty(JSON_TF_IDF) Double tfidf) {
        this.documentId = document_id;
        this.termString = topic_string;
        this.occurrences = occurences;
        this.pattern = pattern;
        this.acronym = acronym;
        this.tfIdf = tfidf;
    }

    public String getTermString() {
		return termString;
	}

	public void setTermString(String termString) {
		this.termString = termString;
	}

	public Double getTfIdf() {
		return tfIdf;
	}

	public void setTfIdf(Double tfIdf) {
		this.tfIdf = tfIdf;
	}

	public String getDocumentId() {
		return documentId;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public String getPattern() {
		return pattern;
	}

	public String getAcronym() {
		return acronym;
	}

	@Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.documentId);
        hash = 11 * hash + Objects.hashCode(this.termString);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocumentTopic other = (DocumentTopic) obj;
        if (!Objects.equals(this.documentId, other.documentId)) {
            return false;
        }
        if (!Objects.equals(this.termString, other.termString)) {
            return false;
        }
        return true;
    }

    

}
