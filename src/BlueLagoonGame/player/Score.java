package BlueLagoonGame.player;

public class Score {

    private int totalScore;
    private int islandScore;
    private int linkScore;
    private int majorityScore;
    private int resourceScore;
    private int resourceAdditionalScore;
    private int statuetteScore;

    public int getTotalScore() {
        return totalScore;
    }

    public void setIslandScore(int islandScore) {
        this.islandScore = islandScore;
    }

    public void setMajorityScore(int majorityScore) {
        this.majorityScore = majorityScore;
    }

    public void setResourceScore(int resourceScore) {
        this.resourceScore = resourceScore;
    }

    public void setLinkScore(int linkScore) {
        this.linkScore = linkScore;
    }

    public void setResourceAdditionalScore(int resourceAdditionalScore) {
        this.resourceAdditionalScore = resourceAdditionalScore;
    }

    public void setStatuetteScore(int statuetteScore) {
        this.statuetteScore = statuetteScore;
    }

    public void updateTotalScore() {
        totalScore = 0;
        totalScore += islandScore;
        totalScore += linkScore;
        totalScore += majorityScore;
        totalScore += resourceScore;
        totalScore += resourceAdditionalScore;
        totalScore += statuetteScore;
    }

    public Score() {
        this.islandScore = 0;
        this.linkScore = 0;
        this.majorityScore = 0;
        this.resourceScore = 0;
        this.resourceAdditionalScore = 0;
        this.totalScore = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("8/7 Islands Score: ");
        sb.append(islandScore);
        sb.append("\nIsland Link Score: ");
        sb.append(linkScore);
        sb.append("\nMajority Score: ");
        sb.append(majorityScore);
        sb.append("\nResource Score: ");
        sb.append(resourceScore);
        sb.append("\nResource Additional Bonus: ");
        sb.append(resourceAdditionalScore);
        sb.append("\nStatuette Score: ");
        sb.append(statuetteScore);
        sb.append("\n\n");
        return sb.toString();
    }
}
