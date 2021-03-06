package org.insightcentre.nlp.saffron.topic.ngrams;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.insightcentre.nlp.saffron.data.Term;

/**
 *
 * @author John McCrae &lt;john@mccr.ae&gt;
 */
public class FilterNGrams {

    private static void badOptions(OptionParser p, String message) throws IOException {
        System.err.println("Error: " + message);
        p.printHelpOn(System.err);
        System.exit(-1);
    }

    public static void main(String[] args) {
        try {
            // Parse command line arguments
            final OptionParser p = new OptionParser() {
                {
                    accepts("t", "The list of terms to read").withRequiredArg().ofType(File.class);
                    accepts("c", "The configuration to use").withRequiredArg().ofType(File.class);
                    accepts("o", "The list of terms to write to").withRequiredArg().ofType(File.class);
                }
            };
            final OptionSet os;

            try {
                os = p.parse(args);
            } catch (Exception x) {
                badOptions(p, x.getMessage());
                return;
            }

            final File input = (File) os.valueOf("t");
            if (input == null || !input.exists()) {
                badOptions(p, "Input does not exist");
            }
            final File configFile = (File) os.valueOf("c");
            if (configFile == null || !configFile.exists()) {
                badOptions(p, "Configuration does not exist");
            }
            final File output = (File) os.valueOf("o");
            if (output == null) {
                badOptions(p, "Output not specified");
            }

            ObjectMapper mapper = new ObjectMapper();
            Configuration config = mapper.readValue(configFile, Configuration.class);

            List<Term> outTerms = new ArrayList<>();
            Iterator<Term> terms = mapper.readValues(new JsonFactory().createParser(input), Term.class);
            while (terms.hasNext()) {
                Term term = terms.next();
                if (!is_invalid(term)) {
                    outTerms.add(term);
                }
            }

            mapper.writeValue(output, outTerms);

        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(-1);
        }
    }

    private static boolean is_invalid(Term term) {
        if(term.getDbpediaUrl() != null)
            return false;
        String[] words = term.getString().split("\\s+");
        // The original Saffron implementation only filters terms of exactly
        // Length 2... We should probably fix this
        if(words.length != 2)
            return false;
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class Configuration {

        File database = new File("models/ngrams.db");

        public File getDatabase() {
            return database;
        }

        public void setDatabase(File database) {
            this.database = database;
        }

    }

}
