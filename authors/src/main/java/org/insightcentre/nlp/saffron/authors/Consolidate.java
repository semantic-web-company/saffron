package org.insightcentre.nlp.saffron.authors;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.insightcentre.nlp.saffron.DefaultSaffronListener;
import org.insightcentre.nlp.saffron.SaffronListener;
import org.insightcentre.nlp.saffron.data.Author;
import org.insightcentre.nlp.saffron.data.Corpus;
import org.insightcentre.nlp.saffron.data.Document;
import org.insightcentre.nlp.saffron.data.index.DocumentSearcher;
import org.insightcentre.nlp.saffron.documentindex.DocumentSearcherFactory;

/**
 *
 * @author John McCrae &lt;john@mccr.ae&gt;
 */
public class Consolidate {
    private static void badOptions(OptionParser p, String message) throws IOException {
        System.err.println("Error: "  + message);
        p.printHelpOn(System.err);
        System.exit(-1);
    }

    public static void main(String[] args) {
        try {
            // Parse command line arguments
            final OptionParser p = new OptionParser() {{
                    accepts("t", "The input text corpus").withRequiredArg().ofType(File.class);
            }};
            final OptionSet os;

            try {
                os = p.parse(args);
            } catch(Exception x) {
                badOptions(p, x.getMessage());
                return;
            }

            final File corpusFile  = (File)os.valueOf("t");
            if(corpusFile == null || !corpusFile.exists()) {
                badOptions(p, "Corpus does not exist");
            }

            ObjectMapper mapper = new ObjectMapper();

            DocumentSearcher corpus        = DocumentSearcherFactory.load(corpusFile);

            Set<Author> authors = extractAuthors(corpus);

            Map<Author, Set<Author>> consolidation = new ConsolidateAuthors().consolidate(authors);

            applyConsolidation(corpus, consolidation, new DefaultSaffronListener());

            //mapper.writerWithDefaultPrettyPrinter().writeValue(output, corpus);

        } catch(Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }

    public static Set<Author> extractAuthors(Corpus corpus) {
        return extractAuthors(corpus, new DefaultSaffronListener());
    }

    public static Set<Author> extractAuthors(Corpus corpus, SaffronListener log) {
        Set<Author> authors = new HashSet<Author>();
        for(Document doc : corpus.getDocuments()) {
            for(Author author : doc.getAuthors()) {
                authors.add(author);
            }
        }
        return authors;
    }

    public static void applyConsolidation(DocumentSearcher corpus, Map<Author, Set<Author>> consolidation, SaffronListener log) {
        Map<Author, Author> rmap = new HashMap<>();
        for(Map.Entry<Author, Set<Author>> e1 : consolidation.entrySet()) {
            for(Author a1 : e1.getValue()) {
                rmap.put(a1, e1.getKey());
            }
        }
                
        Collection<Document> updateDocuments = new AbstractCollection<Document>() {
            final Iterator<Document> iter = corpus.getDocuments().iterator();

            @Override
            public Iterator<Document> iterator() {
                return new Iterator<Document>() {
                    @Override
                    public Document next() {
                        Document document = iter.next();
                        List<Author> authors2 = new ArrayList<>();
                        for (Author a : document.authors) {
                            if(!rmap.containsKey(a))
                                throw new RuntimeException("Author not found in consolidation");
                            authors2.add(rmap.get(a));
                        }
                        Document doc2 = new Document(document.file, document.id, document.url,
                                document.name, document.mimeType, authors2, document.metadata,
                                document.getContents(), document.date);
                        return doc2;
                    }

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }
                };
            }

            @Override

            public int size() {
                throw new UnsupportedOperationException("Size not known");
            }
        };
        corpus.updateDocuments(updateDocuments);
    }

}
