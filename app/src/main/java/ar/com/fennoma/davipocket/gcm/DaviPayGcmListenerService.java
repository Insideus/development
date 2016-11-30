package ar.com.fennoma.davipocket.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import ar.com.fennoma.davipocket.R;
import ar.com.fennoma.davipocket.activities.OrderReceiptActivity;
import ar.com.fennoma.davipocket.model.Cart;
import ar.com.fennoma.davipocket.model.NotificationType;
import ar.com.fennoma.davipocket.session.Session;

public class DaviPayGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if(Session.getCurrentSession(getApplicationContext()).isValid()) {
            String text = data.getString("message");
            try {
                JSONObject notification = new JSONObject(text);
                if(text != null && text.length() > 2) {
                    if(notification.has("type")) {
                        NotificationType type = NotificationType.getType(notification.getString("type"));
                        if(type == NotificationType.ORDER_READY && notification.has("order")) {
                            sendOrderReadyNotification(notification.getJSONObject("order"));
                        }
                        if(type == NotificationType.OTT_PAYMENT && notification.has("order")) {
                            sendOttPaymentNotification(notification.getJSONObject("order"));
                        }
                        if(type == NotificationType.OTT_CONFIRMED_PAYMENT && notification.has("order")) {
                            sendOttPaymentConfirmedNotification(notification.getJSONObject("order"));
                        }
                        if(type == NotificationType.OTT_PAY_REJECTED && notification.has("order")) {
                            sendOttPaymentRejectedNotification(notification.getJSONObject("order"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendOttPaymentRejectedNotification(JSONObject order) {
        Cart cart = Cart.fromJson(order);
        if(cart != null) {
            createOttPaymentRejectedNotification(cart);
        }
    }

    private void sendOttPaymentConfirmedNotification(JSONObject order) {
        Cart cart = Cart.fromJson(order);
        if(cart != null) {
            createOttPaymentConfirmedNotification(cart);
        }
    }

    private void sendOttPaymentNotification(JSONObject order) {
        Cart cart = Cart.fromJson(order);
        if(cart != null) {
            createOttPaymentNotification(cart);
        }
    }

    private void sendOrderReadyNotification(JSONObject order) {
        Cart cart = Cart.fromJson(order);
        if(cart != null) {
            createOrderReadyNotification(cart);
        }
    }

    private void createOrderReadyNotification(Cart cart) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        Intent intent = new Intent(this, OrderReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(OrderReceiptActivity.CART_KEY, cart);
        intent.putExtra(OrderReceiptActivity.FROM_ORDER_READY_NOTIFICATION_KEY, true);
        int notificationId = Integer.valueOf(cart.getReceiptNumber());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.order_ready_notification_text, cart.getReceiptNumber()))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void createOttPaymentNotification(Cart cart) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        Intent intent = new Intent(this, OrderReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(OrderReceiptActivity.CART_KEY, cart);
        intent.putExtra(OrderReceiptActivity.FROM_OTT_NOTIFICATION, true);
        int notificationId = Integer.valueOf(cart.getReceiptNumber());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.ott_pay_notification_text))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void createOttPaymentConfirmedNotification(Cart cart) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        Intent intent = new Intent(this, OrderReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(OrderReceiptActivity.CART_KEY, cart);
        intent.putExtra(OrderReceiptActivity.FROM_OTT_CONFIRMED_NOTIFICATION, true);
        int notificationId = Integer.valueOf(cart.getReceiptNumber());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.ott_pay_confirmed_notification_text, cart.getOttTransaccionId()))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void createOttPaymentRejectedNotification(Cart cart) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
        Intent intent = new Intent(this, OrderReceiptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(OrderReceiptActivity.CART_KEY, cart);
        intent.putExtra(OrderReceiptActivity.FROM_OTT_REJECTED_NOTIFICATION_KEY, true);
        int notificationId = Integer.valueOf(cart.getReceiptNumber());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.ott_pay_rejected_notification_text, cart.getOttTransaccionId()))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

}
