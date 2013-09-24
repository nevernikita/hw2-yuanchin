package edu.cmu.deiis.annotators;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Question;

/*
 * The fourth step to analyze the artifact.
 * Get AnswerScore annotations from the passed JCas object.
 * Assign a score to each answer using N-Gram(all of the 1-Gram, 2-Gram, 3-Gram) overlap scoring method.
 */
public class AnswerScoreAnnotator extends JCasAnnotator_ImplBase{
  
  public void process(JCas aJCas){
    
    /* Get annotations from the aJCas object passed. */
    AnnotationIndex<Annotation> qIndex = aJCas.getAnnotationIndex(Question.type);
    AnnotationIndex<Annotation> aIndex = aJCas.getAnnotationIndex(Answer.type);    
    AnnotationIndex<Annotation> nIndex = aJCas.getAnnotationIndex(NGram.type);
    FSIterator<Annotation> qIterator = qIndex.iterator();
    FSIterator<Annotation> aIterator = aIndex.iterator();
    FSIterator<Annotation> nIterator = nIndex.iterator();
    
    /* Get all the NGram annotations indexed in JCas object */
    ArrayList<NGram> ngramList = new ArrayList<NGram>();
    while(nIterator.hasNext()){
      ngramList.add((NGram)nIterator.next());
    }
    int nGramNum = ngramList.size();
    
    Question q = (Question)qIterator.next();
    int qBeginPos = q.getBegin();
    int qEndPos = q.getEnd();
    ArrayList<String> qNGramList = new ArrayList<String>();
    
    /* Get all the NGram annotations in the Question */
    int i;
    for(i = 0;i < nGramNum;i++)
    {
      if((ngramList.get(i).getBegin() >= qBeginPos) && (ngramList.get(i).getEnd() <= qEndPos)){
        qNGramList.add(ngramList.get(i).getCoveredText());
      }
      else {
        break;
      }
    }   
    
    /* Score each answer. */
    while(aIterator.hasNext()){
      Answer a = (Answer)aIterator.next();
      int aBeginPos = a.getBegin();
      int aEndPos = a.getEnd();
      ArrayList<String> aNGramList = new ArrayList<String>();
      /* Get all the NGram annotations in the Answer */
      for(;i < nGramNum;i++){
        if((ngramList.get(i).getBegin() >= aBeginPos) && (ngramList.get(i).getEnd() <= aEndPos)){
          aNGramList.add(ngramList.get(i).getCoveredText());
        }
        else {
          break;
        }
      }
      
      /* calculate the score using NGram overlap method */
      int qNum = qNGramList.size();
      int aNum = aNGramList.size();
      int count = 0;
      for(int j = 0;j < qNum;j++)
      {
        String qNGram_s = qNGramList.get(j);
        for(int k = 0;k < aNum;k++){
          if(aNGramList.get(k).equals(qNGram_s))
          {
            count++;
          }
        }
      }
      AnswerScore annotation = new AnswerScore(aJCas);
      annotation.setBegin(a.getBegin());
      annotation.setEnd(a.getEnd());
      annotation.setCasProcessorId("AnswerScore");
      annotation.setConfidence(1);
      annotation.setAnswer(a);
      annotation.setScore(((double)count)/((double)aNum));
      annotation.addToIndexes(); /* add the AnswerScore annotations into the JCas index */
    }
    
  }

}
