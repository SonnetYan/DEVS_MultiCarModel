package CheckoutPlace;

import GenCol.entity;

public class customerEntity extends entity {
    protected double processingTime;
    protected double items;
    protected int priority;
    protected boolean processed;

    public customerEntity() {
        this("Customer", 10, 1, false);
    }

    public customerEntity(String name, double _numItems, int _priority, boolean _processed) {
        super(name);
        items = _numItems;
        priority = _priority;
        processed = _processed;
    }

    public double getProcessingTime() {
        return processingTime;
    }

    public double getItems() {
        return items;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isProcessed() { return processed; }

    public String toString() {
        return name + "_num_Items_" + (double) ((int) (getItems()));

    }

}
