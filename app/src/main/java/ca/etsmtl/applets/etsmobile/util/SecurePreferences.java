package ca.etsmtl.applets.etsmobile.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gnut3ll4 on 10/5/14.
 */
public class SecurePreferences implements SharedPreferences {

    private static SharedPreferences sFile;
    private static byte[] sKey;

    /**
     * Constructor.
     *
     * @param context the caller's context
     */
    public SecurePreferences(Context context) {
        // Proxy design pattern
        if (SecurePreferences.sFile == null) {
            SecurePreferences.sFile = PreferenceManager.getDefaultSharedPreferences(context);
        }
        // Initialize encryption/decryption key
        try {
            final String key = SecurePreferences.generateAesKeyName(context);
            String value = SecurePreferences.sFile.getString(key, null);
            if (value == null) {
                value = SecurePreferences.generateAesKeyValue();
                SecurePreferences.sFile.edit().putString(key, value).commit();
            }
            SecurePreferences.sKey = SecurePreferences.decode(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String encode(byte[] input)
    {
        return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static byte[] decode(String input)
    {
        return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    private static String generateAesKeyName(Context context) throws InvalidKeySpecException,
            NoSuchAlgorithmException {
        final char[] password = context.getPackageName().toCharArray();
        final byte[] salt = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID).getBytes();

        // Number of PBKDF2 hardening rounds to use, larger values increase
        // computation time, you should select a value that causes
        // computation to take >100ms
        final int iterations = 1000;

        // Generate a 256-bit key
        final int keyLength = 256;

        final KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        return SecurePreferences.encode(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).getEncoded());
    }

    private static String generateAesKeyValue() throws NoSuchAlgorithmException {
        // Do *not* seed secureRandom! Automatically seeded from system entropy
        final SecureRandom random = new SecureRandom();

        // Use the largest AES key length which is supported by the OS
        final KeyGenerator generator = KeyGenerator.getInstance("AES");
        try {
            generator.init(256, random);
        } catch (Exception e) {
            try {
                generator.init(192, random);
            } catch (Exception e1) {
                generator.init(128, random);
            }
        }
        return SecurePreferences.encode(generator.generateKey().getEncoded());
    }

    private static String encrypt(String cleartext) {
        if (cleartext == null || cleartext.length() == 0) {
            return cleartext;
        }
        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SecurePreferences.sKey, "AES"));
            return SecurePreferences.encode(cipher.doFinal(cleartext.getBytes("UTF-8")));
        } catch (Exception e) {
            Log.w(SecurePreferences.class.getName(), "encrypt", e);
            return null;
        }
    }

    private static String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.length() == 0) {
            return ciphertext;
        }
        try {
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(SecurePreferences.sKey, "AES"));
            return new String(cipher.doFinal(SecurePreferences.decode(ciphertext)), "UTF-8");
        } catch (Exception e) {
            Log.w(SecurePreferences.class.getName(), "decrypt", e);
            return null;
        }
    }

    @Override
    public Map<String, String> getAll() {
        final Map<String, ?> encryptedMap = SecurePreferences.sFile.getAll();
        final Map<String, String> decryptedMap = new HashMap<String, String>(encryptedMap.size());
        for (Map.Entry<String, ?> entry : encryptedMap.entrySet()) {
            try {
                decryptedMap.put(SecurePreferences.decrypt(entry.getKey()),
                        SecurePreferences.decrypt(entry.getValue().toString()));
            } catch (Exception e) {
                // Ignore unencrypted key/value pairs
            }
        }
        return decryptedMap;
    }

    @Override
    public String getString(String key, String defaultValue) {
        final String encryptedValue =
                SecurePreferences.sFile.getString(SecurePreferences.encrypt(key), null);
        return (encryptedValue != null) ? SecurePreferences.decrypt(encryptedValue) : defaultValue;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(String key, Set<String> defaultValues) {
        final Set<String> encryptedSet =
                SecurePreferences.sFile.getStringSet(SecurePreferences.encrypt(key), null);
        if (encryptedSet == null) {
            return defaultValues;
        }
        final Set<String> decryptedSet = new HashSet<String>(encryptedSet.size());
        for (String encryptedValue : encryptedSet) {
            decryptedSet.add(SecurePreferences.decrypt(encryptedValue));
        }
        return decryptedSet;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        final String encryptedValue =
                SecurePreferences.sFile.getString(SecurePreferences.encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(SecurePreferences.decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public long getLong(String key, long defaultValue) {
        final String encryptedValue =
                SecurePreferences.sFile.getString(SecurePreferences.encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(SecurePreferences.decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        final String encryptedValue =
                SecurePreferences.sFile.getString(SecurePreferences.encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(SecurePreferences.decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        final String encryptedValue =
                SecurePreferences.sFile.getString(SecurePreferences.encrypt(key), null);
        if (encryptedValue == null) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(SecurePreferences.decrypt(encryptedValue));
        } catch (NumberFormatException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    @Override
    public boolean contains(String key) {
        return SecurePreferences.sFile.contains(SecurePreferences.encrypt(key));
    }

    @Override
    public Editor edit() {
        return new Editor();
    }

    /**
     * Wrapper for Android's {@link android.content.SharedPreferences.Editor}.
     * <p>
     * Used for modifying values in a {@link SecurePreferences} object. All changes you make in an
     * editor are batched, and not copied back to the original {@link SecurePreferences} until you
     * call {@link #commit()} or {@link #apply()}.
     */
    public static class Editor implements SharedPreferences.Editor {
        private SharedPreferences.Editor mEditor;

        /**
         * Constructor.
         */
        private Editor() {
            mEditor = SecurePreferences.sFile.edit();
        }

        @Override
        public SharedPreferences.Editor putString(String key, String value) {
            mEditor.putString(SecurePreferences.encrypt(key), SecurePreferences.encrypt(value));
            return this;
        }

        @Override
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
            final Set<String> encryptedValues = new HashSet<String>(values.size());
            for (String value : values) {
                encryptedValues.add(SecurePreferences.encrypt(value));
            }
            mEditor.putStringSet(SecurePreferences.encrypt(key), encryptedValues);
            return this;
        }

        @Override
        public SharedPreferences.Editor putInt(String key, int value) {
            mEditor.putString(SecurePreferences.encrypt(key),
                    SecurePreferences.encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor putLong(String key, long value) {
            mEditor.putString(SecurePreferences.encrypt(key),
                    SecurePreferences.encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor putFloat(String key, float value) {
            mEditor.putString(SecurePreferences.encrypt(key),
                    SecurePreferences.encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor putBoolean(String key, boolean value) {
            mEditor.putString(SecurePreferences.encrypt(key),
                    SecurePreferences.encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public SharedPreferences.Editor remove(String key) {
            mEditor.remove(SecurePreferences.encrypt(key));
            return this;
        }

        @Override
        public SharedPreferences.Editor clear() {
            mEditor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return mEditor.commit();
        }

        @Override
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public void apply() {
            mEditor.apply();
        }
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        SecurePreferences.sFile.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        SecurePreferences.sFile.unregisterOnSharedPreferenceChangeListener(listener);
    }
}