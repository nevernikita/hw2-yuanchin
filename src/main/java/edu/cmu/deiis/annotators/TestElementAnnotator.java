package edu.cmu.deiis.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.*;

/*
 * The first step to analyze the artifact.
 * Get the Question and Answer annotation using regex matching.
 */
public class TestElementAnnotator extends JCasAnnotator_ImplBase{
  private Pattern questionPattern = Pattern.compile("Q .+\r\n");
  private Pattern answerPattern = Pattern.compile("A [01]? ?.+\r\n");
  
  public void process(JCas aJCas){
    String docText = aJCas.getDocumentText();
    
    Matcher matcher = questionPattern.matcher(docText);
    int pos = 0;
    while(matcher.find(pos)){
      Question annotation = new Question(aJCas);
      annotation.setBegin(matcher.start()+ 2);
      annotation.setEnd(matcher.end() - 1);
      annotation.setCasProcessorId("QuestionAnnotator");
      annotation.setConfidence(1);
      annotation.addToIndexes();
      pos = matcher.end();
    }
    
    matcher = answerPattern.matcher(docText);
    pos = 0;
    while(matcher.find(pos)){
      Answer annotation = new Answer(aJCas);
      
      annotation.setBegin(matcher.start() + 4);
      annotation.setEnd(matcher.end() - 1);
      if(Integer.valueOf(aJCas.getDocumentText().substring(matcher.start()+2, matcher.start()+3)) == 1){
        annotation.setIsCorrect(true);
      }
      else {
        annotation.setIsCorrect(false);
      }
      annotation.setCasProcessorId("AnswerAnnotator");
      annotation.setConfidence(1);
      annotation.addToIndexes();
      pos = matcher.end();
    }
            
  }

}
