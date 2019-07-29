package br.com.bangu_ao_vivo.bangu.Helper;

import android.annotation.SuppressLint;
import android.support.design.widget.BottomNavigationView;

/**
 * Created by Toshiba pc on 14/03/2019.
 */

public class BottomNavigationViewHelper {
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode (BottomNavigationView view) {
        /*
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                item.setPadding(0, 15, 0, 0);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

*/
    }

}
