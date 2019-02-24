package ca.etsmtl.applets.etsmobile.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Holder class that has a {@link X509TrustManager} as well as a {@link SSLContext} required for Signets
 * It is encapsulated for easier use for a {@link okhttp3.OkHttpClient}.
 *
 * @author zaclimon
 */
public class SignetsSSLTrust {

    private X509TrustManager manager;
    private SSLContext context;

    /**
     * Default constructor
     *
     * @param sslManager the trust manager
     * @param sslContext the SSL context required for validate a given domain.
     */
    public SignetsSSLTrust(X509TrustManager sslManager, SSLContext sslContext) {
        manager = sslManager;
        context = sslContext;
    }

    public SSLContext getContext() {
        return context;
    }

    public X509TrustManager getManager() {
        return manager;
    }
}
