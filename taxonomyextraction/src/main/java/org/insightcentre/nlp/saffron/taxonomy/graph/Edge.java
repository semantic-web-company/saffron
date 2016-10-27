package org.insightcentre.nlp.saffron.taxonomy.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties({"from", "to"})
public class Edge implements Comparable<Edge> {

  private final Node from, to;
  private final double weight;

  public Edge(final Node argFrom, final Node argTo, final double argWeight) {
    from = argFrom;
    to = argTo;
    weight = argWeight;
  }

  public Node getFrom() {
    return from;
  }

  public Node getTo() {
    return to;
  }

  public double getWeight() {
    return weight;
  }

  public int getSrc() {
      return from.getName();
  }

  public int getTrg() {
      return to.getName();
  }

  @Override
  public int compareTo(final Edge argEdge) {
    if (weight == argEdge.weight) {
      return 0;
    } else if ((weight > argEdge.weight)) {
      return 1;
    } else {
      return -1;
    }
  }
}
