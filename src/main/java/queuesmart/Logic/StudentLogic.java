package queuesmart.Logic;

import queuesmart.Database.StudentDAO;

public class StudentLogic {

    private final StudentDAO studentDAO;

    public StudentLogic() {
        studentDAO = new StudentDAO();
    }

    public int getStudentIdByUserId(int userId) {

        if (userId <= 0) {
            return 0;
        }

        return studentDAO.getStudentIdByUserId(userId);
    }
}
