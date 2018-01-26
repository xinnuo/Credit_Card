package com.ruanmeng.receiver;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.lzy.okgo.utils.OkLogger;

import java.util.List;

import cn.jpush.android.service.PushService;

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2018-01-25 15:57
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    private int mJobId = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OkLogger.i("JobSchedulerService", "jobService启动");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        OkLogger.i("JobSchedulerService", "执行了onStartJob方法");
        boolean isPushServiceWork = isServiceWork(this, "io.rong.push.PushService");
        boolean isJpushServiceWork = isServiceWork(this, "cn.jpush.android.service.PushService");

        OkLogger.i("JobSchedulerService", "isPushServiceWork：" + String.valueOf(isPushServiceWork));
        OkLogger.i("JobSchedulerService", "isJpushServiceWork：" + String.valueOf(isJpushServiceWork));

        if (!isPushServiceWork) this.startService(new Intent(this,io.rong.push.PushService.class));
        if (!isJpushServiceWork) this.startService(new Intent(this,PushService.class));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        OkLogger.i("JobSchedulerService", "执行了onStopJob方法");
        scheduleJob(getJobInfo());
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo() {
        @SuppressLint("JobSchedulerService")
        JobInfo.Builder builder = new JobInfo.Builder(mJobId++, new ComponentName(this, JobSchedulerService.class));
        /**
         * 设置设备执行的网络条件：
         *   NETWORK_TYPE_UNMETERED   不计量网络（wifi）
         *   NETWORK_TYPE_NOT_ROAMING 非漫游网络
         *   NETWORK_TYPE_ANY         任何网络
         *   NETWORK_TYPE_NONE        无论是否有网络都执行
         */
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        //设置在设备重新启动继续执行
        builder.setPersisted(true);
        //设置在设备充电时执行
        builder.setRequiresCharging(true);
        //设置在设备空闲时间执行
        builder.setRequiresDeviceIdle(false);
        //间隔时间
        builder.setPeriodic(3000);
        return builder.build();
    }

    //判断服务是否正在运行
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) return false;
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
