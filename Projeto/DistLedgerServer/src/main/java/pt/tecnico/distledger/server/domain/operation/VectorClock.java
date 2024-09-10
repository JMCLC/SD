package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class VectorClock {
    private ArrayList<Integer> timestamps;

    public VectorClock() {
        this.timestamps = new ArrayList<>();
        timestamps.add(0);
        timestamps.add(0);
    }

    public VectorClock(Timestamp vector) {
        this.timestamps = new ArrayList<>(vector.getTSList());
    }

    public List<Integer> getTimestamps() {
        return timestamps;
    }

    public Integer getTS(String qualifier) {
        return timestamps.get(qualifier.charAt(0) - 'A');
    }

    public Integer getTSAtIndex(Integer index) {
        return timestamps.get(index);
    } 

    public void setTS(String qualifier, Integer value) {
        timestamps.set(qualifier.charAt(0) - 'A', value);
    }

    public void setTSAtIndex(Integer index, Integer value) {
        timestamps.set(index, value);
    }

    public void merge(VectorClock v) {
        List<Integer> timestamps = v.getTimestamps();
        for (int i = 0; i < timestamps.size(); i++) {
            if (this.timestamps.get(i) < timestamps.get(i)) {
                this.timestamps.set(i, timestamps.get(i));
            }
        }
    }

    public boolean GreaterOrEqual(VectorClock v) {
        List<Integer> timestamps = v.getTimestamps();
        Integer equal = 0;
        for (int i = 0; i < timestamps.size(); i++) {
            if (this.timestamps.get(i) > timestamps.get(i)) {
                return true;
            } else if (this.timestamps.get(i) == timestamps.get(i)) {
                equal++;
            }
        }
        if (equal == timestamps.size())
            return true;
        return false;
    }

    public Integer getTimestampDifference(VectorClock v) {
        Integer index = 0;
        for (int i = 0; i < v.getTimestamps().size(); i++) {
            if (timestamps.get(i) > v.getTimestamps().get(i))
                index = i;
        }
        return index;
    }

    public boolean existsIn(List<Operation> ledger) {
        for (Operation op: ledger) {
            List<Integer> timestamp = op.getTS().getTimestamps();
            Integer count = 0;
            for (int i = 0; i < timestamp.size(); i++) {
                if (timestamp.get(i) == timestamps.get(i)) {
                    count++;
                }
            }
            if (count == timestamp.size()) {
                return true;
            }
        }
        return false;
    }

    public void increaseClockSize() {
        timestamps.add(0);
    }

    public Timestamp proto() {
        return Timestamp.newBuilder().addAllTS(timestamps).build();
    }

    @Override
    public String toString() {
        String string = "[";
        for (int i = 0; i < timestamps.size(); i++) {
            if (i != timestamps.size() - 1) {
                string += (timestamps.get(i) + ",");
            } else {
                string += timestamps.get(i) + "]";
            }
        }
        return string;
    }
}
