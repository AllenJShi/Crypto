import java.util.*;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private final double p_graph;
    private final double p_malicious;
    private final double p_txDistribution;
    private final int numRounds;
    private boolean[] followees;
    private Set<Transaction> pendingTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // IMPLEMENT THIS
        // Constructor
        this.p_graph = p_graph;
        this.p_malicious = p_malicious;
        this.p_txDistribution = p_txDistribution;
        this.numRounds = numRounds;
        this.pendingTransactions = new HashSet<Transaction>();
    }

    public void setFollowees(boolean[] followees) {
        // IMPLEMENT THIS
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
        for(Transaction tx: pendingTransactions){
            this.pendingTransactions.add(tx);
        }

    }

    public Set<Transaction> sendToFollowers() {
        // IMPLEMENT THIS
        return this.pendingTransactions;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        // IMPLEMENT THIS
        for(Candidate cand : candidates){
            this.pendingTransactions.add(cand.tx);
        }
        
    }
}
