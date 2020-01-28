package wallsplash.ankitray.com.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;

import wallsplash.ankitray.com.wallsplash.R;


public class AppUtils {

    public static void ShortToast(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View CustomView = inflater.inflate(R.layout.customtoast, null);
        Toast toast = new Toast(context);
        TextView TvMsg = CustomView.findViewById(R.id.TvMsg);
        TvMsg.setText(msg);
        // Set layout to toast
        toast.setView(CustomView);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 300);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showAlertDialog(Context context, String Title, String Msg) {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setMessage(Msg)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void showAlertDialogLogout(Context context, String Title, String Msg) {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setMessage(Msg)
                .setCancelable(false)
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                })
                .setPositiveButton(context.getString(R.string.logout), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                )
                ;
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static boolean RemoveImagesFromDevice(String uri) {
        File fdelete = new File(uri);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + uri);
                return true;
            } else {
                System.out.println("file not Deleted :" + uri);
                return false;
            }
        }
        return false;
    }


    public static File getPrivateFileHandle(Context context) throws FileNotFoundException {
        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        String fileName = String.format(File.separator + "IMG_%d.jpg", System.currentTimeMillis());
        if (externalFilesDir == null) {
            throw new FileNotFoundException("Failed to get directory handle to DIRECTORY_PICTUERS");
        }
        Log.d("AddBusinessActivity", "Creating file: " + externalFilesDir.getAbsolutePath() + fileName);

        return new File(externalFilesDir, fileName);
    }


    public static void shareIntent(Context context, String link) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public static float getKmFromLatLong(float lat1, float lon1, float lat2, float lon2) {
        Location loc1 = new Location("");

        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);
        float distanceInMeters = loc1.distanceTo(loc2);

        float dist = distanceInMeters / 1000;
        Log.e("dist", String.valueOf(dist));

        return dist;
    }

    public static void shareApp(Context context) {
        String TellFamilyFriend = "Hey check out my app at: https://play.google.com/store/apps/details?id=com.itechnotion.sharelocation&hl=en";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.app_name) + "\n" + TellFamilyFriend);
        // Log.d("ShareURL",getResources().getString(R.string.app_name_dialog) +" | "+ Constants.TellFamilyFriend);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Share using"));
    }
}
