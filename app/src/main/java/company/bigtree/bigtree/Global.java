package company.bigtree.bigtree;

import android.app.Application;

import com.shamanland.fonticon.FontIconTypefaceHolder;

/**
 * Created by shenzebang on 15/11/6.
 */
public class Global extends Application{




    private static final String ACTIVITY_TO_SERVICE="com.bigtree.activitytoservice";
    private static final String SERVICE_TO_ACTIVITY="com.bigtree.servicetoactivity";


    private boolean startedService;

    /*URl 汇总*/
    private final String serverUrl="xxxxxxxxx";

    public static String getActivityToService() {
        return ACTIVITY_TO_SERVICE;
    }

    public static String getServiceToActivity() {
        return SERVICE_TO_ACTIVITY;
    }

    public boolean isStartedService() {
        return startedService;
    }

    public void setStartedService(boolean startedService) {
        this.startedService = startedService;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startedService=false;

    }
}
