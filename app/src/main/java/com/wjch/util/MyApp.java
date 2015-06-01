package com.wjch.util;
import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;

public class MyApp extends Application {
    private List<Activity> mList = new LinkedList<Activity>();
    private static MyApp instance;

    public MyApp() {  
    }

    public synchronized static MyApp getInstance() {
        if (null == instance) {
            instance = new MyApp();
        }
        return instance;
    }
    // add Activity 
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();    
        System.gc();
    }

}
