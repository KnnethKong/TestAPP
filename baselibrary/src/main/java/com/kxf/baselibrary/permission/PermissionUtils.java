package com.kxf.baselibrary.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求权限的工具类
 * Created by 晋 on 2016/11/30.
 */

public class PermissionUtils {
    /**
     * 定位
     */
    public static final int LOCATIONMASSAGE = 0;//定位
    /**
     * 电话
     */
    public static final int CALLPHONEMESSAGE = 1;//打电话
    /**
     * 相机
     */
    public static final int CAMERAMESSAGE = 2;//相机
    /**
     * 存储
     */
    public static final int STORAGEMESSAGE = 3;//
    public static final int RECORDVIDEO = 4;//录音
    public static String setAappName = "图书之家";
    private static String[] messages = {"定位", "拨打电话", "相机", "读写SD卡", "录音"};//提示信息

    /**
     * 在ACTIVITY中请求相机权限的方法
     *
     * @param mactivity      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstActivityCaramer(Activity mactivity, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mactivity,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mactivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mactivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (mactivity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        || mactivity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || mactivity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) {
                    // 是否显示不在提醒
                    myDialog(mactivity, CAMERAMESSAGE, callBack);
                } else {
                    ActivityCompat.requestPermissions(mactivity, new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }


    /**
     * 在ACTIVITY中请求录音权限的方法
     *
     * @param mactivity      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstActivityRecord(Activity mactivity, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mactivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mactivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mactivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (mactivity.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) || mactivity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || mactivity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // 是否显示不在提醒
                    myDialog(mactivity, RECORDVIDEO, callBack);
                } else {
                    ActivityCompat.requestPermissions(mactivity, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在Fragment中请求相机权限的方法
     *
     * @param mfragment      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstFragmentCamera(Fragment mfragment, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mfragment.getActivity(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mfragment.getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mfragment.getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (mfragment.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        || mfragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || mfragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) {
                    // 是否显示不在提醒
                    if (mfragment.getActivity() != null) {
                        myDialog(mfragment.getActivity(), CAMERAMESSAGE, callBack);
                    } else {
                        myDialog(mfragment.getActivity(), CAMERAMESSAGE, callBack);
                    }
                } else {
                    mfragment.requestPermissions(new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在Activity中请求拍照的方法
     *
     * @param mactivity      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstActivityLocaltion(Activity mactivity, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean b2 = ContextCompat.checkSelfPermission(mactivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
            boolean b3 = ContextCompat.checkSelfPermission(mactivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
//            boolean b4 = ContextCompat.checkSelfPermission(mactivity,
//                    Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED;
            boolean b = mactivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            boolean b1 = mactivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (b2 || b3) {
                if (b || b1) {
                    myDialog(mactivity, perRequestCode, callBack);
                } else {
                    ActivityCompat.requestPermissions(mactivity, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    }, perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在Fragment中请求拍照的方法
     *
     * @param mactivity      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstFragmentLocaltion(Fragment mactivity, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mactivity.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mactivity.getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (mactivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        ) {
                    // 是否显示不在提醒
                    if (mactivity.getActivity() != null) {
                        myDialog(mactivity.getActivity(), LOCATIONMASSAGE, callBack);
                    } else {
                        myDialog(mactivity.getActivity(), LOCATIONMASSAGE, callBack);
                    }
                } else {
                    mactivity.requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在Activity中请求存储权限的方法  可以用在本地图库选择等
     *
     * @param mActivity      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstAcivityStorage(Activity mActivity, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (mActivity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || mActivity.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) {
                    // 是否显示不在提醒
                    myDialog(mActivity, STORAGEMESSAGE, callBack);
                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在Fragment中请求存储权限的方法  可以用在本地图库选择等
     *
     * @param mfragment      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstFragmentStorage(Fragment mfragment, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mfragment.getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(mfragment.getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                mfragment.requestPermissions(new String[]{
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE},
//                        perRequestCode);
                if (mfragment.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || mfragment.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) {
                    // 是否显示不在提醒
                    if (mfragment.getActivity() != null) {
                        myDialog(mfragment.getActivity(), STORAGEMESSAGE, callBack);
                    } else {
                        myDialog(mfragment.getActivity(), STORAGEMESSAGE, callBack);
                    }
                } else {
                    mfragment.requestPermissions(new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在ACTIVITY中请求电话权限的方法
     *
     * @param mactivity      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstActivityCallPhone(Activity mactivity, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mactivity,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (mactivity.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    // 是否显示不在提醒
                    myDialog(mactivity, CALLPHONEMESSAGE, callBack);
                } else {
                    ActivityCompat.requestPermissions(mactivity, new String[]{
                                    Manifest.permission.CALL_PHONE},
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 在Fragment中请求打电话权限的方法
     *
     * @param mfragment      所在activity
     * @param perRequestCode 请求码
     * @param callBack       回调
     */
    public static void requstFragmentCallPhone(Fragment mfragment, int perRequestCode, OnRequestCarmerCall callBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mfragment.getActivity(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (mfragment.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    // 是否显示不在提醒
                    if (mfragment.getActivity() != null) {
                        myDialog(mfragment.getActivity(), CALLPHONEMESSAGE, callBack);
                    } else {
                        myDialog(mfragment.getActivity(), CALLPHONEMESSAGE, callBack);
                    }
                } else {
                    mfragment.requestPermissions(new String[]{
                                    Manifest.permission.CALL_PHONE},
                            perRequestCode);
                }
            } else {
                //有权限
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }
        } else {
//			有权限
            if (callBack != null) {
                callBack.onSuccess();
            }
        }
    }

    /**
     * 定位权限申请结果
     *
     * @param context
     * @param message      提示信息
     * @param grantResults
     */
    public static void perMissionLocationResult(Context context, int message,
                                                int[] grantResults, OnRequestCarmerCall callBack) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (callBack != null)
                callBack.onSuccess();
        } else {
            myDialog(context, message, callBack);
        }
    }

    /**
     * 定位权限申请结果
     *
     * @param context
     * @param message      提示信息
     * @param grantResults
     */
    public static void perMissionLocationResult(Activity context, int message,
                                                int[] grantResults, OnRequestCarmerCall callBack) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
            myDialog(context, message, callBack);
        } else {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    ) {
                if (callBack != null)
                    callBack.onSuccess();
            } else {
                myDialog(context, message, callBack);
            }
        }
    }

    /**
     * 相机权限申请结果
     *
     * @param context
     * @param message      提示信息
     * @param grantResults
     */
    public static void perMissionCameraResult(Context context, int message,
                                              int[] grantResults, OnRequestCarmerCall callBack) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                ) {
            if (callBack != null)
                callBack.onSuccess();
        } else {
            myDialog(context, message, callBack);
        }
    }

    /**
     * 存储权限申请结果
     *
     * @param context
     * @param message      提示信息
     * @param grantResults
     */
    public static void perMissionStorageResult(Context context, int message,
                                               int[] grantResults, OnRequestCarmerCall callBack) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
            if (callBack != null)
                callBack.onSuccess();
        } else {
            myDialog(context, message, callBack);
        }
    }

    /**
     * 电话权限申请结果
     *
     * @param context
     * @param message      提示信息
     * @param grantResults
     */
    public static void perMissionCallPhoneResult(Context context, int message,
                                                 int[] grantResults, OnRequestCarmerCall callBack) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (callBack != null)
                callBack.onSuccess();
        } else {
            myDialog(context, message, callBack);
        }
    }

    private static void myDialog(final Context context, int message, final OnRequestCarmerCall callBack) {
        String showMessage = "相应的权限";
        switch (message) {
            case LOCATIONMASSAGE:
                showMessage = messages[message];
                break;
            case CALLPHONEMESSAGE:
                showMessage = messages[message];
                break;
            case CAMERAMESSAGE:
                showMessage = messages[message];
                break;
            case STORAGEMESSAGE:
                showMessage = messages[message];
                break;
            case RECORDVIDEO:
                showMessage = messages[message];
                break;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("在设置-应用-" + setAappName + "-权限，中设置" + showMessage + "等权限，以正常使用" + showMessage + "等功能");

        builder.setTitle("权限申请");

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                context.startActivity(intent);
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (callBack != null) {
                    callBack.onDilogCancal();
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    /**
     * 回调接口
     */
    public interface OnRequestCarmerCall {
        void onSuccess();

        //        void onRefused();
        void onDilogCancal();
    }

    /**
     * 权限申请--所有的
     *
     * @param activity
     */

    public static void checkRequiredPermission(final Activity activity, String[] permissionsArray, int rqCode) {
        //还需申请的权限列表
        List<String> permissionsList = new ArrayList<String>();
        permissionsList.clear();
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList != null && permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), rqCode);
        }
    }
}
