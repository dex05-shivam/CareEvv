package care.example.careevv.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import care.example.careevv.fragment.HomeFragment;


/**
 * Created by Dextrous on 11/24/2017.
 */

public class BackStackManagement {

    private Context context;
    private static long backPressed;

    public BackStackManagement(Context context) {
        this.context = context;
    }

    public void initFragment(int activieFragment) {
        Fragment fragment = null;
        switch (activieFragment) {
            case 0:
                if (backPressed + 2000 > System.currentTimeMillis()) {
                    ActivityCompat.finishAffinity((Activity) context);
                } else {
                    backPressed = System.currentTimeMillis();
                 //   MyToast.showToast(context, "Press again for exit");
                }
                break;

            case 1:
                fragment = new HomeFragment();
                break;
//
//            case 2:
//                fragment = new ReportsFragment();
//                break;
//
//            case 3:
//                fragment = new ReportDetailsFragment();
//                break;
//
//            case 4:
//                fragment = new CartFragment();
//                break;
//            case 5:
//                fragment = new SubscriptionFragment();
//                break;
//            case 6:
//                fragment = new PracticeFragment();
//                break;

            default:
                break;
        }
        ProjectUtil.replaceFragment(context, fragment);
    }
}
