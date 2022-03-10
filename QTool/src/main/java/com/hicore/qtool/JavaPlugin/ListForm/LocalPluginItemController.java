package com.hicore.qtool.JavaPlugin.ListForm;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hicore.LogUtils.LogUtils;
import com.hicore.Utils.FileUtils;
import com.hicore.Utils.NameUtils;
import com.hicore.Utils.Utils;
import com.hicore.qtool.HookEnv;
import com.hicore.qtool.JavaPlugin.Controller.PluginSetController;
import com.hicore.qtool.R;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Properties;

public final class LocalPluginItemController {
    private static final String TAG = "LocalPluginController";
    private RelativeLayout mRoot;
    private String PluginPath;
    private int MeasureHeight;
    public static LocalPluginItemController create(Context context){
        LocalPluginItemController controller = new LocalPluginItemController(context);
        return controller;
    }
    private boolean IsChangeSize = false;
    private boolean IsExpand = false;
    private LocalPluginItemController(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        mRoot = (RelativeLayout) inflater.inflate(R.layout.plugin_item_local,null);
        mRoot.setOnClickListener(v-> ClickMain());
    }
    private void ClickMain(){
        if (IsChangeSize)return;
        IsChangeSize = true;
        if (IsExpand){
            LinearLayout lExpand = mRoot.findViewById(R.id.HideBar);
            lExpand.getLayoutParams().height = MeasureHeight;
            lExpand.requestLayout();
            int targetHeight = 0;
            Animation scale = new Animation(){
                int initialHeight;
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    lExpand.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
                    lExpand.requestLayout();
                }

                @Override
                public void initialize(int width, int height, int parentWidth, int parentHeight) {
                    initialHeight = height;
                    super.initialize(width, height, parentWidth, parentHeight);
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };
            scale.setDuration(400);
            scale.setFillAfter(true);
            lExpand.startAnimation(scale);
            lExpand.setVisibility(View.INVISIBLE);

            new Handler(Looper.getMainLooper())
                    .postDelayed(()->{
                        lExpand.setVisibility(View.GONE);
                        IsChangeSize = false;
                        IsExpand = false;
                        lExpand.clearAnimation();
                        },500);
        }else {
            LinearLayout lExpand = mRoot.findViewById(R.id.HideBar);
            lExpand.getLayoutParams().height = 0;
            lExpand.requestLayout();
            int targetHeight = MeasureHeight;
            Animation scale = new Animation(){
                int initialHeight = 0;
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    lExpand.getLayoutParams().height = initialHeight + (int) ((targetHeight - initialHeight) * interpolatedTime);
                    lExpand.requestLayout();
                }

                @Override
                public void initialize(int width, int height, int parentWidth, int parentHeight) {
                    initialHeight = height;
                    super.initialize(width, height, parentWidth, parentHeight);
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };
            scale.setDuration(400);
            scale.setFillAfter(true);
            lExpand.startAnimation(scale);
            lExpand.setVisibility(View.INVISIBLE);

            new Handler(Looper.getMainLooper())
                    .postDelayed(()->{
                        lExpand.setVisibility(View.VISIBLE);
                        IsChangeSize = false;
                        IsExpand = true;
                        lExpand.clearAnimation();
                        },500);
        }
    }
    public boolean checkAndLoadPluginInfo(String PluginRootPath){
        File propPath = new File(PluginRootPath,"info.prop");
        if (!propPath.exists())return false;
        try {
            Properties props = new Properties();
            String propString = FileUtils.ReadFileString(propPath.getAbsolutePath());
            props.load(new StringReader(propString));
            setTitle(props.getProperty("name","未设定名字"), Color.BLACK);
            setAuthor("作者:"+props.getProperty("author","未设定作者"));
            setVersion("版本号:"+props.getProperty("version","未设定版本号"));
            setDesc(FileUtils.ReadFileString(new File(PluginRootPath,"desc.txt")));
            String PluginID = props.getProperty("id", NameUtils.GetRandomName());
            setBlackOrWhileMode(PluginSetController.IsBlackMode(PluginID));

            InitMeasure();
            return true;

        } catch (IOException e) {
            LogUtils.warning(TAG,"Can't decode plugin prop file:"+propPath.getAbsolutePath());
            return false;
        }
    }
    public LinearLayout.LayoutParams getParams(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(Utils.dip2sp(mRoot.getContext(),16),Utils.dip2sp(mRoot.getContext(),2),
                Utils.dip2sp(mRoot.getContext(),16),Utils.dip2sp(mRoot.getContext(),2));
        return param;
    }
    public RelativeLayout getRoot(){
        return mRoot;
    }
    private void InitMeasure(){
        LinearLayout ll = mRoot.findViewById(R.id.HideBar);
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        ll.measure(width,height);
        MeasureHeight = ll.getMeasuredHeight();
    }
    public void setTitleColor(int color){
        TextView titleView = mRoot.findViewById(R.id.plugin_title);
        titleView.setTextColor(color);
    }
    private void setTitle(String title,int color){
        TextView titleView = mRoot.findViewById(R.id.plugin_title);
        titleView.setText(title);
        titleView.setTextColor(color);

    }
    private void setVersion(String version){
        TextView versionView = mRoot.findViewById(R.id.plugin_version);
        versionView.setText(version);
    }
    private void setAuthor(String author){
        TextView authorView = mRoot.findViewById(R.id.plugin_author);
        authorView.setText(author);
    }
    private void setDesc(String desc){
        TextView descView = mRoot.findViewById(R.id.plugin_desc);
        descView.setText(desc);
    }
    private void setAutoLoad(boolean isEnable){
        CheckBox authLoad = mRoot.findViewById(R.id.plugin_autoload);
        authLoad.setChecked(isEnable);
    }
    private void setBlackOrWhileMode(boolean blackMode){
        if (blackMode){
            RadioButton button = mRoot.findViewById(R.id.plugin_message_black);
            button.setChecked(true);
        }else {
            RadioButton button = mRoot.findViewById(R.id.plugin_message_while);
            button.setChecked(true);
        }
    }
}