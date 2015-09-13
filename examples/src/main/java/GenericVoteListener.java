import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;

public class GenericVoteListener implements VoteListener {
    @Override
    public void voteMade(Vote vote) {
        Votifier.getInstance().getLog().info(vote.getUsername() + " has voted from " + vote.getServiceName() + " at " + vote.getTimeStamp());
    }
}
