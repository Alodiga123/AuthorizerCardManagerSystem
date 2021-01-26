package com.alodiga.authorizer.cms.ws.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerHandler {

	private EntityManagerFactory entityManagerFactory = null;

	public EntityManagerHandler() {
		this.entityManagerFactory = null;
	}

	public EntityManager getEntityManager() {
		if (this.entityManagerFactory == null) {
			this.entityManagerFactory = Persistence
					.createEntityManagerFactory("default-test");
		}
		return this.entityManagerFactory.createEntityManager();
	}

}
