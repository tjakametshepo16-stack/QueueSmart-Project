package queuesmart.Logic;

import queuesmart.Database.FeedbackDAO;
import queuesmart.Models.Feedback;

public class FeedbackLogic {

    private final FeedbackDAO feedbackDAO;

    public FeedbackLogic() {
        feedbackDAO = new FeedbackDAO();
    }

    public String submitFeedback(int studentId, int rating, String comment) {

        if (studentId <= 0) {
            return "You must be logged in as a student to submit feedback.";
        }

        if (rating < 1 || rating > 5) {
            return "Please choose a rating between 1 and 5.";
        }

        Feedback feedback = new Feedback();
        feedback.setStudentId(studentId);
        feedback.setRating(rating);
        feedback.setComment(comment == null ? "" : comment.trim());

        boolean success = feedbackDAO.insertFeedback(feedback);

        return success ? null : "Could not save your feedback. Please try again.";
    }

    public double getAverageRating() {
        return feedbackDAO.getAverageRating();
    }
}
