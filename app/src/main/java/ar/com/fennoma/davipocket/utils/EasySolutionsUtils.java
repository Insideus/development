package ar.com.fennoma.davipocket.utils;

import android.content.Context;

import net.easysol.dsb.DSB;

/**
 * Created by Julian Vega on 23/09/2016.
 */
public class EasySolutionsUtils {

    public static void scanDeviceStatus(Context context){
        DSB.sdk(context).DEVICE_PROTECTOR_API.scanDeviceStatus();
    }

    public static void scanDeviceHosts(Context context){
        boolean scan = DSB.sdk(context).DEVICE_PROTECTOR_API.scanDeviceHosts();
        if(scan){
            //Código del cliente
        }
        else{
            //Código del cliente
        }
    }

    public static String getDeviceId(Context context){
        return DSB.sdk(context).getDeviceID();
    }

}
