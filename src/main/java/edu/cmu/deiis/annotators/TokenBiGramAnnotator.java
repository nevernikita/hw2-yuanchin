package edu.cmu.deiis.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Token;
/*
 * The third step to analyze the artifact.
 * Generate 2-Gram type of NGram annotations by using the annotations(Question, Answer, Token) indexed in JCas object. 
 */
public class TokenBiGramAnnotator extends JCasAnnotator_ImplBase{
  
  public void process(JCas aJCas){
    AnnotationIndex<Annotation> tIndex = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> tIterator = tIndex.iterator();
    Token t1,t2;
    if (tIterator.hasNext()) {
      t1 = (Token)tIterator.next();
      while(tIterator.hasNext()){
        t2 = (Token)tIterator.next();
        if(t2.getBegin() - t1.getEnd() <= 1)
        {
          NGram annotation = new NGram(aJCas);
          annotation.setBegin(t1.getBegin());
          annotation.setEnd(t2.getEnd());
          FSArray elemArray = new FSArray(aJCas,2); 
          elemArray.set(0, t1);
          elemArray.set(1, t2);
          annotation.setElements(elemArray);
          annotation.setElementType("Token");
          annotation.setCasProcessorId("TokenBiGramAnnotator");
          annotation.setConfidence(1);
          annotation.addToIndexes();
        }        
        t1 = t2;
      } 
    } 
       
  
  }

}
