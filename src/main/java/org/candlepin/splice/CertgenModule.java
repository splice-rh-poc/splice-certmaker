package org.candlepin.splice;

import org.candlepin.pki.PKIReader;
import org.candlepin.pki.PKIUtility;
import org.candlepin.pki.SubjectKeyIdentifierWriter;
import org.candlepin.pki.impl.BouncyCastlePKIReader;
import org.candlepin.pki.impl.BouncyCastlePKIUtility;
import org.candlepin.pki.impl.DefaultSubjectKeyIdentifierWriter;

import com.google.inject.AbstractModule;

public class CertgenModule extends AbstractModule {

	@Override
	protected void configure() {
		// most of this was copied from candlepin's injector module
        bind(PKIUtility.class).to(BouncyCastlePKIUtility.class).asEagerSingleton();
        bind(PKIReader.class).to(BouncyCastlePKIReader.class).asEagerSingleton();
        bind(SubjectKeyIdentifierWriter.class).to(DefaultSubjectKeyIdentifierWriter.class);
	}
	

}
