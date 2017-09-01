package com.dmarjanovic.drools

import org.kie.api.KieServices
import org.kie.api.runtime.{KieContainer, KieSession, StatelessKieSession}

object Kie {
  private lazy val kieServices: KieServices = KieServices.Factory.get()
  private lazy val kieContainer: KieContainer = kieServices.getKieClasspathContainer

  def newStatelessSession: KieSession = kieContainer.newStatelessKieSession().getKieBase.newKieSession()

  def newSession: KieSession = kieContainer.newKieSession()
}
