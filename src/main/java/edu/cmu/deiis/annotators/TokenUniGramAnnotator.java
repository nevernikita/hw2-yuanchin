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
 * Generate 1-Gram type of NGram annotations by using the annotations(Question, Answer, Token) indexed in JCas object. 
 */
public class TokenUniGramAnnotator extends JCasAnnotator_ImplBase{
  
  
  public void process(JCas aJCas){
    AnnotationIndex<Annotation> tIndex = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> tIterator = tIndex.iterator();
    
    while(tIterator.hasNext()){
      Token t = (Token)tIterator.next();
      NGram annotation = new NGram(aJCas);
      annotation.setBegin(t.getBegin());
      annotation.setEnd(t.getEnd());
      FSArray elemArray = new FSArray(aJCas,1); 
      elemArray.set(0, t);
      annotation.setElements(elemArray);
      annotation.setElementType("Token");
      annotation.setCasProcessorId("TokenUniGramAnnotator");
      annotation.setConfidence(1);
      annotation.addToIndexes();
    }
    
  }
  

}

