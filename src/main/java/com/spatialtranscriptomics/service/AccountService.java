/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Account;

@Service
public interface AccountService {

	public Account find(String id);

	public Account findByName(String username);

	public List<Account> list();

	public Account add(Account account);

	public void update(Account account);

	public void delete(String id);

}
