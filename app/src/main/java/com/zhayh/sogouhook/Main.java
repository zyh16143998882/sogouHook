package com.zhayh.sogouhook;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by zhayh on 2017/4/17.
 */
public class Main implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if(loadPackageParam.packageName.equals("com.sohu.inputmethod.sogou")){
            XposedHelpers.findAndHookMethod("bua",
                    loadPackageParam.classLoader,
                    "a",
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.d("sogouTag", (String) param.args[0]);
                        }
                    });

            Log.d("sogouTag1","-------查找删除方法-------");
            XposedHelpers.findAndHookMethod("dbr",
                    loadPackageParam.classLoader,
                    "a",
                    String.class,
                    String.class,
                    new XC_MethodHook() {


                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Log.d("sogouTag1","-------进入dbr.a(String,String)方法-------");
                            Log.d("sogouTag1","str3 = "+param.args[0]);
                            Log.d("sogouTag1","uuds_path = "+param.args[1]);
                            isFile((String)param.args[0]);
                            isFile((String)param.args[1]);

                            //只进行下载过程不进行上传过程，由于下载和上传时目录不一样，所以只能写死

                            //复制uudstmp目录
                            Log.d("sogouTag1","-------开始复制uudstmp-------");
                            String srcDir = "/data/data/com.sohu.inputmethod.sogou/files/dict/uudstmp/";
                            Log.d("sogouTag1","srcDir = "+srcDir);

                            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                Log.d("sogouTag1","SD卡不存在");
                                return;
                            }

                            String destDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test/";
                            Log.d("sogouTag1","destDir = "+destDir);
                            boolean b = CopyFileUtil.copyDirectory(srcDir,destDir,false);

                            if(b){
                                Log.d("sogouTag1","-------拷贝成功-------");
                            }else{
                                Log.d("sogouTag1","-------拷贝失败-------");
                            }


                        }
                    }
            );
        }
    }
    public void isFile(String str){
        File file = new File(str);
        if(file.exists()){
            Log.d("sogouTag1","文件存在"+str);
            if(file.isDirectory()){
                Log.d("sogouTag1","文件时目录"+str);
                String[] list = file.list();
                Log.d("sogouTag1","目录长度"+list.length);
            }else if(file.isFile()){
                Log.d("sogouTag1","文件是单个文件"+str);
            }else{
                Log.d("sogouTag1","文件啥都不是"+str);
            }
        }
    }
}
