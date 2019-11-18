package com.capstone.greenmedicuser.models;


public class Medicine {
    private String Name;
    private String Price;
    private String Strength;
    private String company;
    private String quantity;
    private String Element;
    private String DosageForm;

    public Medicine() {
    }

    public Medicine(String Name, String Price, String Strength, String company, String quantity, String element, String dosageForm) {
        this.Name = Name;
        this.Price = Price;
        this.Strength = Strength;
        this.company = company;
        this.quantity = quantity;
        Element = element;
        DosageForm = dosageForm;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public String getStrength() {
        return Strength;
    }

    public void setStrength(String Strength) {
        this.Strength = Strength;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getElement() {
        return Element;
    }

    public void setElement(String element) {
        Element = element;
    }

    public String getDosageForm() {
        return DosageForm;
    }

    public void setDosageForm(String dosageForm) {
        DosageForm = dosageForm;
    }
}
