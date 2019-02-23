package ca.etsmtl.applets.etsmobile.http;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Utility class handling SSL related matters
 *
 * @author zaclimon
 */
public class SSLUtilities {

    /**
     * Takes a given certificate and stores it inside the device's keystore.
     *
     * @param certificateStream the {@link InputStream} pointing to the certificate
     * @return a {@link SignetsSSLTrust} containing the {@link X509TrustManager} as well as the {@link SSLContext} required for further usage.
     */
    public static SignetsSSLTrust createSignetsCertificateTrust(InputStream certificateStream) {

        try (InputStream caInput = new BufferedInputStream(certificateStream)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);

            // Create a KeyStore containing ÉTS's CA
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            Certificate storedCertificate = keyStore.getCertificate("ca");

            // Add the certificate to the keystore if it doesn't exists or replace it if it has been changed.
            if (!keyStore.containsAlias("ca") || storedCertificate != null && !ca.equals(storedCertificate)) {
                keyStore.setCertificateEntry("ca", ca);
            }

            // Create a TrustManager that trusts the CA in the KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses the TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            X509TrustManager trustManager =  (X509TrustManager) tmf.getTrustManagers()[0];
            SignetsSSLTrust sslTrust = new SignetsSSLTrust(trustManager, context);

            return sslTrust;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
