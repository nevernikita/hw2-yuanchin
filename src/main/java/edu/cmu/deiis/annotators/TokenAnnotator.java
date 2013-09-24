package edu.cmu.deiis.annotators;

import java.util.regex.*;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;





import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Token;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/*
 * The second step to analyze the artifact.
 * Get the Token annotations on the basis of the Question and Answer annotation generated from the first step.
 * Parse tokens from the sentences using punctuation, space, etc.
 */
public class TokenAnnotator extends JCasAnnotator_ImplBase{
  private Pattern tokenPattern = Pattern.compile("[\\w]+[\\W]");
  
  public void process(JCas aJCas){
    AnnotationIndex<Annotation> qIndex = aJCas.getAnnotationIndex(Question.type);
    AnnotationIndex<Annotation> aIndex = aJCas.getAnnotationIndex(Answer.type);
    FSIterator<Annotation> qIterator = qIndex.iterator();
    FSIterator<Annotation> aIterator = aIndex.iterator();
    
    while(qIterator.hasNext()){
      Question q = (Question) qIterator.next();
      String elemText = q.getCoveredText();
      Matcher matcher = tokenPattern.matcher(elemText);
      int pos = 0;
      while(matcher.find(pos)){
        Token annotation = new Token(aJCas);
        annotation.setBegin(q.getBegin() + matcher.start());
        annotation.setEnd(q.getBegin() + matcher.end() - 1);
        annotation.setCasProcessorId("TokenAnnotator");
        annotation.setConfidence(1);
        annotation.addToIndexes();
        pos = matcher.end();
      }
    }
    
    while(aIterator.hasNext()){
      Answer a = (Answer)aIterator.next();
      String elemText = a.getCoveredText();
      Matcher matcher = tokenPattern.matcher(elemText);
      int pos = 0;
      while(matcher.find(pos)){
        Token annotation = new Token(aJCas);
        annotation.setBegin(a.getBegin() + matcher.start());
        annotation.setEnd(a.getBegin() + matcher.end() - 1);
        annotation.setCasProcessorId("TokenAnnotator");
        annotation.setConfidence(1);
        annotation.addToIndexes();
        pos = matcher.end();
      }
      
    }
    
    
  }

}
