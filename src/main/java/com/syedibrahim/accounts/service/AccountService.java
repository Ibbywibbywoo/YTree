package com.syedibrahim.accounts.service;

import com.syedibrahim.accounts.model.Provider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AccountService {

    // The client's added providers
    private final Map<String, Provider> clientProviders = new LinkedHashMap<>();

    // All known providers they can search and add from
    private final List<String> knownProviders = List.of(
            "Barclays", "HSBC", "Vanguard", "Fidelity",
            "Lloyds", "NatWest", "Halifax", "Santander",
            "Interactive Brokers", "Hargreaves Lansdown"
    );

    public AccountService() {
        // Seed data — a few providers already added to start from
        Provider barclays = new Provider("barclays", "Barclays", LocalDate.now().minusDays(10));
        barclays.setStatementFilename("statement_barclays.pdf");
        clientProviders.put("barclays", barclays);
        clientProviders.put("hsbc", new Provider("hsbc", "HSBC", null));
        clientProviders.put("vanguard", new Provider("vanguard", "Vanguard", LocalDate.now().minusMonths(4)));
    }

    public Collection<Provider> getClientProviders() {
        return clientProviders.values();
    }

    public List<String> getAvailableProviders() {
        // Only return providers not already added
        return knownProviders.stream()
                .filter(p -> !clientProviders.containsKey(p.toLowerCase()))
                .toList();
    }

    public Provider addProvider(String name) {
        String id = name.toLowerCase();
        if (clientProviders.containsKey(id)) {
            throw new IllegalArgumentException("Provider already added: " + name);
        }
        Provider provider = new Provider(id, name, null);
        clientProviders.put(id, provider);
        return provider;
    }

    public void removeProvider(String id) {
        if (!clientProviders.containsKey(id)) {
            throw new NoSuchElementException("Provider not found: " + id);
        }
        clientProviders.remove(id);
    }

    public Provider uploadStatement(String id, String filename) {
        Provider provider = clientProviders.get(id);
        if (provider == null) {
            throw new NoSuchElementException("Provider not found: " + id);
        }
        provider.setStatementDate(LocalDate.now());
        provider.setStatementFilename(filename);
        return provider;
    }

    public boolean canSubmit() {
        if (clientProviders.isEmpty()) return false;
        return clientProviders.values().stream()
                .allMatch(p -> p.getStatus().equals("UPLOADED"));
    }

    public void submit() {
        if (!canSubmit()) {
            throw new IllegalStateException("Not all providers have a current statement.");
        }
        System.out.println("✅ Client submitted successfully at " + LocalDate.now());
        // In a real app this would trigger something — for now just validates
    }
}