import java.security.PublicKey;
import java.util.*;

public class TxHandler {
    private UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
        double inputSum = 0;
        double outputSum = 0;
        UTXOPool inputPool = new UTXOPool();
        // HashSet<UTXO> doneUTXO = new HashSet<UTXO>();
        
        int ind = 0;
        for(Transaction.Input input : tx.getInputs()){
            UTXO utxo = new UTXO(input.prevTxHash,input.outputIndex);

        // condition 1
            if(!utxoPool.contains(utxo)){
                return false;
            }
            
        // update inputSum
            Transaction.Output output = utxoPool.getTxOutput(utxo);
            inputSum += output.value;
        
        // condition 2
            PublicKey pubKey = output.address;
            byte[] message = tx.getRawDataToSign(ind);
            byte[] signature = input.signature;
            if(!Crypto.verifySignature(pubKey, message, signature)){
                return false;
            }
        
        // condition 3
            if(inputPool.contains(utxo)){
                return false;
            }
            // if(!doneUTXO.add(utxo)){
            //     return false;
            // }
        
        // update inputPool
            inputPool.addUTXO(utxo, output);

        // update index
            ind ++;
        }
        
        // condition 4    
        for(Transaction.Output output : tx.getOutputs()){
            double temp = output.value;
            if(temp < 0.0){
                return false;
            }
            // update outputSum
            outputSum += temp;
        }
        
        // condition 5
        if(inputSum < outputSum){
            return false;
        }
        
        // clear all constraints and return true
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        // create a valid list
        ArrayList<Transaction> validTX = new ArrayList<Transaction>();

        // this block of code was by 
        // https://github.com/SauceCat/Bitcoin-and-Cryptocurrency-Technologies/blob/master/coursera/assignment3starterCode/TxHandler.java
        // I made small change to adapt my own style
        boolean check = false;
        
        do{
            check = false;
            for(Transaction tx : possibleTxs){
                if(validTX.contains(tx)){
                    continue;
                }
                if(isValidTx(tx)){
                    check =true;
                    validTX.add(tx);
                    for(Transaction.Input input : tx.getInputs()){
                        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                        utxoPool.removeUTXO(utxo);
                    }
                    for(int i = 0; i<tx.getOutputs().size(); i++){
                        UTXO utxo = new UTXO(tx.getHash(), i);
                        utxoPool.addUTXO(utxo, tx.getOutput(i));
                    }
                }
            }
        }while(check);

        Transaction[] outcome = new Transaction[validTX.size()];
        int ind = 0;
        for (Transaction tx: validTX){
            outcome[ind] = tx;
            ind++;
        }
        return outcome; 
    }

}
