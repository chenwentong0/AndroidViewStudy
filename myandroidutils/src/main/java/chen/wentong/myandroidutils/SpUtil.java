package chen.wentong.myandroidutils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by ${wentong.chen} on 18/1/24.
 */

public class SpUtil {

    public static int sp2px(Context context, int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, int dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }
}
