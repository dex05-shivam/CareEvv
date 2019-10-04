package care.example.careevv.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import care.example.careevv.MainActivity;
import care.example.careevv.R;
import care.example.careevv.preferences.LoggedInUser;

/**
 * Created by whit3hawks on 11/16/16.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication error\n" + errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication help\n" + helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.");
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        LoggedInUser loggedInUser = new LoggedInUser(context);
        ParseJasonLang parseJasonLang = new ParseJasonLang(context);

        if(loggedInUser.getLocal_user_id().equals("")){
            MyToast.showToast(context,parseJasonLang.getJsonToString("first_login_with_email_password"));
        }else {
            Intent intent = new Intent((Activity) context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }

    }

    private void update(String e){
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.finger_text);
        textView.setText(e);
    }

}
