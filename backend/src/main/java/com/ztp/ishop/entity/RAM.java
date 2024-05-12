package com.ztp.ishop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rams")
public class RAM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "speed")
    private int speed;

    @Column(name = "capacity")
    private String capacity;

    @Column(name = "voltage")
    private int voltage;

    @Column(name = "module_count")
    private int moduleCount;

    @Column(name = "backlight")
    private boolean backlight;

    @Column(name = "cooling")
    private boolean cooling;

    public RAM() {
    }

    public RAM(Long id, Product product, int speed, String capacity, int voltage, int moduleCount, boolean backlight, boolean cooling) {
        this.id = id;
        this.product = product;
        this.speed = speed;
        this.capacity = capacity;
        this.voltage = voltage;
        this.moduleCount = moduleCount;
        this.backlight = backlight;
        this.cooling = cooling;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getModuleCount() {
        return moduleCount;
    }

    public void setModuleCount(int moduleCount) {
        this.moduleCount = moduleCount;
    }

    public boolean isBacklight() {
        return backlight;
    }

    public void setBacklight(boolean backlight) {
        this.backlight = backlight;
    }

    public boolean isCooling() {
        return cooling;
    }

    public void setCooling(boolean cooling) {
        this.cooling = cooling;
    }
}
