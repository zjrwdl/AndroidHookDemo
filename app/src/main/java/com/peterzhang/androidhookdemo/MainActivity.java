package com.peterzhang.androidhookdemo;

import android.database.CursorWindow;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private volatile static Object sOriginalReporter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("HookCloseGuard");
    }

    private void testAllocate(){
        CursorWindow cursorWindow = new CursorWindow("test");
    }

    public void allocate(View view){
        testAllocate();
    }

    public void hook(View view){
        tryHook();
    }

    private boolean tryHook() {
        try {
            Class<?> closeGuardCls = Class.forName("dalvik.system.CloseGuard");
            Class<?> closeGuardReporterCls = Class.forName("dalvik.system.CloseGuard$Reporter");
            Field fieldREPORTER = closeGuardCls.getDeclaredField("REPORTER");
            Field fieldENABLED = closeGuardCls.getDeclaredField("ENABLED");
            fieldREPORTER.setAccessible(true);
            fieldENABLED.setAccessible(true);
            sOriginalReporter = fieldREPORTER.get(null);
            fieldENABLED.set(null, true);
            ClassLoader classLoader = closeGuardReporterCls.getClassLoader();
            if (classLoader == null) {
                return false;
            }
            fieldREPORTER.set(null, Proxy.newProxyInstance(classLoader,
                    new Class<?>[]{closeGuardReporterCls},
                    new IOCloseLeakDetector(sOriginalReporter)));
            fieldREPORTER.setAccessible(false);
            return true;
        } catch (Throwable e) {
            Log.e(TAG, "tryHook exp=%s", e);
        }

        return false;
    }

    class IOCloseLeakDetector implements InvocationHandler {
        private final Object originalReporter;
        public IOCloseLeakDetector(Object originalReporter) {
            this.originalReporter = originalReporter;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("report")) {
                Log.d(TAG,"invoke hook method");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"invoke hook method",Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            }
            return method.invoke(originalReporter, args);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }
}