package org.example.vitraya_sso_token.model;

import jakarta.persistence.*;

@Entity
@Table(name = "corporates")
public class Corporate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean active;
    private String type;
    @Column(unique = true)
    private String hospitalCode;
    private Integer billParserIdentifier;
    private boolean teritaryHospital;
    private String communicationInString;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getHospitalCode() { return hospitalCode; }
    public void setHospitalCode(String hospitalCode) { this.hospitalCode = hospitalCode; }
    public Integer getBillParserIdentifier() { return billParserIdentifier; }
    public void setBillParserIdentifier(Integer billParserIdentifier) { this.billParserIdentifier = billParserIdentifier; }
    public boolean isTeritaryHospital() { return teritaryHospital; }
    public void setTeritaryHospital(boolean teritaryHospital) { this.teritaryHospital = teritaryHospital; }
    public String getCommunicationInString() { return communicationInString; }
    public void setCommunicationInString(String communicationInString) { this.communicationInString = communicationInString; }
}