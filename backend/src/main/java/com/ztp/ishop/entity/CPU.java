package com.ztp.ishop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cpus")
public class CPU {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "speed")
    private double speed;

    @Column(name = "architecture")
    private String architecture;

    @Column(name = "supported_memory")
    private String supportedMemory;

    @Column(name = "cooling")
    private boolean cooling;

    @Column(name = "threads")
    private int threads;

    @Column(name = "technological_process")
    private int technologicalProcess;

    @Column(name = "power_consumption")
    private int powerConsumption;

    public CPU() {
    }

    public CPU(Long id, Product product, double speed, String architecture, String supportedMemory, boolean cooling, int threads, int technologicalProcess, int powerConsumption) {
        this.id = id;
        this.product = product;
        this.speed = speed;
        this.architecture = architecture;
        this.supportedMemory = supportedMemory;
        this.cooling = cooling;
        this.threads = threads;
        this.technologicalProcess = technologicalProcess;
        this.powerConsumption = powerConsumption;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getSupportedMemory() {
        return supportedMemory;
    }

    public void setSupportedMemory(String supportedMemory) {
        this.supportedMemory = supportedMemory;
    }

    public boolean isCooling() {
        return cooling;
    }

    public void setCooling(boolean cooling) {
        this.cooling = cooling;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getTechnologicalProcess() {
        return technologicalProcess;
    }

    public void setTechnologicalProcess(int technologicalProcess) {
        this.technologicalProcess = technologicalProcess;
    }

    public int getPowerConsumption() {
        return powerConsumption;
    }

    public void setPowerConsumption(int powerConsumption) {
        this.powerConsumption = powerConsumption;
    }
}

