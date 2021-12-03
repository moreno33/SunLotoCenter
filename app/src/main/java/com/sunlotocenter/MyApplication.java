package com.sunlotocenter;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sunlotocenter.activity.LoginActivity;
import com.sunlotocenter.adapter.ClassTypeAdapter;
import com.sunlotocenter.adapter.ClassTypeAdapterFactory;
import com.sunlotocenter.adapter.DateTimeTypeAdapter;
import com.sunlotocenter.adapter.GameTypeAdapter;
import com.sunlotocenter.adapter.LocalTimeTypeAdapter;
import com.sunlotocenter.adapter.UserTypeAdapter;
import com.sunlotocenter.dao.Game;
import com.sunlotocenter.dao.GameResult;
import com.sunlotocenter.dao.GameSchedule;
import com.sunlotocenter.dao.User;
import com.sunlotocenter.service.ConfigurationService;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks{

    private static final String ACCOUNT_PREFERENCES = "com.woolib.ACCOUNT_PREFERENCES";
    /**
     * Networking api. We have only one instance for the
     * whole app.
     */
    private static Retrofit retrofit;

    /**
     * We also want our application to be a singleton. We are
     * going tho keep this instance here.
     */
    private static MyApplication instance;


    private static String APP_PROPERTIES_PATH= "/app.properties";

    /**
     * This is the base URL for the whole app
     */
    public static final String BASE_URL= "http://10.0.0.176:8080";
//    public static final String BASE_URL= "https://www.woolibinc.com";

    private Prefs prefs;

    private Gson gson= new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .registerTypeAdapter(User.class, new UserTypeAdapter<>())
            .registerTypeAdapter(Game.class, new GameTypeAdapter<>())
//            .registerTypeAdapter(Partner.class, new AccountTypeAdapter<Partner>())
//            .registerTypeAdapter(Notification.class, new NotificationTypeAdapter<>())
//            .registerTypeAdapter(Promo.class, new PromoTypeAdapter<>())
            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
            .create();

    private boolean isInBackground;


//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }

    @Override
    public void onCreate() {
        super.onCreate();

//        registerActivityLifecycleCallbacks(this);

        registerActivityLifecycleCallbacks(this);

        //Temporary

        instance= this;

//        JodaTimeAndroid.init(this);

        //Now, let's setup the process lifecycle owner.
//        ProcessLifecycleOwner.get().getLifecycle()
//                .addObserver(new LifecycleObserver() {
//                    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//                    public void onMoveToForeground() {
//                        isInBackground= false;
//                        if(isLoggedIn()) {
//                            //Tell the server the user goes on background
//                            OnlineStatus onlineStatus= new OnlineStatus(null, true, null, getConnectedAccount());
//                            sendOnlineStatusToServer(onlineStatus);
//                        }
//                        Log.i(  TAG, "Returning to foreground…");
//                    }
//
//                    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//                    public void onMoveToBackground() {
//                        isInBackground= true;
//                        if(isLoggedIn()) {
//                            //Tell the server the user goes on background
//                            OnlineStatus onlineStatus= new OnlineStatus(null, false, null, getConnectedAccount());
//                            sendOnlineStatusToServer(onlineStatus);
//                        }
//                        Log.i(TAG, "Moving to background…");
//                    }
//
//                });


    }
//    public void sendOnlineStatusToServer(OnlineStatus onlineStatus){
//        getClientNetworking().create(IAccountRequest.class)
//                .sendOnlineStatusToServer(onlineStatus)
//                .enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                    }
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                    }
//                });
//    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
////        MultiDex.install(this);
//    }

    public static MyApplication getInstance(){
        return instance;
    }

    /**
     * This method allows us to get assert from the system.
     * @return
     */
    public Properties getProperties(){
        Properties properties = new Properties();
        AssetManager assetManager = getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open("app.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    /**
     * This function return the ip address of the device.
     * @return
     */
    public String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces= NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()){
                NetworkInterface intf = interfaces.nextElement();
                Enumeration<InetAddress> addresses= intf.getInetAddresses();

                while(addresses.hasMoreElements()){
                    InetAddress address= addresses.nextElement();
                    if(address.isLoopbackAddress() && address instanceof Inet4Address) return address.getHostAddress();
                }


            }

        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * this method allows us to do networking
     * @return
     */
    public Retrofit getClientNetworking(){
        if(retrofit!= null) return retrofit;

        OkHttpClient ok= new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit= new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(ok)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    /**
     * This method allows to know whether there is a connected user or not
     * @return
     */
    public boolean isLoggedIn(){
        return getConnectedUser()!= null;
    }

    /**
     * Will log the user out and take him on the login activity
     */
    public  void logout(){
        new Prefs(this, PreferenceType.PERMANENT).logout();
        Intent intent= new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(getConnectedUser()!= null){
            startService(new Intent(this, ConfigurationService.class)
                    .setAction(ConfigurationService.ACTION_CONFIG_DATA));
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public void login(User user) {
        //We need to call this any time we are logged the user in.
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.w(TAG, "getInstanceId failed", task.getException());
//                        return;
//                    }
//                    // Get new Instance ID token
//                    String token = task.getResult().getToken();
//
//                    //Let's check if the token is different from what we have in file.
//                    if(token!= null && !token.equals(account.getFcmToken())){
//                        sendTokenToServer(token);
//                    }
//
//                    startService(new Intent(this, ConfigurationService.class)
//                            .setAction(ConfigurationService.ACTION_USER_META_DATA_INFO));
//                });

        setConnectedUser(user);
    }

    private void sendTokenToServer(String token) {
//        Account account= MyApp.getInstance().getConnectedAccount();
//        account.setFcmToken(token);
//
//        MultipartBody.Part profilePart= null;
//
//        /*
//        If it contains http, that means it doesn't come for update
//        we need to send it to the server with null value.
//         */
//        if (account.getProfilePicture()!= null) {
//            if(!account.getProfilePicture().contains("http")){
//                File profileFile= new File(account.getProfilePicture());
//
//                RequestBody reqBody= RequestBody.create(MediaType.parse(getMimeType(Uri.fromFile(profileFile).toString())), profileFile);
//                profilePart= MultipartBody.Part.createFormData("profile", profileFile.getName(), reqBody);
//            }
//        }
//
//        ArrayList<MultipartBody.Part> documentParts = new ArrayList<>();
//
//        for(Document document:account.getDocuments()){
//            if (Utils.INSTANCE.isNotEmpty(document.getImagePath())
//                    && !document.getImagePath().contains("http")) {
//                documentParts.add(createPart(document.getImagePath(), "documents"));
//            }
//        }
//
//        /*
//       Here we send the language of the device to the user.
//        */
//        HashMap<String, String> headers= new HashMap<>();
//        headers.put("accept-language", Locale.getDefault().toString());
//
//        RequestBody accountBody= RequestBody.create(MediaType.parse("application/json"), Utils.INSTANCE.getGson().toJson(account));
//
//        getClientNetworking().create(IAccountRequest.class).saveAccount(documentParts, profilePart, accountBody, headers).enqueue(new Callback<com.woolib.domain.dto.Response<Account>>() {
//            @Override
//            public void onResponse(Call<com.woolib.domain.dto.Response<Account>> call, retrofit2.Response<com.woolib.domain.dto.Response<Account>> response) {
//
//            }
//            @Override
//            public void onFailure(Call<com.woolib.domain.dto.Response<Account>> call, Throwable t) {
//
//            }
//        });

    }

    private String getMimeType(String url){
        String type= null;
//        String extension= Utils.INSTANCE.getFileExtensionFromUrl(url);
//        if(extension != null){
//            type= MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
        return type;
    }

    private MultipartBody.Part createPart(String url, String name){
        File file= new File(url);
        RequestBody reqBody= RequestBody.create(MediaType.parse(getMimeType(url)), file);
        MultipartBody.Part part= MultipartBody.Part.createFormData(name, file.getName(), reqBody);
        return part;
    }

    /**
     * To know whether the application is in background or not
     * @return
     */
    public boolean isAppInBackground(){
        return isInBackground;
    }


    /**
     * Return a user if there is one connected
     * @returnx
     */
    public User getConnectedUser() {
        prefs= new Prefs(this, PreferenceType.PERMANENT);
        return prefs.getRegisteredUser();
    }

    /**
     * Set a connected user
     * @param connectedUser
     */
    public void setConnectedUser(User connectedUser){
        //Initialize the customer session.
        prefs= new Prefs(this, PreferenceType.PERMANENT);
        prefs.setRegisteredUser(connectedUser);
    }


    /**
     * Return the list of game schedules
     * @return
     */
    public ArrayList<GameSchedule> getGameSchedules() {
        prefs= new Prefs(this, PreferenceType.PERMANENT);
        return prefs.getGameSchedules();
    }

    /**
     * Set the list of game schedules
     * @param gameSchedules
     */
    public void setGameSchedules(ArrayList<GameSchedule> gameSchedules){
        prefs= new Prefs(this, PreferenceType.PERMANENT);
        prefs.setGameSchedules(gameSchedules);
    }

    /**
     * Return the latest game result
     * @return
     */
    public GameResult getGameResult() {
        prefs= new Prefs(this, PreferenceType.PERMANENT);
        return prefs.getGameResult();
    }

    /**
     * Set the list of game schedules
     * @param gameResult
     */
    public void setGameResult(GameResult gameResult){
        prefs= new Prefs(this, PreferenceType.PERMANENT);
        prefs.setGameResult(gameResult);
    }


}
