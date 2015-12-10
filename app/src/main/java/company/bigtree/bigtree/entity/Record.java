package company.bigtree.bigtree.entity;

/**
 * Created by shenzebang on 15/11/18.
 */
public class Record {
    private String className;
    private String teacherName;
    private String classDate;
    private int classTime;
    private int onlineTime;
    private double onlineScore;
    private double answerScore;

    public Record(String className, String teacherName, String classDate, int classTime, int onlineTime, double onlineScore, double answerScore) {
        this.className = className;
        this.teacherName = teacherName;
        this.classDate = classDate;
        this.classTime = classTime;
        this.onlineTime = onlineTime;
        this.onlineScore = onlineScore;
        this.answerScore = answerScore;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public int getClassTime() {
        return classTime;
    }

    public void setClassTime(int classTime) {
        this.classTime = classTime;
    }

    public int getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(int onlineTime) {
        this.onlineTime = onlineTime;
    }

    public double getOnlineScore() {
        return onlineScore;
    }

    public void setOnlineScore(double onlineScore) {
        this.onlineScore = onlineScore;
    }

    public double getAnswerScore() {
        return answerScore;
    }

    public void setAnswerScore(double answerScore) {
        this.answerScore = answerScore;
    }

    @Override
    public String toString() {
        return "Record{" +
                "className='" + className + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", classDate='" + classDate + '\'' +
                ", classTime=" + classTime +
                ", onlineTime=" + onlineTime +
                ", onlineScore=" + onlineScore +
                ", answerScore=" + answerScore +
                '}';
    }
}
