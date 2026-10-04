package com.syedibrahim.accounts.model;

import java.time.LocalDate;

public class Provider {

    private String id;
    private String name;
    private LocalDate statementDate;
    private String statementFilename;

    public Provider(String id, String name, LocalDate statementDate) {
        this.id = id;
        this.name = name;
        this.statementDate = statementDate;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDate getStatementDate() { return statementDate; }
    public void setStatementDate(LocalDate statementDate) { this.statementDate = statementDate; }

    public String getStatus() {
        if (statementDate == null) return "MISSING";
        if (statementDate.isBefore(LocalDate.now().minusMonths(3))) return "OUTDATED";
        return "UPLOADED";
    }

    public String getStatementFilename() {
        return statementFilename;
    }
    public void setStatementFilename(String filename) {
        this.statementFilename = filename;
    }
}