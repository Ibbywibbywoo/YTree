package com.syedibrahim.accounts.model;

import java.time.LocalDate;

/**
 * Represents a financial provider that a client has added during onboarding.
 * Each provider holds a statement upload record and dynamically calculates
 * its own status based on the age of the uploaded statement.
 */
public class Provider {

    /** Unique identifier for the provider, derived from the name in lowercase (e.g. "barclays"). */
    private String id;

    /** Display name of the provider (e.g. "Barclays"). */
    private String name;

    /**
     * The date the most recent statement was uploaded.
     * Null if no statement has been uploaded yet.
     */
    private LocalDate statementDate;

    /**
     * The filename of the most recently uploaded statement.
     * Null if no statement has been uploaded yet.
     */
    private String statementFilename;

    /**
     * Constructs a new Provider with the given ID, name, and statement date.
     *
     * @param id            unique identifier (lowercase name)
     * @param name          display name
     * @param statementDate date of the most recent statement, or null if none uploaded
     */
    public Provider(String id, String name, LocalDate statementDate) {
        this.id = id;
        this.name = name;
        this.statementDate = statementDate;
    }

    /**
     * Dynamically calculates the provider's current status based on the statement date.
     * <ul>
     *   <li><b>MISSING</b> — no statement has been uploaded</li>
     *   <li><b>OUTDATED</b> — statement exists but is older than 3 months</li>
     *   <li><b>UPLOADED</b> — statement exists and is within the last 3 months</li>
     * </ul>
     * A statement dated exactly 3 months ago is considered OUTDATED.
     *
     * @return the current status as a string
     */
    public String getStatus() {
        if (statementDate == null) return "MISSING";
        if (statementDate.isBefore(LocalDate.now().minusMonths(3))) return "OUTDATED";
        return "UPLOADED";
    }

    /**
     * @return the provider's unique identifier
     */
    public String getId() { return id; }

    /**
     * @return the provider's display name
     */
    public String getName() { return name; }

    /**
     * @return the date of the most recent statement, or null if none uploaded
     */
    public LocalDate getStatementDate() { return statementDate; }

    /**
     * Updates the statement date, typically set to today when a new statement is uploaded.
     *
     * @param statementDate the new statement date
     */
    public void setStatementDate(LocalDate statementDate) { this.statementDate = statementDate; }

    /**
     * @return the filename of the most recent statement, or null if none uploaded
     */
    public String getStatementFilename() { return statementFilename; }

    /**
     * Updates the statement filename when a new statement is uploaded.
     *
     * @param filename the name of the uploaded file
     */
    public void setStatementFilename(String filename) { this.statementFilename = filename; }
}