package ar.com.fennoma.davipocket.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import ar.com.fennoma.davipocket.DavipocketApplication;
import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.service.Service;

public class ImageUtils {

    private static void loadImage(Activity activity, final ImageView imageView, final String imageUrl, final int placeholder) {
        if (imageUrl == null) {
            imageView.setImageResource(placeholder);
        } else {
            DavipocketApplication.getImageManager().displayImage(getImageUrl(imageUrl), imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String arg0, View arg1) {

                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {

                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {

                }
            });
        }
    }

    public static void loadImageFullURL(final ImageView imageView, String imageUrl){
        if (imageUrl == null) {
            return;
        }
        DavipocketApplication.getImageManager().displayImage(imageUrl, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {}

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }

    public static void loadCardImage(Activity activity, final ImageView imageView, final String imageUrl) {
        loadImage(activity, imageView, imageUrl, R.drawable.card_placeholder);
    }

    public static String getImageUrl(String image) {
        return Service.IMAGE_BASE_URL + image;
    }

}
