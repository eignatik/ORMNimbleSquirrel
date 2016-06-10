package com.example.myapp.Util;

/**
 * Created by Anama on 26.09.2014.
 */
public class  Log
{
  private static String TAG="MYPROGRAMM";
  private static boolean isDebug=true;

  public static void setTag(String tag){TAG=tag;}
  public static void setDebug(boolean v){isDebug=v;}
    public static boolean isDebug(){return isDebug;}

    public static void v(String msg)
    {

        if(isDebug) android.util.Log.v(TAG, getLocation() + msg);
    }

    private static String getLocation() {
        final String className = Log.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (int i = 0; i < traces.length; i++) {
            StackTraceElement trace = traces[i];

            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + ":" + trace.getMethodName() + ":" + trace.getLineNumber() + "]: ";
                    }
                }
                else if (trace.getClassName().startsWith(className)) {
                    found = true;
                    continue;
                }
            }
            catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!clazz.getSimpleName().isEmpty()) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }
}
