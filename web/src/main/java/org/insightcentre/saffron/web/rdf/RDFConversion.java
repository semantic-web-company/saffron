package org.insightcentre.saffron.web.rdf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.insightcentre.nlp.saffron.data.Author;
import org.insightcentre.nlp.saffron.data.Document;
import org.insightcentre.nlp.saffron.data.Topic;
import org.insightcentre.nlp.saffron.data.connections.AuthorAuthor;
import org.insightcentre.nlp.saffron.data.connections.AuthorTopic;
import org.insightcentre.nlp.saffron.data.connections.DocumentTopic;
import org.insightcentre.nlp.saffron.data.connections.TopicTopic;
import org.insightcentre.saffron.web.SaffronData;


/**
 *
 * @author John McCrae
 */
public class RDFConversion {
    
    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch(UnsupportedEncodingException x) {
            throw new RuntimeException(x);
        }
    }
    
    public static Model documentToRDF(Document d, SaffronData data) {
        Model model = ModelFactory.createDefaultModel();

        Resource res = model.createResource("")
                .addProperty(RDF.type, FOAF.Document);
        if (d.getName() != null) {
            res.addLiteral(RDFS.label, d.getName());
        }
        if (d.url != null) {
            res.addProperty(RDFS.seeAlso, model.createResource(d.url.toString()));
        }
        if (d.mimeType != null) {
            res.addLiteral(DCTerms.type, d.getMimeType());
        }

        for (DocumentTopic dt : data.getTopicByDoc(d.id)) {
            res.addProperty(DCTerms.subject, model.createResource("../topic/" + encode(dt.topic_string)));
        }

        model.setNsPrefix("foaf", FOAF.NS);
        model.setNsPrefix("saffron", SAFFRON.NS);
        model.setNsPrefix("dct", DCTerms.NS);

        return model;
    }

    public static Model topicToRDF(Topic t, SaffronData data) {
        Model model = ModelFactory.createDefaultModel();

        Resource res = model.createResource("")
                .addProperty(RDF.type, SKOS.Concept)
                .addProperty(RDFS.label, t.topicString)
                .addProperty(SAFFRON.occurrences, model.createTypedLiteral(t.occurrences))
                .addProperty(SAFFRON.matches, model.createTypedLiteral(t.matches))
                .addProperty(SAFFRON.score, model.createTypedLiteral(t.score));
        if (t.dbpedia_url != null) {
            res.addProperty(SKOS.exactMatch, model.createResource(t.dbpedia_url.toString()));
        }
        for (Topic.MorphologicalVariation mv : t.mvList) {
            res.addProperty(SAFFRON.morphologicalVariant,
                    model.createResource()
                    .addProperty(RDF.value, mv.string)
                    .addProperty(SAFFRON.occurrences,
                            model.createTypedLiteral(mv.occurrences)));
        }
        
        for(AuthorTopic at : data.getAuthorByTopic(t.topicString)) {
            res.addProperty(SAFFRON.author, 
                    model.createResource("../author/" + encode(at.author_id)));
        }
        
        for(TopicTopic tt : data.getTopicByTopic1(t.topicString)) {
            res.addProperty(SKOS.related,
                    model.createResource(encode(tt.topic2)));
        }

        model.setNsPrefix("foaf", FOAF.NS);
        model.setNsPrefix("saffron", SAFFRON.NS);
        model.setNsPrefix("dct", DCTerms.NS);
        
        return model;
    }
    
    public static Model authorToRdf(Author author, SaffronData data) {
        Model model = ModelFactory.createDefaultModel();

        Resource res = model.createResource("")
                .addProperty(RDF.type, FOAF.Person)
                .addLiteral(FOAF.name, author.name);
        for(String variant : author.nameVariants) {
            res.addLiteral(FOAF.nick, variant);
        }
        for(AuthorAuthor aa : data.getAuthorSimByAuthor1(author.id)) {
            res.addProperty(SAFFRON.relatedAuthor, model.createResource(encode(aa.author2_id)));
        }
        for(AuthorTopic at : data.getTopicByAuthor(author.id)) {
            res.addProperty(SAFFRON.authorTopic, model.createResource("../topic/" + encode(at.topic_id)));
        }
        for(Document d : data.getDocsByAuthor(author.id)) {
            res.addProperty(FOAF.made, "../doc/" + encode(d.id));
        }
        
        model.setNsPrefix("foaf", FOAF.NS);
        model.setNsPrefix("saffron", SAFFRON.NS);
        model.setNsPrefix("dct", DCTerms.NS);
        
        return model;
        
    }
}
