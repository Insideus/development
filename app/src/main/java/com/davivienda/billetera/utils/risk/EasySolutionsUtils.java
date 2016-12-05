package com.davivienda.billetera.utils.risk;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.davivienda.billetera.constants.constants.Constants;
import com.davivienda.billetera.service.Service;

import net.easysol.dsb.DSB;
import net.easysol.dsb.device_protector.SIMChangeListener;
import net.easysol.dsb.malware_protector.overlay.OverlayListener;

/**
 * Created by Julian Vega on 23/09/2016.
 */
public class EasySolutionsUtils extends RiskUtils {

    public static void initEasySolution(Application app, Context ctx) {
        DSB.sdk(ctx).init(Constants.EASY_SOLUTIONS_DESARROLLO_LICENCIA);
        DSB.sdk(ctx).setEventsReportingEnabled(true);
        initEasySolutionMalwareProtection(app, ctx);
    }

    public static void initEasySolutionMalwareProtection(Application application, Context ctx) {
        DSB.sdk(ctx).MALWARE_PROTECTOR_API.startOverlappingProtection(application);
    }

    public static void setMalwareDetectedListener(Context ctx, OverlayListener listener) {
        DSB.sdk(ctx).MALWARE_PROTECTOR_API.addOverlayListener(listener);
    }

    public static boolean isSecure(Context ctx) {
        return DSB.sdk(ctx).CONNECTION_PROTECTOR_API.isSecureByRiskRules();
    }

    public static boolean isSecureCertificate(Context ctx) {
        boolean secureCertificate = DSB.sdk(ctx).CONNECTION_PROTECTOR_API.isSecureCertificate(Service.BASE_URL);
        Log.d("SECURE_CERTIFICATE", String.valueOf(secureCertificate));
        return secureCertificate;
    }

    public static void setSimChangeListener(Context ctx, SIMChangeListener listener) {
        DSB.sdk(ctx).DEVICE_PROTECTOR_API.setSimChangeListener(listener);
    }

}
