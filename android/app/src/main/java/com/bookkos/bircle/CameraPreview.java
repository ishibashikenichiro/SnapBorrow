package com.bookkos.bircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    UpLoadFileTest upload = new UpLoadFileTest();
    SurfaceHolder mSurfaceHolder;
    public static final String PREFERENCES_FILE_NAME = "user_preference";
    public static int userId;
    public static int groupId;
    private String regId;
    private String groupName;
    public int n = 0;
    public String name;
    public String count;
    Camera mCamera;
    Context context;

    public CameraPreview(Context context) {
        super(context);
        this.context = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

       /* EditText nametext = new EditText(context);
        new AlertDialog.Builder(context).setTitle("name").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                nametext).setPositiveButton("確認", null)
                .setNegativeButton("取消", null).show();

        name=nametext.getText().toString();
        Log.d(name,name);
        */
      //  inputTitleDialog();
        Toast.makeText(context,"タッチで撮影する",Toast.LENGTH_LONG).show();
        try {
            int openCameraType = Camera.CameraInfo.CAMERA_FACING_BACK;
            if (openCameraType <= Camera.getNumberOfCameras()) {
                mCamera = Camera.open(openCameraType);
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
            } else {
                Log.d("CameraSample", "cannot bind camera.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        msetPreviewSize(width, height);
        mCamera.startPreview();
    }

    protected void msetPreviewSize(int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> supported = params.getSupportedPreviewSizes();
        if (supported != null) {
            for (Camera.Size size : supported) {
                //Log.d("tiaoshi", "" + size.width + size.height);
                if (size.width <= width && size.height <= height) {
                    params.setPictureFormat(ImageFormat.JPEG);
                    params.setJpegQuality(100);
                    params.setJpegThumbnailSize(0,0);
                    params.setPictureSize(320, 240);
                    params.setPreviewSize(320, 240);

                    mCamera.setParameters(params);
                    break;
                }
            }
        }
    }

    private Camera.ShutterCallback mShutterListener =
            new Camera.ShutterCallback() {
                public void onShutter() {
                    // TODO Auto-generated method stub
                }
            };
    public int c = 0;


    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mCamera != null) {
                mCamera.takePicture(mShutterListener, null, mPictureListener);
               // if (n == 1) {
                //    c++;
                 //   count = c + "枚";
                 //   Toast.makeText(context, count, Toast.LENGTH_SHORT).show();
                //}
            }
        }
        return true;
    }


    private Camera.PictureCallback mPictureListener =
            new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    getUserData();
                    String userId_str = String.valueOf(userId);
                    String groupId_str=String.valueOf(groupId);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String realtime = formatter.format(curDate);
                    String fullname;
                  /*  if (n == 1) {
                        fullname = name + c;
                        if (data != null) {
                            try {
                                File imageFile = new File("/sdcard/" + fullname + ".jpg");
                                FileOutputStream outStream = new FileOutputStream(imageFile);
                                outStream.write(data);
                                outStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            camera.startPreview();
                        }
                    } else {
                        fullname =realtime;
                        */
                        if (data != null) {
                            try {
                                File imageFile = new File("/sdcard/" + realtime + ".jpg");
                                FileOutputStream outStream = new FileOutputStream(imageFile);
                                outStream.write(data);
                                String name = URLEncoder.encode(realtime, "utf-8");
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("NAME", name);
                                params.put("USERID",userId_str);
                                params.put("GROUPID",groupId_str);
                                Map<String, File> files = new HashMap<String, File>();
                                files.put(name+".jpg", imageFile);
                                upload.post(BircleTools.BircleHome+"/equipment/oneupload", params, files);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            camera.stopPreview();
                           // camera.release();
                            final Intent localIntent = new Intent(context, EquipmentWebViewActivity.class);
                            Timer timer = new Timer();
                            TimerTask tast = new TimerTask() {
                                @Override
                                public void run() {
                                    context.startActivity(localIntent);
                                }
                            };
                            Toast.makeText(context, "識別中",Toast.LENGTH_LONG).show();
                           timer.schedule(tast,100);
                        }
                  //  }

                }
            };

    private void inputTitleDialog() {

        final EditText inputServer = new EditText(context);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("object name").setView(inputServer).setNegativeButton(
                "cancel", null);
        builder.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        name = inputServer.getText().toString();
                        n=1;
                    }
                });

        builder.show();
    }
    private void getUserData() {
        // preferenceフォルダにあるxmlファイルから値を取得する
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        //
        if(settings == null) {
            return ;
        }

        userId = (int) settings.getLong("user_id", 0);
        groupId = (int) settings.getLong("group_id", 0);
        regId = settings.getString("reg_id", "");
        groupName = settings.getString("group_name", "");

    }
/*  private void setRightCameraOrientation(int cameraId, Camera mCamera) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        //
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }
    */
}


/*
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] imageData, Camera c) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        uploadPhoto(imageData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            mCamera.startPreview();
        }


    };
*/


  /*  private void showDialog(String mess)
    {  final Activity mActivity = (Activity)this.getContext();
        new AlertDialog.Builder(mActivity).setTitle("Message")
                .setMessage(mess)
                .setNegativeButton("确定",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }
    }
}
*/