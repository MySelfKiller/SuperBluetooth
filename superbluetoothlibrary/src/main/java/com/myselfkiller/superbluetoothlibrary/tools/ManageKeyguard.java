package com.myselfkiller.superbluetoothlibrary.tools;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.KeyguardManager.OnKeyguardExitResult;
import android.content.Context;

@SuppressWarnings("deprecation")
public class ManageKeyguard {
    private static KeyguardManager myKM = null;
    @SuppressWarnings("deprecation")
	private static KeyguardLock myKL = null;

    public static synchronized void initialize(Context context) {
        if (myKM == null) {
            myKM = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
        }
    }

    public static synchronized void disableKeyguard(Context context) {
        initialize(context);

        if (myKM.inKeyguardRestrictedInputMode()) {
            myKL = myKM.newKeyguardLock("ManageKeyGuard");
            myKL.disableKeyguard();
            
        } else {
            myKL = null;
        }
    }

    public static synchronized boolean inKeyguardRestrictedInputMode() {
        if (myKM != null) {
            return myKM.inKeyguardRestrictedInputMode();
        }
        return false;
    }

    public static synchronized void reenableKeyguard() {
        if (myKM != null) {
            if (myKL != null) {
                myKL.reenableKeyguard();
                myKL = null;
            }
        }
    }

    public static synchronized void exitKeyguardSecurely(
            final LaunchOnKeyguardExit callback) {
        if (inKeyguardRestrictedInputMode()) {
            myKM.exitKeyguardSecurely(new OnKeyguardExitResult() {
                public void onKeyguardExitResult(boolean success) {
                    reenableKeyguard();
                    if (success) {
                        callback.LaunchOnKeyguardExitSuccess();
                    } else {

                    }
                }
            });
        } else {
            callback.LaunchOnKeyguardExitSuccess();
        }
    }

    public interface LaunchOnKeyguardExit {
        public void LaunchOnKeyguardExitSuccess();
    }
}