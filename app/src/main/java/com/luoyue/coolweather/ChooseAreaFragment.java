package com.luoyue.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luoyue.coolweather.db.City;
import com.luoyue.coolweather.db.County;
import com.luoyue.coolweather.db.Province;
import com.luoyue.coolweather.util.HttpUtil;
import com.luoyue.coolweather.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    //三级省、市、县代号
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    //页面组件
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    //适配器
    private ArrayAdapter<String> adapter;
    //数据列表
    private List<String> dataList=new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //将choose_area布局添加到这个页面
        View view=inflater.inflate(R.layout.choose_area,container,false);

        //获取页面相应组件
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);

        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //设置列表点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //判断选择的是省、市、县中哪一个
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(i);
                    queryCities();

                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(i);
                    queryCounties();

                }else if(currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get(i).getWeatherId();
                    //根据当前所在的页面进行判断
                    //如果是Main页面，则直接请求天气信息
                    //如果是Weather页面,则关闭滑动菜单，显示下拉刷新进度条，再请求消息
                    if(getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();

                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });

        //回退按钮设置点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }


    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces(){
        titleText.setText("中国");
        //最高一级时,返回按钮不可见
        backButton.setVisibility(View.GONE);

        provinceList= LitePal.findAll(Province.class);

        if(provinceList.size()>0){
            //先清空页面列表数据
            dataList.clear();
            for(Province province:provinceList){
                //添加省份数据
                dataList.add(province.getProvinceName());
            }
            //刷新页面列表
            adapter.notifyDataSetChanged();
            //默认选中第一项
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {
            //如果没有数据，则从服务器拿数据
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }


    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities(){
        //头部设置标题为选中的省份
        titleText.setText(selectedProvince.getProvinceName());
        //返回按钮变为可见
        backButton.setVisibility(View.VISIBLE);
        //根据所选省份找到相应的城市
        cityList= LitePal.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);

        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            //刷新
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            //如果没有数据，则从服务器拿数据
            int provinceCode=selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }


    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties(){
        //头部标题变为选中的某个市
        titleText.setText(selectedCity.getCityName());
        //返回按钮可见
        backButton.setVisibility(View.VISIBLE);
        //查询到所选城市的所有县
        countyList=LitePal.where("cityid=?",String.valueOf(selectedCity.getId())).find(County.class);

        if(countyList.size()>0){
            dataList.clear();
            //添加县
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            //刷新
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            //如果没有数据，则从服务器拿数据
            int provinceCode=selectedProvince.getProvinceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }


    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address,final String type){
        showProgressDialog();

        //向服务器请求数据并处理
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {//如果请求失败
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                if("province".equals(type)){//如果是省份,解析处理数据
                    result= Utility.handleProvinceResponse(responseText);

                }else if("city".equals(type)){//如果是市,解析处理数据
                    result=Utility.handleCityResponse(responseText,selectedProvince.getId());

                }else if("county".equals(type)){//如果是县,解析处理数据
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }

                //如果解析成功，数据库得到结果
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();

                            //重新从数据库加载数据
                            if("province".equals(type)){//如果是省份
                                queryProvinces();
                            }else if("city".equals(type)){//如果是市
                                queryCities();
                            }else if("county".equals(type)){//如果是县
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }


    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
