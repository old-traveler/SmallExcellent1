package com.enjoy.hyc.map;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.enjoy.base.BasePresenter;
import com.enjoy.base.LogUtils;
import com.enjoy.base.SmallApplication;
import com.enjoy.hyc.adapter.NearJobAdapter;
import com.enjoy.hyc.bean.Job;
import com.enjoy.hyc.util.JobUtil;

import java.util.List;

/**
 * Created by hyc on 2017/4/19 20:27
 */

public class MapPresenter extends BasePresenter<MapContract> implements AdapterView.OnItemClickListener{

    public static com.amap.api.maps.MapView mMapView=null;

    private boolean isSelectedDestination=false;

    public boolean isType_navigation=false;

    public boolean isGetCityName=false;

    public void initMapView(String name){
        mvpView.initMap();
        mvpView.getListView().setOnItemClickListener(this);
        if (isGetCityName=true){
            loadNearData(name);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getLocation(mvpView.getQueryResults().get(position));
    }

    public void enterRoutePlan(){
        if (isSelectedDestination){
            mvpView.startRoutePlan();
        }
    }

    public void loadNearData(String name){
        isGetCityName=true;
        mvpView.showDialog();
        JobUtil.getJobInfoByCityName(name, new JobUtil.OnGetJobInfoListener() {
            @Override
            public void success(List<Job> jobs) {
                if (jobs.size()>0){
                    mvpView.loadNearJobInformationComplete(jobs);
                }
                mvpView.dismissDialog();
            }

            @Override
            public void fail(String error) {
                mvpView.dismissDialog();
                Toast.makeText(SmallApplication.getContext(), "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setListenerForAdapter(){
        mvpView.setItemClickListener(new NearJobAdapter.OnNearJobItemClick() {
            @Override
            public void onClick(int type,Job job) {
                switch (type){
                    case TYPE_DETAILS:
                        mvpView.startDetailsView(job);
                        break;
                    case TYPE_LOCATION:
                        getLocation(job.getJobRegion());
                        break;
                    case TYPE_NAVIGATION:
                        isType_navigation=true;
                        isSelectedDestination=true;
                        getLocation(job.getJobRegion());
                        break;
                }
            }
        });
    }

    public void getLocation(String name){
        mvpView.showDialog();
        mvpView.getLocationByName(name);
        isSelectedDestination=true;
    }
}
