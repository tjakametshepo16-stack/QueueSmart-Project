package queuesmart.Models;

public class Feedback {
    private int feedbackId,studentId,rating; 
    private int queueId; 
    private String comment;
    
    public int getFeedbackId(){
        return feedbackId;
    } 
    
    public void setFeedbackId(int v){
        feedbackId=v;
    }
    
    public int getStudentId(){
        return studentId;
    } 
    
    public void setStudentId(int v){
        studentId=v;
    }
    
    public int getQueueId(){
        return queueId;
    } 
    
    public void setQueueId(int v){
        queueId=v;
    }
    
    public int getRating(){
        return rating;
    } 
    
    public void setRating(int v){
        rating=v;
    }
    
    public String getComment(){
        return comment;
    } 
    
    public void setComment(String v){
        comment=v;
    }
}
