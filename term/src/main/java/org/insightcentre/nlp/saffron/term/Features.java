package org.insightcentre.nlp.saffron.term;

import java.util.Set;
import org.insightcentre.nlp.saffron.SaffronListener;
import org.insightcentre.nlp.saffron.config.TermExtractionConfiguration;
import org.insightcentre.nlp.saffron.term.domain.DomainStats;
import org.insightcentre.nlp.saffron.term.lda.NovelTopicModel;

/**
 *
 * @author John McCrae &lt;john@mccr.ae&gt;
 */
public class Features {

    public static double calcFeature(
            TermExtractionConfiguration.Feature feat,
            String term, FrequencyStats stats, Lazy<FrequencyStats> ref,
            Lazy<InclusionStats> incl, Lazy<NovelTopicModel> topicModel,
            Lazy<DomainStats> domain, TemporalFrequencyStats tempStats,
            SaffronListener log) throws IntervalTooLong {
        switch (feat) {
            case weirdness:
                return weirdness(term, stats, ref.get());
            case avgTermFreq:
                return aveTermFreq(term, stats);
            case basic:
                return basic(term, 0.75, stats, incl.get());
            case comboBasic:
                return basicCombo(term, 0.75, 0.1, stats, incl.get());
            case cValue:
                return cValue(term, stats, incl.get());
            case relevance:
                return relevance(term, stats, ref.get());
            case residualIdf:
                return residualIDF(term, stats);
            case totalTfIdf:
                return totalTFIDF(term, stats);
            case novelTopicModel:
                return topicModel.get().novelTopicModel(term, stats);
            case postRankDC:
                return domain.get().score(term);
            case futureBasic:
                if(tempStats == null) throw new IllegalArgumentException("Cannot calculate future statistics without an interval");
                return futureBasic(term, 0.75, tempStats, incl.get(), 0.2, log);
            case futureComboBasic:
                if(tempStats == null) throw new IllegalArgumentException("Cannot calculate future statistics without an interval");
                return futureBasicCombo(term, 0.75, 0.1, tempStats, incl.get(), 0.2, log);
            default:
                throw new UnsupportedOperationException("Feature not supported: " + feat);
        }
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private static double EPS = 0.1;

    public static double weirdness(String term, FrequencyStats stats, FrequencyStats ref) {
        int i = stats.termFrequency.getInt(term);
        int j = ref.termFrequency.getInt(term);
        return ((double) i + EPS) * (ref.tokens + EPS) / ((double) j + EPS) / (stats.tokens + EPS);
    }

    public static double aveTermFreq(String term, FrequencyStats stats) {
        double i = stats.termFrequency.getInt(term);
        double j = stats.docFrequency.getInt(term);
        return j == 0 ? 0.0 : i / j;
    }

    public static double residualIDF(String term, FrequencyStats stats) {
        double tf = stats.termFrequency.getInt(term) + EPS;
        double df = stats.docFrequency.getInt(term) + EPS;
        assert (df <= tf);
        assert (df <= stats.documents);
        double theta = tf / stats.documents;
        return tf * (log2(1 - Math.exp(-theta)) - log2(df / stats.documents));
    }

    public static double totalTFIDF(String term, FrequencyStats stats) {
        double tf = stats.termFrequency.getInt(term);
        double df = stats.docFrequency.getInt(term);
        if (df == 0) {
            return 0.0;
        }
        return tf * Math.log((double) stats.documents / df);
    }

    public static double cValue(String term, FrequencyStats freq, InclusionStats incl) {
        double tf = freq.termFrequency.getInt(term) + EPS;
        double t = term.split(" ").length;
        double subtf = 0.0;
        if (incl.subTerms.containsKey(term)) {
            final Set<String> subterms = incl.superTerms.get(term);
            if (subterms != null) {
                for (String subterm : subterms) {
                    subtf += freq.termFrequency.getInt(subterm) + EPS;
                }
                subtf /= subterms.size();
            }
        }
        return log2(t + 0.1) * (tf - subtf);
    }

    public static double basic(String term, double alpha, FrequencyStats freq, InclusionStats incl) {
        double tf = freq.termFrequency.getInt(term) + EPS;
        double t = term.split(" ").length;
        double et = incl.superTerms.containsKey(term) ? incl.superTerms.get(term).size() : 0.0;
        return t * log2(tf) + alpha * et;
    }
    
    public static double futureBasic(String term, double alpha, TemporalFrequencyStats freq, InclusionStats incl, double futureAmount, SaffronListener log) throws IntervalTooLong {
        double tf = freq.predict(term, (int)(freq.freqs.size() * futureAmount), 2);
        double t = term.split(" ").length;
        double et = incl.superTerms.containsKey(term) ? incl.superTerms.get(term).size() : 0.0;
        return t * log2(tf) + alpha * et;
    }

    public static double basicCombo(String term, double alpha, double beta, FrequencyStats freq, InclusionStats incl) {
        double tf = freq.termFrequency.getInt(term) + EPS;
        double t = term.split(" ").length;
        double et = incl.superTerms.containsKey(term) ? incl.superTerms.get(term).size() : 0.0;
        double et2 = incl.subTerms.getInt(term);
        return t * log2(tf) + alpha * et + beta * et2;
    }

    
    public static double futureBasicCombo(String term, double alpha, double beta, TemporalFrequencyStats freq, InclusionStats incl, double futureAmount, SaffronListener log) throws IntervalTooLong {
        double tf = freq.predict(term, (int)(freq.freqs.size() * futureAmount), 2);
        double t = term.split(" ").length;
        double et = incl.superTerms.containsKey(term) ? incl.superTerms.get(term).size() : 0.0;
        double et2 = incl.subTerms.getInt(term);
        return t * log2(tf) + alpha * et + beta * et2;
    }

    
    public static double relevance(String term, FrequencyStats freq, FrequencyStats ref) {
        double ntf1 = ((double) freq.termFrequency.getInt(term) + EPS) / (freq.tokens + EPS);
        double df = (double) freq.docFrequency.getInt(term) / freq.documents;
        double ntf2 = ((double) ref.termFrequency.getInt(term) + EPS) / (ref.tokens + EPS);

        return 1.0 - 1.0 / (log2(2.0 + ntf1 * df / ntf2));
    }
}
