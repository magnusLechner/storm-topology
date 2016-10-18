package at.storm.bolt.values.statistic;

import java.io.Serializable;

import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public abstract class StatisticValue extends Values implements Serializable {
	private static final long serialVersionUID = -3661036106050262776L;

    public StatisticValue() {
        super();
    }

    public static Fields getSchema() {
        return new Fields();
    }
    
}
