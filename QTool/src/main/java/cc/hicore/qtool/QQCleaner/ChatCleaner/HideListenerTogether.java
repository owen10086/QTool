package cc.hicore.qtool.QQCleaner.ChatCleaner;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.UIItem;
import cc.hicore.HookItemLoader.Annotations.VerController;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.MethodFinderBuilder;
import cc.hicore.HookItemLoader.bridge.QQVersion;
import cc.hicore.HookItemLoader.bridge.UIInfo;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import de.robv.android.xposed.XposedBridge;

@XPItem(name = "屏蔽一起听歌顶栏",itemType = XPItem.ITEM_Hook,targetVer = QQVersion.QQ_8_8_90)
public class HideListenerTogether{
    @UIItem
    @VerController
    public UIInfo getUI(){
        UIInfo ui = new UIInfo();
        ui.name = "屏蔽一起听歌顶栏";
        ui.groupName = "聊天界面净化";
        ui.targetID = 2;
        ui.type = 1;
        return ui;
    }
    @VerController
    @MethodScanner
    public void getHookMethod(MethodContainer container){
        container.addMethod("hook",MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.listentogether.ui.BaseListenTogetherPanel"),null,void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.listentogether.ListenTogetherSession")
        }));

    }
    @VerController
    @XPExecutor(methodID = "hook")
    public BaseXPExecutor worker(){
        return param -> param.setResult(null);
    }
}
