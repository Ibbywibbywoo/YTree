package com.syedibrahim.accounts;

import com.syedibrahim.accounts.model.Provider;
import com.syedibrahim.accounts.service.AccountService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

	@Test
	void providerWithNullDateIsMissing() {
		Provider p = new Provider("test", "Test Bank", null);
		assertEquals("MISSING", p.getStatus());
	}

	@Test
	void providerWithRecentDateIsUploaded() {
		Provider p = new Provider("test", "Test Bank", LocalDate.now().minusDays(5));
		assertEquals("UPLOADED", p.getStatus());
	}

	@Test
	void providerWithOldDateIsOutdated() {
		Provider p = new Provider("test", "Test Bank", LocalDate.now().minusMonths(4));
		assertEquals("OUTDATED", p.getStatus());
	}

	@Test
	void cannotSubmitWithMissingProvider() {
		AccountService service = new AccountService();
		assertFalse(service.canSubmit());
	}

	@Test
	void cannotAddDuplicateProvider() {
		AccountService service = new AccountService();
		assertThrows(IllegalArgumentException.class, () -> service.addProvider("Barclays"));
	}
}