package edu.cmu.deiis.annotators;

import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Question;
/*
 * The last step to analyze the artifact.
 * Evaluate the overall performance of the scoring method on each document in terms of precision. 
 * As well as give the average precision on all the artifacts analyzed.
 * (Using member variables to record the number of overall correct answers and the number of overall true-positive predicted answers.)
 * Print summary results to the Console.
 */
public class Evaluator extends JCasAnnotator_ImplBase{
  private int allCorrectNum = 0; /* record the number of overall correct answers */
  private int allPredictNum = 0;  /* record the number of overall true-positive predicted answers */
  java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
    
  public void process(JCas aJCas){
    
    /* get all AnswerScore annotations from aJCas passed */
    AnnotationIndex<Annotation> sIndex = aJCas.getAnnotationIndex(AnswerScore.type);
    FSIterator<Annotation> sIterator = sIndex.iterator();    
    ArrayList<AnswerScore> sList = new ArrayList<AnswerScore>();
    
    int correctNum = 0; /* record the number of correct answers in current doc */
    
    while(sIterator.hasNext()){
      AnswerScore answerScore = (AnswerScore)sIterator.next();
      sList.add(answerScore);  
      if(answerScore.getAnswer().getIsCorrect())
      {
        correctNum++;
      }
      
    }
    
    allCorrectNum += correctNum;
    
    /* Sort the AnswerScore annotations in descending order using bubble sort*/
    AnswerScore temp;
    int num = sList.size();
    for(int i = 0;i < num;i++)
    {
      for(int j = i+1;j < num;j++)
      {
        if(sList.get(j).getScore() > sList.get(i).getScore()){
          temp = sList.get(i);
          sList.set(i, sList.get(j));
          sList.set(j, temp);
        }
      }
    }
    
    int predictNum = 0; /* record the number of true-positive predicted answers in current doc */
    for(int i = 0;i < correctNum;i++){
      if(sList.get(i).getAnswer().getIsCorrect())
        predictNum++;
    }
    
    allPredictNum += predictNum;
    
    double precision = (double)predictNum/(double)correctNum;
    printResult(aJCas, sList, precision,correctNum);
  }
  
  /*
   * Print summary results to the Console.
   */  
  private void printResult(JCas aJCas, ArrayList<AnswerScore> sList, double precision,int correctNum){
     
    AnnotationIndex<Annotation> qIndex = aJCas.getAnnotationIndex(Question.type);
    FSIterator<Annotation> qIterator = qIndex.iterator();
    System.out.println("Question: "+qIterator.next().getCoveredText());
    
    int aNum = sList.size();
    for(int i = 0;i < aNum;i++){
      if(sList.get(i).getAnswer().getIsCorrect())
      {
        System.out.println("+ " + df.format(sList.get(i).getScore()) + " " + sList.get(i).getCoveredText());
      }
      else 
      {
        System.out.println("- " + df.format(sList.get(i).getScore()) + " " + sList.get(i).getCoveredText());
      }
    }
    System.out.println("Precision at " + (double)correctNum + ": " + df.format(precision));
    System.out.println();
  }
  
  /*
   * Overrides the collectionProcessComplete() method to print the average precision at the end of processing the collection.
   */
  public void collectionProcessComplete()
  {
    System.out.println("Average Precision: " + df.format(((double)allPredictNum)/((double)allCorrectNum)));
  }
  



}
