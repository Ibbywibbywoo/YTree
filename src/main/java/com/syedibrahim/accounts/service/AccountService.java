package com.syedibrahim.accounts.service;

import com.syedibrahim.accounts.model.Provider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service layer responsible for all business logic related to a client's
 * account onboarding. Manages the client's provider list in memory,
 * including adding, removing, uploading statements, and validating submission.
 */
@Service
public class AccountService {

    /** The client's currently added providers, keyed by provider ID. */
    private final Map<String, Provider> clientProviders = new LinkedHashMap<>();

    /**
     * The full list of known providers a client can search and select from.
     * In a production system this would be loaded from a database.
     */
    private final List<String> knownProviders = List.of(
            "Barclays", "HSBC", "Vanguard", "Fidelity",
            "Lloyds", "NatWest", "Halifax", "Santander",
            "Interactive Brokers", "Hargreaves Lansdown"
    );

    /**
     * Seeds the client's provider list with sample data on startup,
     * covering all three possible statuses: Uploaded, Missing, and Outdated.
     */
    public AccountService() {
        Provider barclays = new Provider("barclays", "Barclays", LocalDate.now().minusDays(10));
        barclays.setStatementFilename("statement_barclays.pdf");
        clientProviders.put("barclays", barclays);
        clientProviders.put("hsbc", new Provider("hsbc", "HSBC", null));
        clientProviders.put("vanguard", new Provider("vanguard", "Vanguard", LocalDate.now().minusMonths(4)));
    }

    /**
     * Returns all providers the client has added, each with their
     * dynamically calculated statement status.
     *
     * @return collection of the client's providers
     */
    public Collection<Provider> getClientProviders() {
        return clientProviders.values();
    }

    /**
     * Returns the list of known providers that the client has not yet added.
     * Filters out any providers already present in the client's list.
     *
     * @return list of available provider names
     */
    public List<String> getAvailableProviders() {
        return knownProviders.stream()
                .filter(p -> !clientProviders.containsKey(p.toLowerCase()))
                .toList();
    }

    /**
     * Adds a new provider to the client's account list with no statement uploaded.
     * Throws an exception if the provider has already been added.
     *
     * @param name the display name of the provider to add
     * @return the newly created Provider
     * @throws IllegalArgumentException if the provider is already in the client's list
     */
    public Provider addProvider(String name) {
        String id = name.toLowerCase();
        if (clientProviders.containsKey(id)) {
            throw new IllegalArgumentException("Provider already added: " + name);
        }
        Provider provider = new Provider(id, name, null);
        clientProviders.put(id, provider);
        return provider;
    }

    /**
     * Removes a provider from the client's account list.
     * Throws an exception if the provider does not exist.
     *
     * @param id the unique identifier of the provider to remove
     * @throws NoSuchElementException if the provider is not found
     */
    public void removeProvider(String id) {
        if (!clientProviders.containsKey(id)) {
            throw new NoSuchElementException("Provider not found: " + id);
        }
        clientProviders.remove(id);
    }

    /**
     * Records a statement upload for a given provider.
     * Sets the statement date to today and stores the filename.
     * This will update the provider's status from Missing or Outdated to Uploaded.
     *
     * @param id       the unique identifier of the provider
     * @param filename the name of the uploaded statement file
     * @return the updated Provider
     * @throws NoSuchElementException if the provider is not found
     */
    public Provider uploadStatement(String id, String filename) {
        Provider provider = clientProviders.get(id);
        if (provider == null) {
            throw new NoSuchElementException("Provider not found: " + id);
        }
        provider.setStatementDate(LocalDate.now());
        provider.setStatementFilename(filename);
        return provider;
    }

    /**
     * Checks whether the client is eligible to submit.
     * Returns true only if at least one provider exists and every
     * provider has a current (Uploaded) statement.
     *
     * @return true if all providers are in Uploaded status, false otherwise
     */
    public boolean canSubmit() {
        if (clientProviders.isEmpty()) return false;
        return clientProviders.values().stream()
                .allMatch(p -> p.getStatus().equals("UPLOADED"));
    }

    /**
     * Processes the client's submission after validating that all providers
     * have a current statement. In a production system this would trigger
     * a downstream event such as notifying an advisor or persisting to a database.
     *
     * @throws IllegalStateException if any provider is Missing or Outdated
     */
    public void submit() {
        if (!canSubmit()) {
            throw new IllegalStateException("Not all providers have a current statement.");
        }
        System.out.println("✅ Client submitted successfully at " + LocalDate.now());
    }
}