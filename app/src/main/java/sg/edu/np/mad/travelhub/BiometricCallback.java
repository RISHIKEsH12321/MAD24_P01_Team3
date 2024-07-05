package sg.edu.np.mad.travelhub;

public interface BiometricCallback {
    void onBiometricAuthenticationSuccessful();
    void onBiometricAuthenticationFailed();
}