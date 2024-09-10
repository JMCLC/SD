package pt.tecnico.distledger.userclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class VectorClock {
    private List<Integer> timestamps;

    public VectorClock() {
        this.timestamps = new ArrayList<>();
        timestamps.add(0);
        timestamps.add(0);
    }

    public VectorClock(Timestamp vector) {
        this.timestamps = vector.getTSList();
    }

    public List<Integer> getTimestamps() {
        return timestamps;
    }

    public Integer getTS(String qualifier) {
        return timestamps.get(qualifier.charAt(0) - 'A');
    }

    public void setTS(String qualifier, Integer value) {
        timestamps.set(qualifier.charAt(0) - 'A', value);
    }

    public void merge(VectorClock v) {
        List<Integer> newTimestamps = v.getTimestamps();
        if (newTimestamps.size() > this.timestamps.size()) {
            this.timestamps.add(newTimestamps.get(newTimestamps.size() - 1));
        }
        for (int i = 0; i < timestamps.size(); i++) {
            if (timestamps.get(i) < newTimestamps.get(i)) {
                timestamps.set(i, newTimestamps.get(i));
            }
        }
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
