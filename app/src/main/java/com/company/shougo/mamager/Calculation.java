package com.company.shougo.mamager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.company.shougo.data.TravelData;
import com.company.shougo.db.MyTravelDB;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculation {

    /*******************************
     *
     *     在setContentView之前設定
     *     不會被手機系統設定文字大小影響
     *     強制設定放大倍率為 configuration.fontScale
     *
     * @param activity
     * @param configuration - getResources().getConfiguration()
     */
    public static void adjustFontScale(Activity activity,Configuration configuration)
    {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(metrics);
        }
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        activity.getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    /*******************
     * 檢查ＥＭＡＩＬ格式
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /********************
     * 檢查姓名格式
     * @param name
     * @return
     */
    public static boolean isNameValid(String name){
        if (name==null || name.length()<=0){
            return false;
        }
        return true;
    }

    /*********************
     * 檢查密碼格式
     * @param pwd
     * @return
     */
    public static boolean isPwdValid(String pwd){
        if (pwd==null || pwd.length()<6 || pwd.length()>18){
            return false;
        }
        return true;
    }

    /************
     * URI轉PATH
     * @param context
     * @param data
     * @return
     */
    public static String getPathByUri(Context context, Uri data) {
        String filename=null;
        if (data==null) return null;
        if (data.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(data, new String[] { "_data" }, null, null, null);
            if (cursor.moveToFirst()) {
                filename = cursor.getString(0);
            }
        } else if (data.getScheme().toString().compareTo("file") == 0) {// file:///开头的uri
            filename = data.toString().replace("file://", "");// 替换file://
            if (!filename.startsWith("/mnt")) {// 加上"/mnt"头
                filename += "/mnt";
            }
        }
        return filename;
    }


    public static String getImagePath(Context context,Uri uri,String selection){
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri,null,selection,null,null);
        if (cursor.moveToFirst()){
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        cursor.close();
        return path;
    }

    public static float convertDpToPixel(float dp, Context context){
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static Bitmap compressImage(Bitmap image, String filepath) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 質量壓縮方法，這裡100表示不壓縮，把壓縮後的資料存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 1024) { // 迴圈判斷如果壓縮後圖片是否大於200kb,大於繼續壓縮
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 這裡壓縮options%，把壓縮後的資料存放到baos中
            options -= 10;// 每次都減少10
        }
        try {
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 读取照片的旋转角度
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return degree;
    }

    /**
     * 旋转照片
     * @param bitmap
     * @param rotate
     * @return
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }


    /**
     * 根据图片路径压缩图片并返回压缩后图片路径
     * @param path
     * @return
     */
    public static String compress(String path) {
        Bitmap bm = compressImage(getImage(path), path);//压缩照片
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(path);//读取照片旋转角度
        if (degree > 0) {
            bm = rotateBitmap(bm, degree); //旋转照片
        }
        String file = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "temp" + File.separator;
        File f = new File(file);
        if (!f.exists()) {
            f.mkdirs();
        }
        String picName = System.currentTimeMillis() + ".jpg";
        String resultFilePath = file + picName;
        try {
            FileOutputStream out = new FileOutputStream(resultFilePath);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultFilePath;

    }

    public static Bitmap getImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap, srcPath);//压缩好比例大小后再进行质量压缩
    }

    public static boolean isAddLocalTravel(Context context, long vendorId){
        MyTravelDB myTravelDB = new MyTravelDB(context);
        List<TravelData> list = myTravelDB.getAllByEmail();

        boolean isHave = false;

        for (int i=0;i<list.size();i++){
            if (list.get(i).getVendor_id()==vendorId){
                isHave = true;
                break;
            }
        }

        return isHave;

    }

}
