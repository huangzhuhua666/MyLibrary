package com.example.mylibrary.util.proxy;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import com.example.mylibrary.util.HookUtils;
import com.example.mylibrary.util.ReflectUtils;

import java.util.List;

/**
 * Create by hzh on 2021/01/12.
 */
public class InstrumentationProxy extends Instrumentation {

    private Instrumentation mInstrumentation;
    private PackageManager mPackageManager;

    public InstrumentationProxy(Instrumentation instrumentation, PackageManager packageManager) {
        mInstrumentation = instrumentation;
        mPackageManager = packageManager;
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                            Activity target, Intent intent, int requestCode,
                                            Bundle options) {
        List<ResolveInfo> matchActivities = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);

        Intent newIntent = intent;
        if (matchActivities != null && matchActivities.isEmpty()) {
            newIntent = new Intent();
            newIntent.putExtra(HookUtils.INTENT_REAL_ACTIVITY_NAME, intent.getComponent().getClassName());
            newIntent.setClassName(who, "com.example.mylibrary.ui.activity.StubActivity");
        }

        try {
            return (ActivityResult) ReflectUtils.INSTANCE.getMethod(
                    Instrumentation.class,
                    "execStartActivity",
                    new Class[]{Context.class, IBinder.class, IBinder.class, Activity.class, Intent.class,
                            int.class, Bundle.class}
            ).invoke(mInstrumentation, who, contextThread, token, target, newIntent,
                    requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String realClassName = intent.getStringExtra(HookUtils.INTENT_REAL_ACTIVITY_NAME);
        return super.newActivity(cl, TextUtils.isEmpty(realClassName) ? className : realClassName, intent);
    }
}
