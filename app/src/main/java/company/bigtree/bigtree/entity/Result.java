package company.bigtree.bigtree.entity;

/**
 * Created by shenzebang on 15/11/18.
 */
public class Result {
    private int resultId;
    private String myAnswer;
    private String standardAnswer;
    private boolean isRight;

    public Result(int resultId, String myAnswer, String standardAnswer, boolean isRight) {
        this.resultId = resultId;
        this.myAnswer = myAnswer;
        this.standardAnswer = standardAnswer;
        this.isRight = isRight;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public String getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }

    public String getStandardAnswer() {
        return standardAnswer;
    }

    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setIsRight(boolean isRight) {
        this.isRight = isRight;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", myAnswer='" + myAnswer + '\'' +
                ", standardAnswer='" + standardAnswer + '\'' +
                ", isRight=" + isRight +
                '}';
    }
}
