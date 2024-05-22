package DDDS_2024_MultiCarModel;

import GenCol.entity;

public class Vehicle extends entity {
    private double position;
    protected double speed;
    private String direction;
    private int type;  				//use a type to determine the speed: 1 means slow and 2 means fast
    private double coefficient = 1;

    public Vehicle() {
        this("veh");
    }

    public Vehicle(double position, double speed) {
        this("veh");
        this.position = position;
        this.speed = speed;
        this.type = 2;
    }

    public Vehicle(String nm) {
        super(nm);
    }

    public Vehicle(Vehicle other) {
        super(other.getName());
        this.position = other.getPosition();
        this.speed = other.getSpeed();
        this.type = other.getType();
        this.direction = other.getDirection();
    }

    public double getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    /**
     * 1 means slow, 2 means fast, 3 means customized vehicle
     * @return vehicle Type
     */
    public int getType() {
        return type;
    }

    public double updatePosition(double time) {
        return getPosition() + getSpeed() * time;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCoefficient(double co) {
        this.coefficient = co;
    }

    public double getCoefficient() {
        return coefficient;
    }
}
