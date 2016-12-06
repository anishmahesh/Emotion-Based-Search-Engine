package edu.nyu.cs.cs2580;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
public class DocumentIndexed extends Document {
  private static final long serialVersionUID = 9184892508124423115L;
  private int totalTerms;
  private double anger;
  private double disgust;
  private double fear;
  private double joy;
  private double sadness;

  public void setAnger(double anger) {
    this.anger = anger;
  }

  public void setDisgust(double disgust) {
    this.disgust = disgust;
  }

  public void setFear(double fear) {
    this.fear = fear;
  }

  public void setJoy(double joy) {
    this.joy = joy;
  }

  public void setSadness(double sadness) {
    this.sadness = sadness;
  }

  public double getAnger() {
    return this.anger;
  }

  public double getDisgust() {
    return this.disgust;
  }

  public double getFear() {
    return this.fear;
  }

  public double getJoy() {
    return this.joy;
  }

  public double getSadness() {
    return this.sadness;
  }

  public void setTotalTerms(int total){
    totalTerms = total;
  }

  public int getTotalTerms(){
    return totalTerms;
  }

  public DocumentIndexed(int docid) {
    super(docid);
  }
}
